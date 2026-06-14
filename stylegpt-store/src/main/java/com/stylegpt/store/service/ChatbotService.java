package com.stylegpt.store.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stylegpt.store.dto.ChatDtos.ChatRequest;
import com.stylegpt.store.dto.ChatDtos.ChatResponse;
import com.stylegpt.store.dto.ProductDtos.ProductResponse;
import com.stylegpt.store.entity.ChatbotHistory;
import com.stylegpt.store.entity.Product;
import com.stylegpt.store.entity.User;
import com.stylegpt.store.repository.ChatbotHistoryRepository;
import com.stylegpt.store.repository.ProductRepository;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {
    private final ProductRepository productRepository;
    private final ChatbotHistoryRepository historyRepository;
    private final MapperService mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-5.5}")
    private String model;

    @Value("${openai.url:https://api.openai.com/v1/responses}")
    private String openAiUrl;

    public ChatbotService(ProductRepository productRepository, ChatbotHistoryRepository historyRepository, MapperService mapper) {
        this.productRepository = productRepository;
        this.historyRepository = historyRepository;
        this.mapper = mapper;
    }

    public ChatResponse answer(User user, ChatRequest request) {
        String message = request.message() == null ? "" : request.message().trim();
        if (message.isBlank()) {
            message = "Suggest a stylish outfit";
        }
        List<Product> recommended = recommendProducts(message);
        String reply;
        boolean fallback = false;
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("OpenAI API key missing");
            }
            reply = callOpenAi(message, recommended);
        } catch (Exception ex) {
            fallback = true;
            reply = localReply(message, recommended);
        }

        ChatbotHistory history = new ChatbotHistory();
        history.setUser(user);
        history.setUserMessage(message);
        history.setBotResponse(reply);
        history.setFallbackUsed(fallback);
        historyRepository.save(history);

        List<ProductResponse> responses = shouldShowProducts(message)
                ? recommended.stream().limit(5).map(mapper::product).toList()
                : List.of();
        return new ChatResponse(reply, fallback, responses);
    }

    private String callOpenAi(String message, List<Product> products) throws Exception {
        String productContext = products.stream()
                .limit(8)
                .map(p -> p.getName() + " | " + p.getCategory() + " | " + p.getColor() + " | Rs." + p.getPrice())
                .reduce("", (a, b) -> a + "\n- " + b);
        String prompt = """
                You are StyleGPT, a friendly fashion shopping assistant for an Indian e-commerce store.
                Answer in English.
                Answer only the user's exact current question. Do not start a new topic.
                If they ask for product suggestions, mention matching catalog products. Otherwise give only fashion guidance.
                If the question is not fashion, outfit, shopping, size, color, or product related, politely say you can help only with fashion shopping.
                Keep the answer concise and answer only after the user asks.

                Available products:
                %s

                User: %s
                """.formatted(productContext, message);

        String body = objectMapper.writeValueAsString(Map.of(
                "model", model,
                "input", prompt
        ));
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(openAiUrl))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("OpenAI API failed: " + response.statusCode());
        }
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode outputText = root.path("output_text");
        if (!outputText.isMissingNode() && !outputText.asText().isBlank()) {
            return outputText.asText();
        }
        StringBuilder text = new StringBuilder();
        for (JsonNode output : root.path("output")) {
            for (JsonNode content : output.path("content")) {
                if ("output_text".equals(content.path("type").asText())) {
                    text.append(content.path("text").asText()).append("\n");
                }
            }
        }
        String answer = text.toString().trim();
        if (answer.isBlank()) {
            throw new IllegalStateException("No AI response text");
        }
        return answer;
    }

    private List<Product> recommendProducts(String message) {
        String q = message.toLowerCase(Locale.ROOT);
        BigDecimal max = extractBudget(q);
        String category = null;
        if (containsAny(q, "shoe", "sneaker", "shoes")) category = "Shoes";
        else if (containsAny(q, "shirt", "top", "kurti")) category = "Shirts";
        else if (containsAny(q, "pant", "jean", "trouser")) category = "Pants";
        else if (containsAny(q, "dress", "wedding", "party")) category = "Dresses";
        else if (containsAny(q, "snack", "chips", "cookie", "noodle")) category = "Snacks";

        String color = null;
        for (String c : List.of("black", "white", "blue", "pink", "green", "red", "cream", "brown", "grey")) {
            if (q.contains(c)) {
                color = c;
                break;
            }
        }
        String finalCategory = category;
        String finalColor = color;
        return productRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(p -> max == null || p.getPrice().compareTo(max) <= 0)
                .sorted(Comparator.comparingInt((Product p) -> score(p, q, finalCategory, finalColor)).reversed())
                .limit(8)
                .toList();
    }

    private int score(Product p, String q, String category, String color) {
        int score = 0;
        String all = (p.getName() + " " + p.getDescription() + " " + p.getCategory() + " " + p.getColor()).toLowerCase(Locale.ROOT);
        if (category != null && p.getCategory().equalsIgnoreCase(category)) score += 8;
        if (color != null && p.getColor().toLowerCase(Locale.ROOT).contains(color)) score += 5;
        for (String word : q.split("\\s+")) {
            if (word.length() > 2 && all.contains(word)) score += 2;
        }
        if (containsAny(q, "college", "casual") && containsAny(all, "casual", "sneaker", "jeans", "shirt")) score += 4;
        if (containsAny(q, "wedding", "party") && containsAny(all, "dress", "ethnic", "blazer", "heels")) score += 4;
        return score;
    }

    private String localReply(String message, List<Product> products) {
        String q = message.toLowerCase(Locale.ROOT);
        StringBuilder reply = new StringBuilder();
        if (!isFashionQuestion(q)) {
            return "I can help only with fashion, outfit matching, size, color, and shopping questions.";
        }
        if (q.contains("black") && (q.contains("shirt") || q.contains("shirt-ku"))) {
            reply.append("Black shirt-ku beige, grey, blue denim, or cream chinos super match aagum. ");
        } else if (containsAny(q, "wedding", "party")) {
            reply.append("Wedding/party look-ku rich colors like maroon, navy, cream, gold accents and polished footwear choose pannunga. ");
        } else if (containsAny(q, "college", "casual")) {
            reply.append("College casual-ku comfort first: clean shirt/top, jeans/chinos, and sneakers best. ");
        } else {
            reply.append("StyleGPT suggestion: color balance, fit, and occasion match pannina outfit polished-a irukkum. ");
        }
        if (shouldShowProducts(message) && !products.isEmpty()) {
            reply.append("Recommended products: ");
            reply.append(products.stream().limit(4)
                    .map(p -> p.getName() + " (Rs." + p.getPrice().intValue() + ")")
                    .reduce((a, b) -> a + ", " + b).orElse(""));
            reply.append(".");
        }
        return reply.toString();
    }

    private boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) return true;
        }
        return false;
    }

    private boolean isFashionQuestion(String q) {
        return containsAny(q, "shirt", "pant", "jean", "shoe", "sneaker", "dress", "kurti", "outfit",
                "wedding", "college", "casual", "color", "match", "size", "fashion", "wear", "recommend",
                "suggest", "under", "budget", "grocery", "snack", "chips", "cookie", "watch", "sunglass");
    }

    private boolean shouldShowProducts(String message) {
        String q = message.toLowerCase(Locale.ROOT);
        return containsAny(q, "suggest", "recommend", "under", "budget", "product", "buy", "shoe",
                "dress", "shirt", "pant", "kurti", "grocery", "snack", "chips", "cookie", "watch", "sunglass", "outfit");
    }

    private BigDecimal extractBudget(String q) {
        String digits = q.replaceAll("[^0-9]", " ").trim().replaceAll("\\s+", " ");
        if (digits.isBlank()) return null;
        try {
            return new BigDecimal(digits.split(" ")[0]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
