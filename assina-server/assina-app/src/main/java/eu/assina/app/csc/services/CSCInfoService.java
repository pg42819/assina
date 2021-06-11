package eu.assina.app.csc.services;

import eu.assina.app.common.config.CSCProperties;
import eu.assina.app.csc.model.CSCInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CSCInfoService {
	private CSCProperties cscProperties;

	@Autowired
	public CSCInfoService(CSCProperties cscProperties) {
		this.cscProperties = cscProperties;
	}

	public CSCInfo getInfo() {
		// TODO some of the info is from properties but some of it is dynamic domains edtc
	    return cscProperties.getInfo();
	}
}
