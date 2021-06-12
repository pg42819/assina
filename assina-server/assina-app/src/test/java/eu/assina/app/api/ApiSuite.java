package eu.assina.app.api;
import eu.assina.app.api.controller.CredentialControllerTest;
import eu.assina.app.api.service.CredentialServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CredentialControllerTest.class, CredentialServiceTest.class
})
public class ApiSuite {
}
