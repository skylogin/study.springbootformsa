package microservices.book.multiplication.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * {@link User}가 {@link Multiplication}을 계산한 답안을 정의한 클래스
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public final class MultiplicationResultAttempt {
  private final User user;
  private final Multiplication multiplication;
  private final int resultAttempt;

  private final boolean correct;

  // JSON (역)직렬화를 위한 빈 생성자
  public MultiplicationResultAttempt() {
    this.user = null;
    this.multiplication = null;
    this.resultAttempt = -1;
    this.correct = false;
  }
}