package eu.assina.app;

import eu.assina.app.error.AssinaError;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Utility methods for matching responses in controller tests
 */
public class TestResponseMatches {

  /**
   * Returns a matcher for asserting a JSON response as valid Assina standard error response.
   *
   * @return a result matcher for error responses
   */
  public static ResultMatcher validErrorResponse() {
    return validErrorResponse(null);
  }


  /**
   * Returns a matcher for asserting a JSON response as a Assina API error.
   *
   * @param expectedError the expected error enum; if null is passed, we assert that it is NOT null
   *
   * @return a result matcher for error responses
   */
  public static ResultMatcher validErrorResponse(AssinaError expectedError) {
    return ResultMatcher.matchAll(
        jsonPath("$.error.code", expectedError == null ? notNullValue() : is(expectedError.getCode())),
        jsonPath("$.error.desc", notNullValue())
    );
  }
}
