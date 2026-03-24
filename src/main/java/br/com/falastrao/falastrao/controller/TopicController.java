package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.TopicSuggestionRequest;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.service.topic.TopicService;
import br.com.falastrao.falastrao.service.topic.TopicSuggestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;
    private final TopicSuggestionService suggestionService;


    public TopicController(TopicService topicService, TopicSuggestionService suggestionService) {
        this.topicService = topicService;
        this.suggestionService = suggestionService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<String>> listTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(topicService.getTopics(page, size));
    }

    @GetMapping("/ranked")
    public ResponseEntity<List<String>> ranked (@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(topicService.getRankedTopics(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> search (
            @RequestParam
            @Size(min = 2, message = "Prefix must be at least 2 characters")
            String prefix) {
        return ResponseEntity.ok(topicService.searchTopics(prefix));
    }

    @PostMapping("/suggest")
    public ResponseEntity<List<String>> suggest(@Valid @RequestBody TopicSuggestionRequest request) {
        return ResponseEntity.ok(suggestionService.suggestTopics(request.title(), request.content()));
    }
}
