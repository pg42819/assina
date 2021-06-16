package eu.assina.app.api.controller;

import eu.assina.app.api.model.User;
import eu.assina.app.api.payload.UserIdentityAvailability;
import eu.assina.app.api.payload.UserProfile;
import eu.assina.app.api.services.UserService;
import eu.assina.app.common.error.ApiException;
import eu.assina.app.common.error.AssinaError;
import eu.assina.app.security.CurrentUser;
import eu.assina.app.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AssinaUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        String id = userPrincipal.getId();
        User user = userService.getUserById(id).orElseThrow(
                () -> new ApiException(AssinaError.UserNotFound, "Failed to find user {}", id));
        return user;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return new UserIdentityAvailability(userService.isUsernameAvailable(username));
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        return new UserIdentityAvailability(userService.isEmailAvailable(email));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserProfile getUserProfile(@PathVariable(value = "id") String id) {
        UserProfile userProfile = userService.getUserProfile(id).orElseThrow(
                () -> new ApiException(AssinaError.UserNotFound, "Failed to find user {}", id));
        return userProfile;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserProfile> getUsersPaginated(Pageable pageable) {
        List<UserProfile> userProfiles = userService.getUserProfiles(pageable);
        return userProfiles;
    }
}
