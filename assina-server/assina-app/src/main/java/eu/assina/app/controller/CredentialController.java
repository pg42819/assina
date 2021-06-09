package eu.assina.app.controller;

import eu.assina.app.model.AssinaCredential;
import eu.assina.app.services.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/certs")
public class CredentialController
{
	private CredentialService cryptoService;

	public CredentialController(@Autowired final CredentialService cryptoService)
	{
		this.cryptoService = cryptoService;
	}

	@GetMapping
	public Page<AssinaCredential> findPaginated(Pageable pageable)
	{
		return cryptoService.getCredentials(pageable);
	}

	@GetMapping("/{owner}")
	public Page<AssinaCredential> getCertificatesByOwner(@PathVariable(value = "owner") String owner, Pageable pageable)
	{
		return cryptoService.getCredentialsByOwner(owner, pageable);
	}

	@PostMapping("/{owner}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<AssinaCredential> createCredential(@PathVariable(value = "owner") String owner)
	{
		final AssinaCredential credential = cryptoService.createCredential(owner, owner);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(credential.getId())
				.toUri();
		final ResponseEntity.BodyBuilder responseEntityBuilder = ResponseEntity.created(location);
		return responseEntityBuilder.body(credential);
	}

	@DeleteMapping("/{credentialId}")
	public void deleteCredential(@PathVariable(value = "credentialId") String credentialId)
	{
		cryptoService.deleteCredentials(credentialId);
	}
}
