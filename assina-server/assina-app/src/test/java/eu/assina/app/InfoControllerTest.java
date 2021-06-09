package eu.assina.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.assina.app.config.CSCProperties;
import eu.assina.app.csc.controller.CSCInfoController;
import eu.assina.app.csc.services.CSCInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@WebMvcTest(CSCInfoController.class)
@ContextConfiguration
//@ContextConfiguration(classes= {SecurityConfig.class, CustomUserDetailsService.class})
//@SpringBootTest
class InfoControllerTest {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CSCInfoService infoService;

  @Autowired
  CSCProperties cscProperties;

  @Test
  public void retrieveInfo_200Returned() throws Exception {
    when(infoService.getInfo()).thenReturn(cscProperties.getInfo());

    this.mockMvc.perform(post("/info/"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(asJson(cscProperties.getInfo())));
  }

  private String asJson(final Object object) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(object);
  }
}
