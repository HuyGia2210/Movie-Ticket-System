package mini_food.gateway_server.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class CorsHeaderDedupeFilter {

    @Bean
    public GlobalFilter dedupeCorsHeaderFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> {
                dedupeHeader(exchange.getResponse().getHeaders(), "Access-Control-Allow-Origin");
                dedupeHeader(exchange.getResponse().getHeaders(), "Access-Control-Allow-Credentials");
                return Mono.empty();
            });
            return chain.filter(exchange);
        };
    }

    private void dedupeHeader(org.springframework.http.HttpHeaders headers, String headerName) {
        List<String> values = headers.get(headerName);
        if (values == null || values.size() <= 1) {
            return;
        }
        Set<String> uniqueValues = new LinkedHashSet<>(values);
        headers.put(headerName, List.copyOf(uniqueValues));
    }
}
