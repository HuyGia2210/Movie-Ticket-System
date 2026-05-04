package mini_food.user_service.repository;

import mini_food.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Le Tran Gia Huy
 * @created 08/04/2026 - 6:54 PM
 * @project eureka-server
 * @package mini_food.user_service.repository
 */
public interface UserRepository extends JpaRepository<User, Long> {
     User findByUsername(String username);
     User findByEmail(String email);
}
