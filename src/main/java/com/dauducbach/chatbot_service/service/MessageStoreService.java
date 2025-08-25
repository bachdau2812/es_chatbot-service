package com.dauducbach.chatbot_service.service;

import com.dauducbach.chatbot_service.entity.Conversation;
import com.dauducbach.chatbot_service.entity.Message;
import com.dauducbach.chatbot_service.repository.ConversationRepository;
import com.dauducbach.chatbot_service.repository.MessageRepository;
import com.dauducbach.event.ChatEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j

public class MessageStoreService {
    ConversationRepository conversationRepository;
    MessageRepository messageRepository;

    @KafkaListener(topics = "chatbot_event")
    public void saveMessage(@Payload ChatEvent chatEvent) {
        log.info("Chat event: {}", chatEvent);
        if (!conversationRepository.existsByUserId(chatEvent.getUserId())) {
            Conversation newConversation = Conversation.builder()
                    .id(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now())
                    .userId(chatEvent.getUserId())
                    .build();

            conversationRepository.save(newConversation);
        }

        var conversation = conversationRepository.findByUserId(chatEvent.getUserId());
        var message = Message.builder()
                .id(UUID.randomUUID().toString())
                .messageSender(chatEvent.getMessageSender())
                .content(chatEvent.getContent())
                .conversation(conversation)
                .timestamp(Instant.now())
                .build();

        messageRepository.save(message);
    }

    public Conversation getHistory(String userId) {
        return conversationRepository.findByUserId(userId);
    }
}
