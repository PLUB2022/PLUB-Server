package plub.plubserver.domain.calendar.service;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.model.CalendarAttend;
import plub.plubserver.domain.notification.dto.NotificationDto;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.util.CronExpressionGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SchedulerService {


    private final ThreadPoolTaskScheduler scheduler;
    private final NotificationService notificationService;

    public SchedulerService(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.scheduler = new ThreadPoolTaskScheduler();
        this.scheduler.initialize();
    }

    private final Map<Calendar, ThreadPoolTaskScheduler> schedulerMap = new ConcurrentHashMap<>();

    public ThreadPoolTaskScheduler getScheduler() {
       return scheduler;
    }

    public void addScheduler(Calendar calendar, ThreadPoolTaskScheduler scheduler) {
        schedulerMap.put(calendar, scheduler);
        LocalDateTime schedulerTime = parseSchedulerTime(calendar);
        scheduler.schedule(getRunnable(calendar), getTrigger(schedulerTime));
    }

    public LocalDateTime parseSchedulerTime(Calendar calendar) {
        LocalDateTime schedulerTime = LocalDateTime.parse(calendar.getStartedAt()+ "T" + calendar.getStartTime());
        LocalDateTime alarmTime = calendar.getAlarmType().getAlarmTime(schedulerTime);
        return alarmTime;
    }

    public void removeScheduler(Calendar calendar) {
        schedulerMap.remove(calendar);
    }

    private Runnable getRunnable(Calendar calendar) {

        Plubbing plubbing = calendar.getPlubbing();
        List<CalendarAttend> attendList = calendar.getCalendarAttendList();
        if (attendList == null || attendList.isEmpty()) {
            return () -> {
                // 삭제 로직
                removeScheduler(calendar);
            };
        }
        return () -> {
            // 알람 로직
            attendList.forEach(member -> {
                NotificationDto.NotifyParams params = NotificationDto.NotifyParams.builder()
                        .receiver(member.getAccount())
                        .type(NotificationType.CREATE_UPDATE_CALENDAR)
                        .redirectTargetId(calendar.getId())
                        .title(plubbing.getName())
                        .content("곧 일정이 시작됩니다!\n : " + calendar.getTitle() + "," + calendar.getStartedAt() + " ~ " + calendar.getEndedAt() + "," + calendar.getPlaceName())
                        .build();
                notificationService.pushMessage(params);
            });

            // 삭제 로직
            removeScheduler(calendar);
        };
    }

    private Trigger getTrigger(LocalDateTime schedulerTime) {
        String cronExpression = CronExpressionGenerator.generate(schedulerTime);
        return new CronTrigger(cronExpression);
    }
}
