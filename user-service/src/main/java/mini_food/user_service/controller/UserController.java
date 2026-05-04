package mini_food.user_service.controller;

import jakarta.servlet.http.HttpServletResponse;
import mini_food.user_service.entity.User;
import mini_food.user_service.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/users", "/api/users"})
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping({"/users/{id}", "/api/users/{id}"})
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping({"/users/by-username", "/api/users/by-username"})
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping({"/register", "/api/users/register"})
    public ResponseEntity<?> saveUser(
            @RequestBody RegisterRequest requestForm,
            HttpServletResponse response
    ) {
        User tempAcc = new User();
        tempAcc.setUsername(requestForm.getUsername());
        tempAcc.setPassword(requestForm.getPassword());
        tempAcc.setEmail(requestForm.getEmail());
        tempAcc.setRole(requestForm.getRole() == null || requestForm.getRole().isBlank() ? "USER" : requestForm.getRole());
        User createdAcc = userService.save(tempAcc);

        String token  = userService.verifyWithoutAuth(createdAcc.getUsername());
        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", token)
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(new AuthResponse(token, createdAcc));
    }

    @PostMapping({"/login", "/api/users/login"})
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        String token  = userService.verify(request.getUsername(), request.getPassword());
        User user = userService.findByUsername(request.getUsername());
        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", token)
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping({"/logout", "/api/users/logout"})
    public ResponseEntity<?> logout(HttpServletResponse response) {
        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", "")
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }

    static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class AuthResponse {
        private final String token;
        private final User user;

        public AuthResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }
}


