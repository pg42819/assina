package eu.assina.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.assina.app.controller.CredentialController;
import eu.assina.app.error.AssinaError;
import eu.assina.app.model.AssinaCredential;
import eu.assina.app.services.CredentialService;
import eu.assina.crypto.cert.CertificateGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.security.KeyPair;
import java.security.cert.Certificate;
import java.util.Optional;

import static eu.assina.app.TestResponseMatches.validErrorResponse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO make this match section 11 of the CSC spec
//    https://stackoverflow.com/questions/50209302/spring-security-rest-unit-tests-fail-with-httpstatuscode-401-unauthorized
@WebMvcTest(value = CredentialController.class)
//    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
//    excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
//5/22/2021, 4:32:21 PM Deprecate 'secure' on WebMvcTest and AutoConfigureMockMvc · Issue #14227 · spring-projects/spring-boot
//    https://github.com/spring-projects/spring-boot/issues/14227#issuecomment-688824627
//@WebAppConfiguration
//@Import(value = {CredentialController.class, MockMvcAutoConfiguration.class})
//@EnableConfigurationProperties({ResourceProperties.class, WebMvcProperties.class})
class CredentialControllerTest {

  static final String USER = "test-subject";
  static final String CREDENTIAL_ID = "1234-5678";
  static AssinaCredential CREDENTIAL;

  private static String credPath() {
    return "/certs";
  }

  private static String credPath(String id) {
    return "/certs/" + id;
  }

  static {
    try {
      CertificateGenerator generator = new CertificateGenerator();
      final KeyPair keyPair = generator.generateKeyPair();
      final Certificate selfSignedCert = generator.createSelfSignedCert(keyPair, USER);
      CREDENTIAL = new AssinaCredential();
      CREDENTIAL.setId(CREDENTIAL_ID);
      CREDENTIAL.setCertificate(selfSignedCert);
      CREDENTIAL.setKeyPair(keyPair);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CredentialService credentialService;

  @Test
  @WithMockUser()
  public void retrieveCredentialById_idFound_200credentialReturned() throws Exception {
    when(credentialService.getCredentialWithId(CREDENTIAL.getId())).thenReturn(Optional.of(CREDENTIAL));

    this.mockMvc.perform(get(credPath(CREDENTIAL.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(asJson(CREDENTIAL)));
  }

  @Test
  @WithMockUser()
  public void retrieveCredentialById_idNotFound_404() throws Exception {
    when(credentialService.getCredentialWithId(CREDENTIAL.getId())).thenReturn(Optional.empty());
    this.mockMvc.perform(get(credPath(CREDENTIAL.getId())))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(validErrorResponse(AssinaError.CredentialNotFound));
  }

  @Test
  public void deleteCredential_idFound_204() throws Exception {
    this.mockMvc.perform(delete(credPath(CREDENTIAL.getId())))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  public void createCredential_201WithLocationHeaderAndCredentialReturned() throws Exception {
    when(credentialService.createCredential(USER, USER)).thenReturn(CREDENTIAL);
    final MockHttpServletRequestBuilder requestBuilder =
        post(credPath())
            .content(asJson(CREDENTIAL));

    this.mockMvc.perform(
        requestBuilder)
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().string("location", "http://localhost" + credPath(CREDENTIAL.getId())))
        .andExpect(content().json(asJson(CREDENTIAL)));
  }

  private static String asJson(final AssinaCredential credential) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(credential);
  }
}
