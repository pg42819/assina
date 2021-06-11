package eu.assina.app.api.services;

import eu.assina.app.error.ResourceNotFoundException;
import eu.assina.app.model.User;
import eu.assina.app.payload.UserProfile;
import eu.assina.app.repository.CredentialRepository;
import eu.assina.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public User getUserById(String id) {
        User user = userRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return user;

    }

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return user;
    }

    public boolean isUsernameAvailable(String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return !isAvailable;
    }

    public boolean isEmailAvailable(String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return isAvailable;
    }

    public UserProfile getUserProfile(String id) {
        User user = getUserById(id);
        long credCount = credentialRepository.countByOwner(id);
        UserProfile userProfile = new UserProfile(user.getId(), user.getName(), user.getName(),
                user.getCreatedAt(), credCount);

        return userProfile;
    }

    public List<UserProfile> getUserProfiles() {
        List<UserProfile> profiles = userRepository.findAll().stream().map(user ->
            new UserProfile(user.getId(), user.getName(), user.getName(),
                user.getCreatedAt(), credentialRepository.countByOwner(user.getId())))
                                             .collect(Collectors.toList());

        return profiles;
    }

}
