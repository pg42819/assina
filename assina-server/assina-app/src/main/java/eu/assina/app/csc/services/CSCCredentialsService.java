package eu.assina.app.csc.services;

import eu.assina.app.csc.payload.CSCCredentialsInfoRequest;
import eu.assina.app.csc.payload.CSCCredentialsInfoResponse;
import eu.assina.app.csc.payload.CSCCredentialsListRequest;
import eu.assina.app.csc.payload.CSCCredentialsListResponse;
import org.springframework.stereotype.Service;

@Service
public class CSCCredentialsService {

	public CSCCredentialsService() {
	}

	public CSCCredentialsListResponse listCredentials(CSCCredentialsListRequest listRequest) {
	    throw new IllegalStateException("Not Yet Implemented"); // TODO implement this
	}

	public CSCCredentialsInfoResponse getCredentialsInfo(CSCCredentialsInfoRequest infoRequest) {
		throw new IllegalStateException("Not Yet Implemented");
	}
}
