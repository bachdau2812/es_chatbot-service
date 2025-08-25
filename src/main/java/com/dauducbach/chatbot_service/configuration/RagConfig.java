package com.dauducbach.chatbot_service.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(ChatClient chatClient, VectorStore vectorStore) {
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(chatClient.mutate())
                        .build())
                .queryTransformers(TranslationQueryTransformer.builder()
                        .chatClientBuilder(chatClient.mutate())
                        .targetLanguage("vietnamese")
                        .build()
                )
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.3)
                        .topK(10)
                        .vectorStore(vectorStore)
                        .build())
                .build();
    }
}
