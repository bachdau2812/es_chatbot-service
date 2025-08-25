package com.dauducbach.chatbot_service.controller;

import com.dauducbach.chatbot_service.service.ChatService;
import com.dauducbach.chatbot_service.service.IngestDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

public class ChatController {
    ChatService chatService;
    IngestDataService ingestDataService;

    @GetMapping("/chat")
    String chat(@RequestParam String query) {
        return chatService.chat(query);
    }

    @GetMapping("/get_memory")
    List<Message> getMemory() {
        return chatService.getJdbcChatMemory();
    }

    @GetMapping("/ingest_data")
    void ingestData() throws IOException {
        ingestDataService.ingestAllFromProducts();
    }
}
