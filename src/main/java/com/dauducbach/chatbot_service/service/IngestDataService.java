package com.dauducbach.chatbot_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.dauducbach.event.IngestDataEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class IngestDataService {
    ElasticsearchClient elasticsearchClient;
    EmbeddingModel embeddingModel;
    VectorStore vectorStore;

    public void ingestAllFromProducts() throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("products")
                .size(100)
        );

        SearchResponse<Map<String, Object>> response = elasticsearchClient.search(searchRequest, (Type) Map.class);

        List<Document> docs = new ArrayList<>();
        for (Hit<Map<String, Object>> hit : response.hits().hits()) {
            Map<String, Object> source = hit.source();


            assert source != null;
            String id = source.get("id").toString();
            String textContent = convertProductToText(source);

            System.out.println(source.toString());

            // metadata hữu ích để hiển thị/citation sau này
            Map<String, Object> meta = new HashMap<>();
            meta.put("product_id", id);
            meta.put("productName", source.get("productName"));
            meta.put("brand", source.get("productBrandId"));
            meta.put("category", source.get("productCategoryId"));

            Document document = new Document(id, textContent, meta);
            float[] embedding = embeddingModel.embed(document);
            document.getMetadata().put("embedding", embedding);
            docs.add(document);
        }

        List<Document> splittedDocs = new TokenTextSplitter().apply(docs);

        vectorStore.add(splittedDocs);
        System.out.println("3. Complete!!");

    }

    @SuppressWarnings("unchecked")
    public String convertProductToText(Map<String, Object> source) {
        StringBuilder sb = new StringBuilder();

        sb.append("Tên sản phẩm: ").append(source.get("productName")).append("\n");
        sb.append("Dòng sản phẩm: ").append(source.get("productLine")).append("\n");
        sb.append("Thương hiệu: ").append(source.get("productBrandId")).append("\n");
        sb.append("Danh mục: ").append(source.get("productCategoryId")).append("\n");
        sb.append("Mô tả: ").append(source.get("descriptions")).append("\n");
        sb.append("Mô tả ngắn: ").append(source.get("shortDescriptions")).append("\n");
        sb.append("Giá: ").append(source.get("price")).append("\n");
        sb.append("Số lượng đã bán: ").append(source.get("purchased_count")).append("\n");

        // Xử lý attributes
        List<Map<String, String>> attributes = (List<Map<String, String>>) source.get("attributes");
        if (attributes != null) {
            sb.append("Các thuộc tính kỹ thuật:\n");
            for (Map<String, String> attr : attributes) {
                sb.append(" - ").append(attr.get("name")).append(": ").append(attr.get("value")).append("\n");
            }
        }

        return sb.toString();
    }

    @KafkaListener(topics = "ingest_product_data")
    public void ingestProduct(@Payload IngestDataEvent event) {
        Map<String, Object> source = event.getSource();
        List<Document> docs = new ArrayList<>();

        String id = source.get("id").toString();
        String textContent = convertProductToText(source);

        System.out.println(source.toString());

        // metadata hữu ích để hiển thị/citation sau này
        Map<String, Object> meta = new HashMap<>();
        meta.put("product_id", id);
        meta.put("productName", source.get("productName"));
        meta.put("brand", source.get("productBrandId"));
        meta.put("category", source.get("productCategoryId"));

        Document document = new Document(id, textContent, meta);
        float[] embedding = embeddingModel.embed(document);
        document.getMetadata().put("embedding", embedding);
        docs.add(document);

        List<Document> splitDocs = new TokenTextSplitter().apply(docs);

        vectorStore.add(splitDocs);
    }

}
