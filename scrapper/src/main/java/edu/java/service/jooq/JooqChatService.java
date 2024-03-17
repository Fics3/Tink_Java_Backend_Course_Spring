package edu.java.service.jooq;

import edu.java.domain.repository.jooq.JooqChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JooqChatService implements ChatService {

    @Autowired
    private JooqChatRepository jooqChatRepository;

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
