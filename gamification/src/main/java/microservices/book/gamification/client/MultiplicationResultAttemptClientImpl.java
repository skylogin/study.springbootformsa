package microservices.book.gamification.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import microservices.book.gamification.client.dto.MultiplicationResultAttempt;

/**
 * Multiplication 마이크로서비스와 REST로 연결하기 위한
 * MultiplicationResultAttemptClient 인터페이스의 구현체
 */
@Component
public class MultiplicationResultAttemptClientImpl implements MultiplicationResultAttemptClient{
    private final RestTemplate restTemplate;
    private final String multiplicationHost;

    @Autowired
    public MultiplicationResultAttemptClientImpl(final RestTemplate restTemplate,
            @Value("${multiplicationHost}") final String multiplicationHost) {
        this.restTemplate = restTemplate;
        this.multiplicationHost = multiplicationHost;
    }

    @Override
    public MultiplicationResultAttempt retrieveMultiplicationResultAttemptById(final Long multiplicationResultAttemptId) {
		return restTemplate.getForObject(multiplicationHost + "/results/" + multiplicationResultAttemptId, MultiplicationResultAttempt.class);
	}
}