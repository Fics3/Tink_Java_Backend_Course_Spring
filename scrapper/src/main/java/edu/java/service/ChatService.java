package edu.java.service;

import edu.java.domain.repository.ChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository jooqChatRepository;

    public void add(Long tgChatId) {
        if (jooqChatRepository.existsChat(tgChatId)) {
            throw new DuplicateRegistrationScrapperException(
                "Повторная регистрация",
                "Такой пользователь уже зарегистрирован"
            );
        }
        jooqChatRepository.addChat(tgChatId);
    }

    public void remove(Long tgChatId) {
        jooqChatRepository.removeChat(tgChatId);
    }
}
