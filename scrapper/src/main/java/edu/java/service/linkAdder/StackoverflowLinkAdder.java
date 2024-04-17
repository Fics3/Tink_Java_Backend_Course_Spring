package edu.java.service.linkAdder;

import edu.java.client.StackoverflowClient;
import edu.java.configuration.ClientConfig;
import edu.java.domain.repository.LinksRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.model.LinkModel;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StackoverflowLinkAdder implements LinkAdder {

    private final ClientConfig clientConfig;
    private final StackoverflowClient stackoverflowClient;
    private final LinksRepository linksRepository;
    private final StackoverflowQuestionRepository stackoverflowQuestionRepository;

    @Override
    public LinkModel addLink(URI url, Long tgChatId) {
        var question = stackoverflowClient.fetchQuestion(url).block();
        var link = linksRepository.addLink(
            tgChatId,
            url.toString(),
            Objects.requireNonNull(question).items().getFirst().lastActivityDate()
        );
        return stackoverflowQuestionRepository.addQuestion(
            link,
            question.items().getFirst().answerCount()
        );
    }

    @Override
    public String getDomain() {
        return clientConfig.stackoverflowProperties().domain();
    }
}
