package plub.plubserver.domain.recruit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.recruit.dto.RecruitDto.QuestionResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.RecruitResponse;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.repository.RecruitRepository;

import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final AccountService accountService;

    private Recruit findById(Long recruitId) {
        return recruitRepository.findById(recruitId).orElseThrow();
    }

    public RecruitResponse getRecruit(Long recruitId) {
        return RecruitResponse.of(findById(recruitId));
    }

    public List<QuestionResponse> getRecruitQuestions(Long recruitId) {
        return QuestionResponse.ofList(findById(recruitId).getRecruitQuestionList());
    }

    public void doneRecruit(Long recruitId) {
        Recruit recruit = findById(recruitId);
        recruit.done();
    }

//    public void applyRecruit(Long recruitId) {
//        Account account = accountService.getCurrentAccount();
//        Recruit recruit = findById(recruitId);
//        recruit.
//        recruit.apply();
//    }

}
