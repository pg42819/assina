package eu.assina.rssp.api.services;

import eu.assina.rssp.api.model.User;
import eu.assina.rssp.api.payload.UserProfile;
import eu.assina.rssp.repository.CredentialRepository;
import eu.assina.rssp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO rename to AssinaUserService
@Service
public class UserService {

    private UserRepository userRepository;
    private CredentialRepository credentialRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       CredentialRepository credentialRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isUsernameAvailable(String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return !isAvailable;
    }

    public boolean isEmailAvailable(String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return isAvailable;
    }

    public Optional<UserProfile> getUserProfile(String id) {
        return getUserById(id).map(this::profile);
    }

    public List<UserProfile> getUserProfiles(Pageable pageable) {
        List<UserProfile> profiles = userRepository.findAll(pageable).stream().map(this::profile)
                                             .collect(Collectors.toList());

        return profiles;
    }

    /**
     * Functional to wrap user in profile
     */
    private UserProfile profile(User user) {
        return new UserProfile(user.getId(), user.getName(), user.getName(), user.getCreatedAt(),
                credentialRepository.countByOwner(user.getId()));
    }
}
