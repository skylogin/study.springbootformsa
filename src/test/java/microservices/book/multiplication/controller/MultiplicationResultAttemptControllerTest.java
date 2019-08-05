package microservices.book.multiplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.service.MultiplicationService;

@RunWith(SpringRunner.class)
@WebMvcTest(MultiplicationResultAttemptController.class)
public class MultiplicationResultAttemptControllerTest {

  @MockBean
  private MultiplicationService multiplicationService;

  @Autowired
  private MockMvc mvc;

  // 이 객체는 initFields() 메서드를 이용하여 자동으로 초기화
  private JacksonTester<MultiplicationResultAttempt> jsonResultAttempt;
  private JacksonTester<List<MultiplicationResultAttempt>> jsonResultAttemptList;

  @Before
  public void setUp() {
    JacksonTester.initFields(this, new ObjectMapper());
  }

  @Test
  public void postResultReturnCorrect() throws Exception {
    genericParameterizedTest(true);
  }

  @Test
  public void postResultReturnNotCorrect() throws Exception {
    genericParameterizedTest(false);
  }

  public void genericParameterizedTest(final boolean correct) throws Exception {
    // given (서비스를 테스트하는 것이 아니기때문에 MockBean에서 기대값을 리턴)
    given(multiplicationService.checkAttempt(any(MultiplicationResultAttempt.class))).willReturn(correct);

    User user = new User("John");
    Multiplication multiplication = new Multiplication(50, 70);
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3500, false);

    // when
    MockHttpServletResponse response = mvc.perform(
        post("/results").contentType(MediaType.APPLICATION_JSON).content(jsonResultAttempt.write(attempt).getJson()))
        .andReturn().getResponse();

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString())
        .isEqualTo(jsonResultAttempt.write(new MultiplicationResultAttempt(attempt.getUser(),
            attempt.getMultiplication(), attempt.getResultAttempt(), correct)).getJson());
  }

  @Test
  public void getUserStats() throws Exception {
    // given
    User user = new User("John_doe");
    Multiplication multiplication = new Multiplication(50, 70);
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3500, true);
    List<MultiplicationResultAttempt> recentAttempts = Lists.newArrayList(attempt, attempt);
    given(multiplicationService.getStatsForUser("John_doe")).willReturn(recentAttempts);

    // when
    MockHttpServletResponse response = mvc.perform(get("/results").param("alias", "John_doe")).andReturn()
        .getResponse();

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isEqualTo(jsonResultAttemptList.write(recentAttempts).getJson());
  }
}