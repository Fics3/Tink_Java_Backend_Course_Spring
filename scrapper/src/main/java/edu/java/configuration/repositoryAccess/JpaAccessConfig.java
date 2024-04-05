package edu.java.configuration.repositoryAccess;

import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.domain.repository.jpa.JpaChatRepository;
import edu.java.domain.repository.jpa.JpaGithubRepositoryRepository;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import edu.java.domain.repository.jpa.JpaStackoverflowQuestionRepository;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.NotificationService;
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
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfig {

    @Bean
    public ChatService chatService(JpaChatRepository jpaChatRepository) {
        return new ChatService(jpaChatRepository);
    }

    @Bean
    public LinkService linkService(
        JpaLinksRepository linkRepository,
        @Qualifier("linkAdders") Map<String, LinkAdder> linkAdders,
        JpaGithubRepositoryRepository jpaGithubRepositoryRepository,
        JpaStackoverflowQuestionRepository jpaStackoverflowQuestionRepository
    ) {
        return new LinkService(
            linkAdders,
            linkRepository,
            jpaGithubRepositoryRepository,
            jpaStackoverflowQuestionRepository
        );
    }

    @Bean
    public LinkUpdater linkUpdater(
        JpaLinksRepository jpaLinksRepository,
        @Qualifier("updateCheckers") Map<String, UpdateChecker> updateCheckers,
        @Value("#{@scheduler.forceCheckDelay().toMillis()}") Duration threshold
    ) {
        return new LinkUpdater(
            jpaLinksRepository,
            updateCheckers,
            threshold
        );
    }

    @Bean
    public GithubUpdateChecker githubUpdateChecker(
        ApplicationConfig applicationConfig,
        GithubClient githubClient,
        NotificationService notificationService,
        JpaChatRepository jpaChatRepository,
        JpaLinksRepository jpaLinksRepository,
        GithubRepositoryRepository jpaGithubRepositoryRepository
    ) {
        return new GithubUpdateChecker(
            applicationConfig,
            githubClient,
            notificationService,
            jpaChatRepository,
            jpaLinksRepository,
            jpaGithubRepositoryRepository
        );
    }

    @Bean
    public StackoverflowUpdateChecker stackoverflowUpdateChecker(
        ApplicationConfig applicationConfig,
        StackoverflowClient stackoverflowClient,
        NotificationService notificationService,
        JpaChatRepository jpaChatRepository,
        JpaLinksRepository jpaLinksRepository,
        StackoverflowQuestionRepository jpaStackoverflowQuestionRepository
    ) {
        return new StackoverflowUpdateChecker(
            applicationConfig,
            stackoverflowClient,
            notificationService,
            jpaChatRepository,
            jpaLinksRepository,
            jpaStackoverflowQuestionRepository
        );
    }

    @Bean
    public GithubLinkAdder githubLinkAdder(
        ApplicationConfig applicationConfig,
        GithubClient githubClient,
        JpaLinksRepository jpaLinksRepository,
        JpaGithubRepositoryRepository jpaGithubRepositoryRepository
    ) {
        return new GithubLinkAdder(
            applicationConfig,
            githubClient,
            jpaLinksRepository,
            jpaGithubRepositoryRepository
        );
    }

    @Bean
    public StackoverflowLinkAdder stackoverflowLinkAdder(
        ApplicationConfig applicationConfig,
        StackoverflowClient stackoverflowClient,
        JpaLinksRepository jpaLinksRepository,
        JpaStackoverflowQuestionRepository jpaStackoverflowQuestionRepository
    ) {
        return new StackoverflowLinkAdder(
            applicationConfig,
            stackoverflowClient,
            jpaLinksRepository,
            jpaStackoverflowQuestionRepository
        );
    }
}
