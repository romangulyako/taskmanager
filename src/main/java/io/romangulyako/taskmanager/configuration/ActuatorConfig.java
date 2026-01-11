package io.romangulyako.taskmanager.configuration;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ActuatorConfig {
    @Bean
    public InfoContributor customInfoContributor() {
        return builder -> {
            Map<String, Object> appInfo = Map.of(
                    "name", "Task Manager",
                    "description", "Task Management Application",
                    "version", "1.0.0"
            );
            builder.withDetails(appInfo);
        };
    }
}
