package microservices.book.multiplication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
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
import microservices.book.multiplication.event.EventDispatcher;
import microservices.book.multiplication.event.MultiplicationSolvedEvent;
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

  @Mock
  private EventDispatcher eventDispatcher;

  private MultiplicationService multiplicationServiceImpl;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    multiplicationServiceImpl = new MultiplicationServiceImpl(
      randomGeneratorService, 
      attemptRepository,
      userRepository,
      eventDispatcher
    );
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
    MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(attempt.getId(), attempt.getUser().getId(),
        attempt.isCorrect());
    given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

    // when
    boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isTrue();
    verify(attemptRepository).save(verifiedAttempt);
    verify(eventDispatcher).send(eq(event));
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
    MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(attempt.getId(), attempt.getUser().getId(),
        attempt.isCorrect());
    given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

    // when
    boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isFalse();
    verify(attemptRepository).save(attempt);
    verify(eventDispatcher).send(eq(event));
  }

  // @Test
  // public void checkSameAttemptTest() {
  // // given
  // Multiplication multiplication = new Multiplication(50, 60);
  // User user = new User("John_doe");
  // MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user,
  // multiplication, 3000, false);
  // given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

  // // when
  // boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);
  // boolean attemptResult2 = multiplicationServiceImpl.checkAttempt(attempt);

  // assert
  // assertThat(attemptResult).
  // }

  @Test
  public void retrieveStatsTest() {
    // given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("John_doe");
    MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
    MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user, multiplication, 3051, false);
    List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(attempt1, attempt2);
    given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());
    given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("John_doe")).willReturn(latestAttempts);

    // when
    List<MultiplicationResultAttempt> latestAttemptsResult = multiplicationServiceImpl.getStatsForUser("John_doe");

    // then
    assertThat(latestAttemptsResult).isEqualTo(latestAttempts);
  }

  // @Test
  // public void checkResultAttempt(){
  //   // given
  //   Multiplication multiplication = new Multiplication(50, 60);
  //   User user = new User("John_doe");
  //   MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);
  //   List<MultiplicationResultAttempt> resultAttempt = Lists.newArrayList(attempt);

  //   // when
  //   List<MultiplicationResultAttempt> result = multiplicationServiceImpl.getMultiplication(attempt.getId());

  //   // then
  //   assertThat(result).isEqualTo(resultAttempt);

  // }

}