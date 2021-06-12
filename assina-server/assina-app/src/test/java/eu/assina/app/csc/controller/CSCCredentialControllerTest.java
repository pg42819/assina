package eu.assina.app.csc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.assina.app.csc.services.CSCCredentialsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

//@RunWith(SpringRunner.class)
//@WebMvcTest(CSCInfoController.class)
//@ContextConfiguration
//@ContextConfiguration(classes= {SecurityConfig.class, CustomUserDetailsService.class, UserRepository.class})
@AutoConfigureMockMvc
@SpringBootTest
public class CSCCredentialControllerTest {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CSCCredentialsService credentialsService;

  @Test
  @WithMockUser("anyone")
  public void listCrededntials_200Returned() throws Exception {
//    when(credentialsService.getCredentialsInfo().thenReturn(INFO);
//
//    this.mockMvc.perform(post("/info/"))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().json(asJson(INFO)));
  }

  private String asJson(final Object object) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(object);
  }
}
