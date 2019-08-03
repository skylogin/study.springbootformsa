package microservices.book.multiplication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MultiplicationServiceImplTest {

  @Mock
  private RandomGeneratorService randomGeneratorService;

  @Mock
  private MultiplicationResultAttemptRepository attemptRepository;

  @Mock
  private UserRepository userRepository;

  private MultiplicationService multiplicationServiceImpl;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    multiplicationServiceImpl = new MultiplicationServiceImpl(randomGeneratorService, attemptRepository,
        userRepository);
  }

  @Test
  public void createRandomMultiplicationTest() {
    // given (randomGeneratorService가 처음에 50, 나중에 30을 반환하도록 설정)
    given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

    // when
    Multiplication multiplication = multiplicationServiceImpl.createRandomMultiplication();

    // assert
    assertThat(multiplication.getFactorA()).isEqualTo(50);
    assertThat(multiplication.getFactorB()).isEqualTo(30);

  }

  @Test
  public void checkCorrectAttemptTest() {
    // given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("John_doe");
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);
    MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, true);
    given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

    // when
    boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isTrue();
    verify(attemptRepository).save(verifiedAttempt);
    // given에서는 사용자가 악의적으로 false로 보내는 케이스가 attempt에 담긴다.
    // 하지만 when에서의 checkAttempt 메서드 수행이후 실제 값이 변경된다.
    // 저장되는 값에 대해서는 true와 비교하여 verify(검증) 한다.
  }

  @Test
  public void checkWrongAttemptTest() {
    // given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("John_doe");
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);
    given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

    // when
    boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isFalse();
    verify(attemptRepository).save(attempt);
  }

}