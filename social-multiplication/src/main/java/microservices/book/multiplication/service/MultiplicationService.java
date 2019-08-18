package microservices.book.multiplication.service;

import java.util.List;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;

public interface MultiplicationService {

  /**
   * 두개의 무작위 인수를 담은 (@link Multiplication) 객체를 생성한다. 무작위로 생성되는 숫자의 범위는 11 ~ 99
   * 
   * @return 무작위 인수를 담은 (@link Multiplication) 객체
   */
  public Multiplication createRandomMultiplication();

  /**
   * @return 곱셈 계산 결과가 맞으면 true, 아니면 false
   */
  public boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);

  /**
   * @return 5개의 결과를 되돌려주는 메서드
   */
  public List<MultiplicationResultAttempt> getStatsForUser(String userAlias);

  /**
   * @return 결과값을 되돌력 주는 메서드
   */
  public MultiplicationResultAttempt getMultiplication(Long resultId);
}