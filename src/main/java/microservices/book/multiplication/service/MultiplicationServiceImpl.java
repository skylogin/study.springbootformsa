package microservices.book.multiplication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

  @Autowired
  private RandomGeneratorService randomGeneratorService;

  @Autowired
  private MultiplicationResultAttemptRepository attemptRepository;

  @Autowired
  private UserRepository userRepository;

  public MultiplicationServiceImpl(final RandomGeneratorService randomGeneratorService,
      final MultiplicationResultAttemptRepository attemptRepository, final UserRepository userRepository) {
    this.randomGeneratorService = randomGeneratorService;
    this.attemptRepository = attemptRepository;
    this.userRepository = userRepository;
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

    // 조작된 답안을 방지
    Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다!!");

    // 답안 채점
    boolean isCorrect = attempt.getResultAttempt() == attempt.getMultiplication().getFactorA()
        * attempt.getMultiplication().getFactorB();

    // 복사본을 만들고 correct 필드를 상황에 맞게 설정
    MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(user.orElse(attempt.getUser()),
        attempt.getMultiplication(), attempt.getResultAttempt(), isCorrect);

    // 답안 저장
    attemptRepository.save(checkedAttempt);

    return isCorrect;
  }
}