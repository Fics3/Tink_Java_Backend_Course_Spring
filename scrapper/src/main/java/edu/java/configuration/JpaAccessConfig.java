package edu.java.configuration;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.jpa.JpaChatRepository;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import edu.java.domain.repository.jpa.JpaQuestionRepository;
import edu.java.domain.repository.jpa.JpaRepositoryRepository;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaLinkUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfig {

    @Bean
    public ChatService chatService(JpaChatRepository jpaChatRepository) {
        return new JpaChatService(jpaChatRepository);
    }

    @Bean
    public LinkService linkService(
        JpaLinksRepository linkRepository,
        JpaChatRepository tgChatRepository,
        GithubClient githubClient,
        StackoverflowClient stackoverflowClient
    ) {
        return new JpaLinkService(githubClient, stackoverflowClient, linkRepository, tgChatRepository);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JpaLinksRepository jpaLinksRepository,
        GithubClient githubClient,
        StackoverflowClient stackoverflowClient,
        BotClient botClient,
        JpaRepositoryRepository jpaRepositoryRepository,
        JpaQuestionRepository jpaQuestionRepository
    ) {
        return new JpaLinkUpdater(
            jpaLinksRepository,
            githubClient,
            stackoverflowClient,
            botClient,
            jpaRepositoryRepository,
            jpaQuestionRepository
        );
    }
}
