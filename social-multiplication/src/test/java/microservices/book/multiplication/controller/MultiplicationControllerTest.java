package microservices.book.multiplication.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import microservices.book.multiplication.controller.MultiplicationController;
import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.service.MultiplicationService;

@RunWith(SpringRunner.class)
@WebMvcTest(MultiplicationController.class)
public class MultiplicationControllerTest {

  // Controller를 테스트하기 위해 진짜 Bean(MultiplicationServiceImpl)을 가져오는것이 아님.
  // 테스트케이스에서 given에 넘어오는 값만 셋팅됨
  @MockBean
  private MultiplicationService multiplicationService;

  @Autowired
  private MockMvc mvc;

  // 이 객체는 initFields() 메서드를 통해 자동으로 초기화
  private JacksonTester<Multiplication> json;

  @Before
  public void setUp() {
    JacksonTester.initFields(this, new ObjectMapper());
  }

  @Test
  public void getRandomMultiplicationTest() throws Exception {
    // given
    given(multiplicationService.createRandomMultiplication()).willReturn(new Multiplication(70, 20));

    // when
    MockHttpServletResponse response = mvc.perform(get("/multiplications/random").accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    // then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isEqualTo(json.write(new Multiplication(70, 20)).getJson());
  }
}