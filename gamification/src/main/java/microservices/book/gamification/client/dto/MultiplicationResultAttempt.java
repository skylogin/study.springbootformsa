package microservices.book.gamification.client.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * User가 곱셈을 푼 답안을 정의한 클래스
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@JsonDeserialize
public final class MultiplicationResultAttempt {

    private final String userAlias;
    private final int multiplicationFactorA;
    private final int multiplicationFactorB;
    private final int resultAttempt;

    private final boolean correct;

    // JSON (역)직렬화 및 JPA를 위한 빈 생성자
    public MultiplicationResultAttempt() {
        this.userAlias = null;
        this.multiplicationFactorA = -1;
        this.multiplicationFactorB = -1;
        this.resultAttempt = -1;
        this.correct = false;
    }
}