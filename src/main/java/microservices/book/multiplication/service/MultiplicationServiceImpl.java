package microservices.book.multiplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

  @Autowired
  private RandomGeneratorService randomGeneratorService;

  public MultiplicationServiceImpl(final RandomGeneratorService randomGeneratorService) {
    this.randomGeneratorService = randomGeneratorService;
  }

  @Override
  public Multiplication createRandomMultiplication() {
    int factorA = randomGeneratorService.generateRandomFactor();
    int factorB = randomGeneratorService.generateRandomFactor();
    return new Multiplication(factorA, factorB);
  }

  @Override
  public boolean checkAttempt(final MultiplicationResultAttempt attempt) {
    boolean correct = attempt.getResultAttempt() == attempt.getMultiplication().getFactorA()
        * attempt.getMultiplication().getFactorB();

    // 조작된 답안을 방지
    Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다!!");

    // 복사본을 만들고 correct 필드를 상황에 맞게 설정
    MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(attempt.getUser(),
        attempt.getMultiplication(), attempt.getResultAttempt(), correct);

    return correct;
  }
}