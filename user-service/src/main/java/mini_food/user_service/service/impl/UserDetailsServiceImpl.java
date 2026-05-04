package mini_food.user_service.service.impl;

import mini_food.user_service.entity.AppUserDetails;
import mini_food.user_service.entity.User;
import mini_food.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Le Tran Gia Huy
 * @created 08/04/2026 - 7:09 PM
 * @project eureka-server
 * @package mini_food.user_service.service.impl
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository theUserRepository) {
        userRepository = theUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User "+username+" not found");
        }

        return new AppUserDetails(user);
    }
}
