package mini_food.user_service.service.impl;

import java.util.List;
import mini_food.user_service.entity.User;
import mini_food.user_service.repository.UserRepository;
import mini_food.user_service.service.UserEventPublisher;
import mini_food.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserEventPublisher userEventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            JwtService jwtService,
            UserEventPublisher userEventPublisher
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userEventPublisher = userEventPublisher;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username da ton tai");
        }
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email da ton tai");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        User createdUser = userRepository.save(user);
        userEventPublisher.publishUserRegistered(createdUser);
        return createdUser;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public String verify(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            LOG.error("User not authenticated");
            throw new IllegalArgumentException("Sai tai khoan hoac mat khau");
        }
        LOG.info("User authenticated");
        return jwtService.generateToken(username);
    }

    public String verifyWithoutAuth(String username) {
        return jwtService.generateToken(username);
    }
}
