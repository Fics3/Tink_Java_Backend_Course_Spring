package edu.java.configuration;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.domain.repository.jooq.JooqChatRepository;
import edu.java.domain.repository.jooq.JooqGithubRepositoryRepository;
import edu.java.domain.repository.jooq.JooqLinksRepository;
import edu.java.domain.repository.jooq.JooqStackoverflowQuestionRepository;
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
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfig {

    @Bean
    public ChatService chatService(JooqChatRepository jooqChatRepository) {
        return new ChatService(jooqChatRepository);
    }

    @Bean
    public LinkService linkService(
        JooqLinksRepository linkRepository,
        @Qualifier("linkAdders") Map<String, LinkAdder> linkAdders
    ) {
        return new LinkService(linkAdders, linkRepository);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JooqLinksRepository jooqLinksRepository,
        @Qualifier("updateCheckers") Map<String, UpdateChecker> updateCheckers,
        @Value("#{@scheduler.forceCheckDelay().toMillis()}") Duration threshold
    ) {
        return new LinkUpdater(
            jooqLinksRepository,
            updateCheckers,
            threshold
        );
    }

    @Bean
    public GithubUpdateChecker githubUpdateChecker(
        ApplicationConfig applicationConfig,
        GithubClient githubClient,
        BotClient botClient,
        JooqChatRepository jooqChatRepository,
        JooqLinksRepository jooqLinksRepository,
        GithubRepositoryRepository jooqGithubRepositoryRepository
    ) {
        return new GithubUpdateChecker(
            applicationConfig,
            githubClient,
            botClient,
            jooqChatRepository,
            jooqLinksRepository,
            jooqGithubRepositoryRepository
        );
    }

    @Bean
    public StackoverflowUpdateChecker stackoverflowUpdateChecker(
        ApplicationConfig applicationConfig,
        StackoverflowClient stackoverflowClient,
        BotClient botClient,
        JooqChatRepository jooqChatRepository,
        JooqLinksRepository jooqLinksRepository,
        StackoverflowQuestionRepository jooqStackoverflowQuestionRepository
    ) {
        return new StackoverflowUpdateChecker(
            applicationConfig,
            stackoverflowClient,
            botClient,
            jooqChatRepository,
            jooqLinksRepository,
            jooqStackoverflowQuestionRepository
        );
    }

    @Bean
    public GithubLinkAdder githubLinkAdder(
        ApplicationConfig applicationConfig,
        GithubClient githubClient,
        JooqLinksRepository jooqLinksRepository,
        JooqGithubRepositoryRepository jooqGithubRepositoryRepository
    ) {
        return new GithubLinkAdder(
            applicationConfig,
            githubClient,
            jooqLinksRepository,
            jooqGithubRepositoryRepository
        );
    }

    @Bean
    public StackoverflowLinkAdder stackoverflowLinkAdder(
        ApplicationConfig applicationConfig,
        StackoverflowClient stackoverflowClient,
        JooqLinksRepository jooqLinksRepository,
        JooqStackoverflowQuestionRepository jooqStackoverflowQuestionRepository
    ) {
        return new StackoverflowLinkAdder(
            applicationConfig,
            stackoverflowClient,
            jooqLinksRepository,
            jooqStackoverflowQuestionRepository
        );
    }
}
