package eu.assina.app.csc.services;

import eu.assina.app.csc.payload.CSCSignaturesSignHashRequest;
import eu.assina.app.csc.payload.CSCSignaturesSignHashResponse;
import eu.assina.app.csc.payload.CSCSignaturesTimestampRequest;
import eu.assina.app.csc.payload.CSCSignaturesTimestampResponse;
import org.springframework.stereotype.Service;

@Service
public class CSCSignaturesService {

	public CSCSignaturesService() {
	}

	public CSCSignaturesSignHashResponse signHash(CSCSignaturesSignHashRequest signHashRequest) {
		throw new IllegalStateException("Not Yet Implemented"); // TODO implement this
	}

	public CSCSignaturesTimestampResponse generateTimestamp(CSCSignaturesTimestampRequest timestampRequest) {
		throw new IllegalStateException("Not Yet Implemented"); // TODO implement this
	}
}
