package edu.java.configuration.repositoryAccess;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.jooq.JooqChatRepository;
import edu.java.domain.repository.jooq.JooqLinksRepository;
import edu.java.domain.repository.jooq.JooqQuestionRepository;
import edu.java.domain.repository.jooq.JooqRepositoryRepository;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.jooq.JooqChatService;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.jooq.JooqLinkUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfig {

    @Bean
    public ChatService chatService(JooqChatRepository jooqChatRepository) {
        return new JooqChatService(jooqChatRepository);
    }

    @Bean
    public LinkService linkService(
        JooqLinksRepository linkRepository,
        GithubClient githubClient,
        StackoverflowClient stackoverflowClient
    ) {
        return new JooqLinkService(githubClient, stackoverflowClient, linkRepository);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JooqLinksRepository jooqLinksRepository,
        JooqChatRepository jooqChatRepository,
        GithubClient githubClient,
        StackoverflowClient stackoverflowClient,
        BotClient botClient,
        JooqRepositoryRepository jooqRepositoryRepository,
        JooqQuestionRepository jooqQuestionRepository
    ) {
        return new JooqLinkUpdater(
            jooqLinksRepository,
            jooqChatRepository,
            githubClient,
            stackoverflowClient,
            botClient,
            jooqRepositoryRepository,
            jooqQuestionRepository
        );
    }
}
