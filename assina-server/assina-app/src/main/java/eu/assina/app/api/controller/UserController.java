package eu.assina.app.api.controller;

import eu.assina.app.api.services.UserService;
import eu.assina.app.model.User;
import eu.assina.app.payload.UserIdentityAvailability;
import eu.assina.app.payload.UserProfile;
import eu.assina.app.security.CurrentUser;
import eu.assina.app.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userService.getUserById(userPrincipal.getId());
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
    public UserProfile getUserProfile(@PathVariable(value = "id") String id) {
        UserProfile userProfile = userService.getUserProfile(id);
        return userProfile;
    }

    @GetMapping("/users")
    public List<UserProfile> getUsers() {
        List<UserProfile> userProfiles = userService.getUserProfiles();
        return userProfiles;
    }
}
