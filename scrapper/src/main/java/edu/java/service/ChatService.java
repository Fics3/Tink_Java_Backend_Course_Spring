package edu.java.service;

import edu.java.domain.repository.ChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public void add(Long tgChatId) {
        if (chatRepository.existsChat(tgChatId)) {
            throw new DuplicateRegistrationScrapperException(
                "Повторная регистрация",
                "Такой пользователь уже зарегистрирован"
            );
        }
        chatRepository.addChat(tgChatId);
    }

    public void remove(Long tgChatId) {
        chatRepository.removeChat(tgChatId);
    }
}
