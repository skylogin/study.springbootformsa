package microservices.book.multiplication.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.event.EventDispatcher;
import microservices.book.multiplication.event.MultiplicationSolvedEvent;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

  private RandomGeneratorService randomGeneratorService;
  private MultiplicationResultAttemptRepository attemptRepository;
  private UserRepository userRepository;
  private EventDispatcher eventDispatcher;

  @Autowired
  public MultiplicationServiceImpl(final RandomGeneratorService randomGeneratorService,
      final MultiplicationResultAttemptRepository attemptRepository, 
      final UserRepository userRepository, 
      final EventDispatcher eventDispatcher) {
    this.randomGeneratorService = randomGeneratorService;
    this.attemptRepository = attemptRepository;
    this.userRepository = userRepository;
    this.eventDispatcher = eventDispatcher;
  }

  @Override
  public Multiplication createRandomMultiplication() {
    int factorA = randomGeneratorService.generateRandomFactor();
    int factorB = randomGeneratorService.generateRandomFactor();
    return new Multiplication(factorA, factorB);
  }

  @Transactional
  @Override
  public boolean checkAttempt(final MultiplicationResultAttempt attempt) {

    // 해당 닉네임의 사용자가 존재하는지 확인
    Optional<User> user = userRepository.findByAlias(attempt.getUser().getAlias());

    // 도전과제(p69) - 같은 곱셈 문제가 있다면 저장하지 않는다.
    // List<MultiplicationResultAttempt> userAttempt = attemptRepository
    // .findByUserAliasAndMultiplicationFactorAAndMultiplicationFactorB(user.orElse(attempt.getUser()).getAlias(),
    // attempt.getMultiplication().getFactorA(),
    // attempt.getMultiplication().getFactorB());

    // System.out.println("########################################");
    // System.out.println(user.orElse(attempt.getUser()).getAlias());
    // System.out.println(attempt.getMultiplication().getFactorA());
    // System.out.println(attempt.getMultiplication().getFactorB());
    // System.out.println(userAttempt.size());
    // System.out.println(userAttempt.isEmpty());
    // System.out.println(!userAttempt.isEmpty());
    // System.out.println("@@@");
    // System.out.println(attempt.isCorrect());
    // System.out.println(!attempt.isCorrect());
    // System.out.println("########################################");

    // Assert.isTrue(!userAttempt.isEmpty(), "기존에 풀었던 문제입니다!!");
    // 조작된 답안을 방지
    Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다!!");

    // 답안 채점
    boolean isCorrect = attempt.getResultAttempt() == attempt.getMultiplication().getFactorA()
        * attempt.getMultiplication().getFactorB();

    // 복사본을 만들고 correct 필드를 상황에 맞게 설정
    MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(
      user.orElse(attempt.getUser()),
      attempt.getMultiplication(), 
      attempt.getResultAttempt(), 
      isCorrect
    );

    // 답안 저장
    attemptRepository.save(checkedAttempt);


    // 이벤트로 결과를 전송
    eventDispatcher.send(new MultiplicationSolvedEvent(checkedAttempt.getId(), checkedAttempt.getUser().getId(), checkedAttempt.isCorrect()));

    return isCorrect;
  }

  @Override
  public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
    return attemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
  }

}