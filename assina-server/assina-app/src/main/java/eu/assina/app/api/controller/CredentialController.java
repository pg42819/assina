package eu.assina.app.api.controller;

import eu.assina.app.api.model.AssinaCredential;
import eu.assina.app.api.model.AuthProvider;
import eu.assina.app.api.model.User;
import eu.assina.app.api.payload.CredentialSummary;
import eu.assina.app.api.services.CredentialService;
import eu.assina.app.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Provider;

@RestController
@RequestMapping(value = "/credentials")
public class CredentialController
{
	private CredentialService cryptoService;
	private UserService userService;

	public CredentialController(@Autowired final CredentialService cryptoService,
						        @Autowired final UserService userService)
	{
		this.cryptoService = cryptoService;
		this.userService = userService;
	}

	@GetMapping
	public Page<CredentialSummary> getCredentialsPaginated(Pageable pageable)
	{
		return cryptoService.getCredentials(pageable).map(this::summarize);
	}

	@GetMapping("/{owner}")
	public Page<CredentialSummary> getCredentialsByOwner(@PathVariable(value = "owner") String owner,
														Pageable pageable)
	{
		return cryptoService.getCredentialsByOwner(owner, pageable).map(this::summarize);
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

	/**
	 * Functional method to convert a credential into a summary for returning
	 **/
	private CredentialSummary summarize(AssinaCredential cred) {
	    String ownerId = cred.getOwner();
		User user = userService.getUserById(ownerId)
							.orElse(new User(ownerId, "unknown", "unknown", AuthProvider.local));
		return new CredentialSummary(cred.getId(), user.getUsername(), user.getName(), cred.getCreatedAt(), cred.getCertificate().getType(), cred.getDescription());
	}
}
