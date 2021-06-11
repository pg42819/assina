package eu.assina.app.csc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.assina.app.csc.model.CSCInfo;
import eu.assina.app.csc.services.CSCInfoService;
import eu.assina.app.util.MockCSCInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringRunner.class)
//@WebMvcTest(CSCInfoController.class)
//@ContextConfiguration
//@ContextConfiguration(classes= {SecurityConfig.class, CustomUserDetailsService.class, UserRepository.class})
@AutoConfigureMockMvc
@SpringBootTest
class InfoControllerTest {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static final CSCInfo INFO = new MockCSCInfo();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CSCInfoService infoService;

  @Test
  @WithMockUser("duke")
  public void retrieveInfo_200Returned() throws Exception {
    when(infoService.getInfo()).thenReturn(INFO);

    this.mockMvc.perform(post("/info/"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(asJson(INFO)));
  }

  private String asJson(final Object object) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(object);
  }
}
