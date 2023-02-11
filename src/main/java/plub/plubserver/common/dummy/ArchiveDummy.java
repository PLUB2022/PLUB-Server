package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.archive.dto.ArchiveDto;
import plub.plubserver.domain.archive.repository.ArchiveRepository;
import plub.plubserver.domain.archive.service.ArchiveService;

import java.util.List;

import static plub.plubserver.common.dummy.DummyImage.PLUB_MAIN_LOGO;
import static plub.plubserver.common.dummy.DummyImage.PLUB_PROFILE_TEST;

@Slf4j
@Transactional
@Component("archiveDummy")
@DependsOn("plubbingDummy")
@RequiredArgsConstructor
public class ArchiveDummy implements ApplicationRunner {

    private final AccountService accountService;
    private final ArchiveService archiveService;
    private final ArchiveRepository archiveRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (archiveRepository.count() > 0) {
            log.info("[4] 아카이브가 존재하여 더미를 생성하지 않았습니다.");
            return;
        }
        Account admin1 = accountService.getAccountByEmail("admin1");
        for (int i = 0; i < 10; i++) {
            ArchiveDto.ArchiveRequest archiveRequest = new ArchiveDto.ArchiveRequest(
                    "테스트 아카이브" + i,
                    List.of(PLUB_MAIN_LOGO, PLUB_MAIN_LOGO, PLUB_PROFILE_TEST)
            );
            archiveService.createArchive(admin1, 1L, archiveRequest);
        }
        log.info("[4] 아카이브 더미 생성 완료 - 영속성 컨텍스트 lazy 예외 때문에 스프링 빈이 아닌, ApplicationRunner 구현체로 생성.");
    }
}
