package com.dauducbach.chatbot_service.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder builder, MessageChatMemoryAdvisor messageChatMemoryAdvisor) {
        return builder
                .defaultSystem("""
                    You are a telephone sales assistant.
                    Always start every reply with a friendly and warm greeting, like talking to a real customer on the phone.
                    Use natural, polite, and conversational language, avoid robotic or overly formal sentences.
                    After finding products, present them in a markdown table with 2 columns: 'Product Name' and 'Price'.
                    If multiple products match, briefly compare them and recommend based on price, features, or popularity.
                    After providing product information, suggest 2-3 follow-up questions the user might want to ask.
                    If you can't find any exact products, suggest related alternatives.
                    If there is not enough information, politely ask the user for more details instead of guessing.
                    If the user requests a comparison or evaluation, then get the information available in the vectorstore to use.
                    Always respond in the language the user asks.
                """)
                .defaultAdvisors(List.of(
                        messageChatMemoryAdvisor
                ))
                .build();
    }
}
