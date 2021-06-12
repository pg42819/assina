package eu.assina.app.csc;
import eu.assina.app.api.controller.CredentialControllerTest;
import eu.assina.app.api.service.CredentialServiceTest;
import eu.assina.app.csc.controller.CSCCredentialControllerTest;
import eu.assina.app.csc.controller.CSCInfoControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CSCCredentialControllerTest.class, CSCInfoControllerTest.class
})
public class CSCSuite {
}
