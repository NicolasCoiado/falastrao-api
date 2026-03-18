package br.com.falastrao.falastrao.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.ThinkingConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeminiTopicSuggestionClient {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    private static final String MODEL = "gemini-2.5-flash-lite";
    private static final int THINKING_BUDGET = 512;

    public GeminiTopicSuggestionClient(Client geminiClient, ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public List<String> suggest(String prompt) {

        GenerateContentConfig config = GenerateContentConfig.builder()
                .thinkingConfig(ThinkingConfig.builder()
                        .thinkingBudget(THINKING_BUDGET)
                        .build())
                .build();

        GenerateContentResponse response = geminiClient.models.generateContent(
                MODEL,
                prompt,
                config
        );

        return parseTopics(response.text());
    }

    private List<String> parseTopics(String text) {
        try {
            String cleaned = text.replaceAll("(?s)```json|```", "").trim();
            JsonNode node = objectMapper.readTree(cleaned);

            List<String> topics = new ArrayList<>();
            node.forEach(t -> topics.add(t.asText()));
            return topics;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + text, e);
        }
    }
}