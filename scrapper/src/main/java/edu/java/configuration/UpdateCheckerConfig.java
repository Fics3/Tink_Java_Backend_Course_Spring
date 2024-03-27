package edu.java.configuration;

import edu.java.service.updateChecker.UpdateChecker;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateCheckerConfig {
    @Bean
    @Qualifier("updateCheckers")
    public Map<String, UpdateChecker> updateCheckers(List<UpdateChecker> updateCheckers) {
        return updateCheckers.stream()
            .collect(Collectors.toMap(UpdateChecker::getDomain, updateChecker -> updateChecker));
    }
}
