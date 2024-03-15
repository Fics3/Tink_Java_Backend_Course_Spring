package edu.java.jdbc;

import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.repository.ChatRepository;
import edu.java.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JdbcChatService implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public void add(Long tgChatId) {
        if (chatRepository.existsChat(tgChatId)) {
            throw new DuplicateRegistrationScrapperException(
                "Повторная регистрация",
                "Такой пользователь уже зарегистрирован"
            );
        }
        chatRepository.addChat(tgChatId);
    }

    @Override
    public void remove(Long tgChatId) {
        chatRepository.removeChat(tgChatId);
    }
}
