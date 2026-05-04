package mini_food.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import mini_food.user_service.entity.User;
import mini_food.user_service.event.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String userRegisteredTopic;

    public UserEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topic.user-registered}") String userRegisteredTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userRegisteredTopic = userRegisteredTopic;
    }

    public void publishUserRegistered(User user) {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setUserId(user.getId());
        event.setUsername(user.getUsername());
        event.setEmail(user.getEmail());
        event.setRegisteredAt(LocalDateTime.now());
        try {
            kafkaTemplate.send(
                    userRegisteredTopic,
                    String.valueOf(user.getId()),
                    objectMapper.writeValueAsString(event)
            );
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Khong the serialize USER_REGISTERED event", ex);
        }
    }
}
