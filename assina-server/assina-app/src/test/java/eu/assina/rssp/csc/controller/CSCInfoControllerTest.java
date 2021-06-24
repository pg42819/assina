package eu.assina.rssp.csc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.assina.rssp.csc.services.CSCInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(CSCInfoController.class)
//@ContextConfiguration
//@ContextConfiguration(classes= {SecurityConfig.class, CustomUserDetailsService.class, UserRepository.class})
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class CSCInfoControllerTest {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;


  @Autowired
  private CSCInfoService infoService;

  @Test
  @WithMockUser("anyone")
  public void retrieveInfo_200Returned() throws Exception {
    // TODO some of this info is dynamic so this test should be more dynamic
    this.mockMvc.perform(post("/csc/v1/info/"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is("Assina")));
  }

  private String asJson(final Object object) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(object);
  }
}
