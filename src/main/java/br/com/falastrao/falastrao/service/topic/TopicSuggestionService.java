package br.com.falastrao.falastrao.service.topic;

import br.com.falastrao.falastrao.ai.GeminiTopicSuggestionClient;
import br.com.falastrao.falastrao.model.Topic;
import br.com.falastrao.falastrao.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicSuggestionService {

    private final GeminiTopicSuggestionClient geminiClient;
    private final TopicRepository topicRepository;

    @Value("${spring.gemini.topics.max-suggestions}")
    private int maxSuggestions;

    public TopicSuggestionService(GeminiTopicSuggestionClient geminiClient,
                                  TopicRepository topicRepository) {
        this.geminiClient = geminiClient;
        this.topicRepository = topicRepository;
    }

    public List<String> suggestTopics(String title, String content) {
        Set<String> existingTopics = topicRepository.findAll()
                .stream()
                .map(Topic::getSubject)
                .collect(Collectors.toSet());

        String prompt = buildPrompt(title, content, existingTopics);
        System.out.println(prompt);

        return geminiClient.suggest(prompt);
    }

    private String buildPrompt(String title, String content, Set<String> existingTopics) {
        return """
                You are a topic tagging assistant. Suggest exactly %d relevant topics for the review below.
                Prioritize topics from the existing list. Only suggest new topics if clearly relevant and not covered.
                Respond ONLY with a JSON array of strings. No explanation. Example: ["Interstellar", "Movie", "Oscar"]

                Existing topics: %s

                Review title: %s
                Review content: %s
                """.formatted(maxSuggestions, existingTopics, title, content);
    }
}