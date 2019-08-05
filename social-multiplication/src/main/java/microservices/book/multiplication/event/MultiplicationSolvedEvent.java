package microservices.book.multiplication.event;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 시스템에서 {@link microservices.book.multiplication.domain.Multiplication}
 * 문제가 해결됐다는 사실을 모델링한 이벤트
 * 곱셈에 대한 컨텍스트 정보를 제공
 */

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MultiplicationSolvedEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final Long multiplicationResultAttemptId;
    private final Long userId;
    private final boolean correct;
    
}