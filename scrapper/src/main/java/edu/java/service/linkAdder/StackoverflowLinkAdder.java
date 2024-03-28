package edu.java.service.linkAdder;

import edu.java.client.StackoverflowClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.domain.repository.LinksRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.model.LinkModel;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StackoverflowLinkAdder implements LinkAdder {

    private final ApplicationConfig applicationConfig;
    private final StackoverflowClient stackoverflowClient;
    private final LinksRepository jooqLinksRepository;
    private final StackoverflowQuestionRepository jooqStackoverflowQuestionRepository;

    @Override
    public LinkModel addLink(URI url, Long tgChatId) {
        var question = stackoverflowClient.fetchQuestion(url).block();
        var link = jooqLinksRepository.addLink(
            tgChatId,
            url.toString(),
            Objects.requireNonNull(question).items().getFirst().lastActivityDate()
        );
        return jooqStackoverflowQuestionRepository.addQuestion(
            link,
            question.items().getFirst().answerCount()
        );
    }

    @Override
    public String getDomain() {
        return applicationConfig.stackoverflowProperties().domain();
    }
}
