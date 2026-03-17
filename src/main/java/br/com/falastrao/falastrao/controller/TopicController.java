package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.service.topic.TopicService;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {

    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> search (
            @RequestParam
            @Size(min = 2, message = "Prefix must be at least 2 characters")
            String prefix) {
        return ResponseEntity.ok(service.searchTopics(prefix));
    }

}
