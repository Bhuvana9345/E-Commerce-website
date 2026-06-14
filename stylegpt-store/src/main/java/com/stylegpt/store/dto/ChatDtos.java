package com.stylegpt.store.dto;

import java.util.List;

public class ChatDtos {
    public record ChatRequest(String message, Long productId) {}
    public record ChatResponse(String reply, boolean fallbackUsed, List<ProductDtos.ProductResponse> recommendations) {}
}
