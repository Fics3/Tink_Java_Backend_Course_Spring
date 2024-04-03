package edu.java.configuration.repositoryAccess;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.domain.repository.jdbc.JdbcChatRepository;
import edu.java.domain.repository.jdbc.JdbcGithubRepositoryRepository;
import edu.java.domain.repository.jdbc.JdbcLinksRepository;
import edu.java.domain.repository.jdbc.JdbcStackoverflowQuestionRepository;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.linkAdder.GithubLinkAdder;
import edu.java.service.linkAdder.LinkAdder;
import edu.java.service.linkAdder.StackoverflowLinkAdder;
import edu.java.service.updateChecker.GithubUpdateChecker;
import edu.java.service.updateChecker.StackoverflowUpdateChecker;
import edu.java.service.updateChecker.UpdateChecker;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfig {

    @Bean
    public ChatService chatService(JdbcChatRepository jdbcChatRepository) {
        return new ChatService(jdbcChatRepository);
    }

    @Bean
    public LinkService linkService(
        JdbcLinksRepository linkRepository,
        @Qualifier("linkAdders") Map<String, LinkAdder> linkAdders,
        JdbcGithubRepositoryRepository jdbcGithubRepositoryRepository,
        JdbcStackoverflowQuestionRepository jdbcStackoverflowQuestionRepository
    ) {
        return new LinkService(
            linkAdders,
            linkRepository,
            jdbcGithubRepositoryRepository,
            jdbcStackoverflowQuestionRepository
        );
    }

    @Bean
    public LinkUpdater linkUpdater(
        JdbcLinksRepository jdbcLinksRepository,
        @Qualifier("updateCheckers") Map<String, UpdateChecker> updateCheckers,
        @Value("#{@scheduler.forceCheckDelay().toMillis()}") Duration threshold
    ) {
        return new LinkUpdater(
            jdbcLinksRepository,
            updateCheckers,
            threshold
        );
    }

    @Bean
    public GithubUpdateChecker githubUpdateChecker(
        ApplicationConfig applicationConfig,
        GithubClient githubClient,
        BotClient botClient,
        JdbcChatRepository jdbcChatRepository,
        JdbcLinksRepository jdbcLinksRepository,
        GithubRepositoryRepository jdbcGithubRepositoryRepository
    ) {
        return new GithubUpdateChecker(
            applicationConfig,
            githubClient,
            botClient,
            jdbcChatRepository,
            jdbcLinksRepository,
            jdbcGithubRepositoryRepository
        );
    }

    @Bean
    public StackoverflowUpdateChecker stackoverflowUpdateChecker(
        ApplicationConfig applicationConfig,
        StackoverflowClient stackoverflowClient,
        BotClient botClient,
        JdbcChatRepository jdbcChatRepository,
        JdbcLinksRepository jdbcLinksRepository,
        StackoverflowQuestionRepository jdbcStackoverflowQuestionRepository
    ) {
        return new StackoverflowUpdateChecker(
            applicationConfig,
            stackoverflowClient,
            botClient,
            jdbcChatRepository,
            jdbcLinksRepository,
            jdbcStackoverflowQuestionRepository
        );
    }

    @Bean
    public GithubLinkAdder githubLinkAdder(
        ApplicationConfig applicationConfig,
        GithubClient githubClient,
        JdbcLinksRepository jdbcLinksRepository,
        JdbcGithubRepositoryRepository jdbcGithubRepositoryRepository
    ) {
        return new GithubLinkAdder(
            applicationConfig,
            githubClient,
            jdbcLinksRepository,
            jdbcGithubRepositoryRepository
        );
    }

    @Bean
    public StackoverflowLinkAdder stackoverflowLinkAdder(
        ApplicationConfig applicationConfig,
        StackoverflowClient stackoverflowClient,
        JdbcLinksRepository jdbcLinksRepository,
        JdbcStackoverflowQuestionRepository jdbcStackoverflowQuestionRepository
    ) {
        return new StackoverflowLinkAdder(
            applicationConfig,
            stackoverflowClient,
            jdbcLinksRepository,
            jdbcStackoverflowQuestionRepository
        );
    }
}
