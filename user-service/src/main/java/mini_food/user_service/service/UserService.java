package mini_food.user_service.service;

import mini_food.user_service.entity.User;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 08/04/2026 - 7:14 PM
 * @project eureka-server
 * @package mini_food.user_service.service
 */
public interface UserService {
    User findByUsername(String username);

    User findByEmail(String email);

    User findById(Long id);

    User save(User user);

    String verify(String username, String password);

    String verifyWithoutAuth(String username);

    List<User> findAll();
}
