package edu.java.service.jpa;

import edu.java.domain.entity.ChatEntity;
import edu.java.domain.repository.jpa.JpaChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.service.ChatService;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpaChatService implements ChatService {

    @Autowired
    private JpaChatRepository jpaChatRepository;

    @Override
    public void add(Long tgChatId) {
        if (jpaChatRepository.existsByTelegramChatId(tgChatId)) {
            throw new DuplicateRegistrationScrapperException(
                "Повторная регистрация",
                "Такой пользователь уже зарегистрирован"
            );
        }

        ChatEntity newChat = new ChatEntity(tgChatId, OffsetDateTime.now());
        jpaChatRepository.save(newChat);
    }

    @Override
    public void remove(Long tgChatId) {
        jpaChatRepository.deleteByTelegramChatId(tgChatId);
    }
}
