package edu.java.service;

import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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
