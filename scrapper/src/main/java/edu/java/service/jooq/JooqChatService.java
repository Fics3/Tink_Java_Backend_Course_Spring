package edu.java.service.jooq;

import edu.java.domain.repository.jooq.JooqChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.service.ChatService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JooqChatService implements ChatService {
    private final JooqChatRepository jooqChatRepository;

    @Override
    public void add(Long tgChatId) {
        if (jooqChatRepository.existsChat(tgChatId)) {
            throw new DuplicateRegistrationScrapperException(
                "Повторная регистрация",
                "Такой пользователь уже зарегистрирован"
            );
        }
        jooqChatRepository.addChat(tgChatId);
    }

    @Override
    public void remove(Long tgChatId) {
        jooqChatRepository.removeChat(tgChatId);
    }
}
