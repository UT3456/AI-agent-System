package com.substring.agent.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.substring.agent.backend.service.ChatService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<String> chatGet(@RequestHeader("Conversation-Id") String conversationId,
            @RequestBody String message) {
        return ResponseEntity.ok(chatService.chat(message, conversationId));
    }

    /**
     * Server-Sent Events endpoint that streams the assistant reply token by
     * token instead of waiting for the complete response.
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestHeader("Conversation-Id") String conversationId,
            @RequestBody String message) {
        return chatService.stream(message, conversationId);
    }

}
