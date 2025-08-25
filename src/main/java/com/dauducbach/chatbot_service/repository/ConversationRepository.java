package com.dauducbach.chatbot_service.repository;

import com.dauducbach.chatbot_service.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    boolean existsByUserId(String userId);
    Conversation findByUserId(String userId);
}
