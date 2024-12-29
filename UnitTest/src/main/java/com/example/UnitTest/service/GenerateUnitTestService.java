package com.example.UnitTest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Service
@Slf4j
public class GenerateUnitTestService {


    private final WebClient webClient;

    @Autowired
    public GenerateUnitTestService(WebClient webClient) {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofMinutes(3))))
                .build();
    }

    @Value("${generate.url}")
    String generateUrl;

    public String generateResponse(String url) {
        String output = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Log the request for debugging
            log.info("Sending request to {}, with URL: {}", generateUrl, url);

            String res = webClient.post()
                    .uri(generateUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue("{\"fileUrl\": \"" + url + "\"}")
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            return response.bodyToMono(String.class);
                        } else {
                            log.error("Error response: {}", response.statusCode());
                            return Mono.just("{\"error\": \"" + response.statusCode() + "\"}");
                        }
                    })
                    .block();

            // Log the response for debugging
            log.info("Received response: {}", res);

            JsonNode rootNode = objectMapper.readTree(res);

            // Check if the response has the expected field
            JsonNode generatedTest = rootNode.get("generated_test");
            if (generatedTest == null) {
                // Try alternate field name or log the structure
                log.error("Response doesn't contain 'generated_test' field. Response structure: {}", rootNode.toString());
                return "";
            }

            output = generatedTest.asText();
            output = output.replaceAll("```java\\s*", "")  // Remove opening markers
                    .replaceAll("```\\s*$", "")       // Remove closing markers
                    .trim();

        } catch (Exception e) {
            log.error("Error generating response: {}", e.getMessage());
            return "";  // Return empty string instead of null
        }

        return output;
    }
}