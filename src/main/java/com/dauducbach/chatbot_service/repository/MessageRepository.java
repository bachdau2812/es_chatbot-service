package com.dauducbach.chatbot_service.repository;

import com.dauducbach.chatbot_service.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

}
