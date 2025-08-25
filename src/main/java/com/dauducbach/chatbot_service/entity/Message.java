package com.dauducbach.chatbot_service.entity;

import com.dauducbach.chatbot_service.dto.MessageSender;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

@Entity
public class Message {
    @Id
    String id;

    @ManyToOne
    Conversation conversation;

    MessageSender messageSender;
    String content;
    Instant timestamp;
}
