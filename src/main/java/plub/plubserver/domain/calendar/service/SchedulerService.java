package plub.plubserver.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.repository.CalendarRepository;
import plub.plubserver.domain.notification.dto.NotificationDto;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SchedulerService {

    private final CalendarRepository calendarRepository;
    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;

    // DB에 저장된 모든 캘린더 정보를 가져와서 스케줄링
    @Scheduled(fixedRate = 60000) // 매분마다 실행 (적절한 주기로 변경 가능)
    @Transactional
    public void scheduleCalendars() {
        try{
            List<Calendar> calendars = calendarRepository.findAll();
            LocalDateTime now = LocalDateTime.now();

            for (Calendar calendar : calendars) {
                LocalDateTime schedulerTime = parseSchedulerTime(calendar);
                if (schedulerTime.isBefore(now)) {
                    // 스케줄 시간이 이미 지났으면 알림 전송 후 캘린더 삭제
                    sendNotificationAndRemoveCalendar(calendar);
                } else {
                    // 스케줄링
                    taskScheduler.schedule(() -> sendNotificationAndRemoveCalendar(calendar), Instant.from(schedulerTime));
                }
            }
        } catch (Exception e) {
            log.error("스케줄링 작업 중 예외 발생: " + e.getMessage(), e);
        }

    }

    private void sendNotificationAndRemoveCalendar(Calendar calendar) {
        Plubbing plubbing = calendar.getPlubbing();
        plubbing.getMembers().forEach(member -> {
            NotificationDto.NotifyParams params = NotificationDto.NotifyParams.builder()
                    .receiver(member)
                    .type(NotificationType.CREATE_UPDATE_CALENDAR)
                    .redirectTargetId(calendar.getId())
                    .title(plubbing.getName())
                    .content("곧 일정이 시작됩니다!\n : " + calendar.getTitle())
                    .build();
            notificationService.pushMessage(params);
        });
        calendarRepository.delete(calendar);
    }

    private LocalDateTime parseSchedulerTime(Calendar calendar) {
        LocalDateTime schedulerTime = LocalDateTime.parse(calendar.getStartedAt() + "T" + calendar.getStartTime());
        return calendar.getAlarmType().getAlarmTime(schedulerTime);
    }
}
