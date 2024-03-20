package edu.java.bot.configuration;

import edu.java.bot.service.ScrapperService;
import edu.java.bot.service.commands.CommandService;
import edu.java.bot.service.commands.resourcesHandlers.ChainResourceHandler;
import edu.java.bot.service.commands.resourcesHandlers.GitHubHandler;
import edu.java.bot.service.commands.resourcesHandlers.StackOverflowHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommandConfig {

    private final ScrapperService scrapperService;

    @Bean
    @Qualifier("commandMap")
    Map<String, CommandService> commandMap(List<CommandService> commandServices) {
        return commandServices.stream().collect(Collectors.toMap(CommandService::getName, command -> command));
    }

    @Bean
    public ChainResourceHandler chainResourceHandler() {
        ChainResourceHandler chain = new GitHubHandler(scrapperService);
        chain.linkWith(new StackOverflowHandler(scrapperService));
        return chain;
    }

}
