package com.dauducbach.chatbot_service.service;

import com.dauducbach.chatbot_service.dto.MessageSender;
import com.dauducbach.event.ChatEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j

public class ChatService {
    ChatClient chatClient;
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    ChatMemory chatMemory;
    KafkaTemplate<String, Object> kafkaTemplate;

    public String chat(String query) {
        String userId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject();

        chatMemory.add(userId, new UserMessage(query));

        //Gui event luu tin nhan
        ChatEvent chatEvent = ChatEvent.builder()
                .userId(userId)
                .messageSender(MessageSender.USER)
                .content(query)
                .build();
        kafkaTemplate.send("chatbot_event", chatEvent);

        String assistantMessage = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(query)
                .call()
                .content();

        assert assistantMessage != null;
        chatMemory.add(userId, new AssistantMessage(assistantMessage));

        //Gui event luu tin nhan
        ChatEvent chatEvent2 = ChatEvent.builder()
                .userId(userId)
                .messageSender(MessageSender.ASSISTANT)
                .content(assistantMessage)
                .build();
        kafkaTemplate.send("chatbot_event", chatEvent2);

        return assistantMessage;
    }

    public List<Message> getJdbcChatMemory() {
        String userId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject();
        return chatMemory.get(userId);
    }

}
