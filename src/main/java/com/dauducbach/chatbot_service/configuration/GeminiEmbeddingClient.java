package com.dauducbach.chatbot_service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Primary
@Component
@Slf4j
public class GeminiEmbeddingClient implements EmbeddingModel {
    private final RestClient restClient;
    @Value("${gemini-key}")
    private String apiKey;

    public GeminiEmbeddingClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = new ArrayList<>();

        for (int idx = 0; idx < request.getInstructions().size(); idx++) {
            String text = request.getInstructions().get(idx);

            var response = restClient.post()
                    .uri("/models/gemini-embedding-exp-03-07:embedContent?key=" + apiKey)
                    .body(Map.of("content",
                            Map.of("parts", List.of(Map.of("text", text)))))
                    .retrieve()
                    .body(Map.class);

            // Parse response JSON
            Map<String, Object> embeddingMap = (Map<String, Object>) response.get("embedding");
            List<Double> values = (List<Double>) embeddingMap.get("values");


            float[] embeddingVector = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                embeddingVector[i] = values.get(i).floatValue();
            }

            embeddings.add(new Embedding(embeddingVector, idx));
        }


        return new EmbeddingResponse(embeddings);
    }


    @Override
    public float[] embed(Document document) {
        EmbeddingRequest request = new EmbeddingRequest(List.of(document.getFormattedContent()), null);
        EmbeddingResponse response = call(request);
        return response.getResult().getOutput();
    }
}

