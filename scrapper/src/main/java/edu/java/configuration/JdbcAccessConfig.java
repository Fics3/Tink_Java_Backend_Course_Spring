package edu.java.configuration;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.jdbc.JdbcChatRepository;
import edu.java.domain.repository.jdbc.JdbcLinksRepository;
import edu.java.domain.repository.jdbc.JdbcQuestionRepository;
import edu.java.domain.repository.jdbc.JdbcRepositoryRepository;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.jdbc.JdbcChatService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcLinkUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfig {

    @Bean
    public ChatService chatService(JdbcChatRepository jdbcChatRepository) {
        return new JdbcChatService(jdbcChatRepository);
    }

    @Bean
    public LinkService linkService(
        JdbcLinksRepository linkRepository,
        GithubClient githubClient,
        StackoverflowClient stackoverflowClient
    ) {
        return new JdbcLinkService(linkRepository, githubClient, stackoverflowClient);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JdbcLinksRepository jdbcLinksRepository,
        JdbcChatRepository jdbcChatRepository,
        GithubClient githubClient,
        StackoverflowClient stackoverflowClient,
        BotClient botClient,
        JdbcRepositoryRepository jdbcRepositoryRepository,
        JdbcQuestionRepository jdbcQuestionRepository
    ) {
        return new JdbcLinkUpdater(
            jdbcLinksRepository,
            jdbcChatRepository,
            githubClient,
            stackoverflowClient,
            botClient,
            jdbcRepositoryRepository,
            jdbcQuestionRepository
        );
    }
}
