package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.UserRequest;
import br.com.falastrao.falastrao.dto.response.UserResponse;
import br.com.falastrao.falastrao.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create (@RequestBody UserRequest user) {
        UserResponse response = service.save(user);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "User successfully created!");
        responseMap.put("user", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }
}
