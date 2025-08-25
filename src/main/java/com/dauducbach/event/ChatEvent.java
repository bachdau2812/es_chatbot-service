package com.dauducbach.event;

import com.dauducbach.chatbot_service.dto.MessageSender;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class ChatEvent {
    String userId;
    String content;
    MessageSender messageSender;
}
