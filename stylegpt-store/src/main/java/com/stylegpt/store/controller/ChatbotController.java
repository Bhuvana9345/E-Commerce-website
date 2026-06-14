package com.stylegpt.store.controller;

import com.stylegpt.store.dto.ChatDtos.ChatRequest;
import com.stylegpt.store.dto.ChatDtos.ChatResponse;
import com.stylegpt.store.security.UserPrincipal;
import com.stylegpt.store.service.ChatbotService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public ChatResponse message(@AuthenticationPrincipal UserPrincipal principal, @RequestBody ChatRequest request) {
        return chatbotService.answer(principal.getUser(), request);
    }
}
