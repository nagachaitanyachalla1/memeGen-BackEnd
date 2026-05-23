package com.magicmeme.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicmeme.config.OpenRouterProperties;
import com.magicmeme.dto.MemeSuggestionDto;
import com.magicmeme.exception.AiServiceException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterService {

    private static final String BASE_PROMPT = """
            You are a viral meme writer for a fast, funny AI meme app.
            Your job is not to describe the image. Your job is to find the funniest possible joke hidden in it.

            Study the uploaded image carefully:
            - facial expression
            - body language
            - eye contact
            - mood
            - awkwardness
            - chaos level
            - visual irony
            - what the person/object seems to be thinking
            - what relatable situation this image could represent

            Generate exactly 6 DISTINCT meme suggestions that people would actually want to share.
            Each suggestion must use a different humor angle, not just different wording.

            Each suggestion must contain:
            - id
            - title
            - tone
            - topText
            - bottomText
            - reason

            Tone selection:
            %s

            Meme writing rules:
            - Be funny first. Prefer punchlines over explanations.
            - Captions must be short, sharp, and easy to read on an image.
            - Top text should set up the relatable situation.
            - Bottom text should deliver the twist, betrayal, roast, or punchline.
            - Make the joke tightly connected to the uploaded image's expression, pose, object, or visual mood.
            - Use internet-native phrasing, but do not overuse slang.
            - Use chaotic, awkward, roast-style humor, but keep it playful and non-offensive.
            - Try humor angles like fake confidence, instant regret, overthinking, social anxiety, exam panic, family pressure, group chat drama, relationship confusion, main-character meltdown, broke energy, and silent judgment when they fit the image.
            - Use Indian/desi flavor only when it makes the joke better, not randomly.
            - Use Hinglish only if it sounds natural and funnier than English.
            - Top and bottom text must be uppercase.
            - topText should usually be 3 to 9 words.
            - bottomText should usually be 3 to 11 words.
            - Make every suggestion feel different from the others.
            - The reason must briefly explain why the joke fits the image.

            Quality filter:
            - Reject any joke that could fit any random image.
            - Reject generic captions like "WHEN LIFE HAPPENS", "ME EVERY DAY", "THAT MOMENT WHEN", "EXPECTATION VS REALITY", or "MONDAY MOOD".
            - Reject bland corporate/coding jokes unless the image strongly suggests that context.
            - Reject long motivational, wholesome-only, or caption-contest style lines.
            - Reject jokes that need private context not visible in the image.
            - Before finalizing, mentally rank your ideas and return only the 6 funniest.

            Safety rules:
            - Avoid offensive, hateful, political, religious, sexual, or discriminatory jokes.
            - Avoid jokes about real identity traits like race, caste, religion, disability, gender, sexuality, nationality, or body type.
            - Do not identify or name real people in the image.

            Output rules:
            - Return ONLY valid JSON array.
            - No markdown.
            - No explanation outside JSON.
            """;

    private static final String USER_TONE_RULE = """
            The user requested this tone: "%s".
            Include that requested tone in exactly one suggestion.
            Make that requested-tone suggestion genuinely funny, not forced.
            Choose the other 5 tones yourself based on what would be funniest for this specific image.
            All 6 tones must be distinct.
            """;

    private static final String AI_TONE_RULE = """
            The user did not request a tone.
            You must choose the 6 funniest distinct tones for this specific image.
            Do not use a fixed tone list unless those tones genuinely fit the image.
            Choose tones like a meme page admin, not like a formal assistant.
            Example tone styles can include savage, desi, gen-z, student-life, relationship-chaos, dramatic, awkward, roast, overthinking, family-group, exam-panic, broke-energy, main-character, fake-confidence, silent-judgment, or any better image-specific tone.
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final OpenRouterProperties properties;

    public OpenRouterService(RestClient restClient, ObjectMapper objectMapper, OpenRouterProperties properties) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public List<MemeSuggestionDto> generateSuggestions(MultipartFile image, String requestedTone) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new AiServiceException("AI meme suggestions are unavailable because OpenRouter API key is missing.");
        }

        try {
            String dataUrl = toDataUrl(image);
            Map<String, Object> requestBody = buildRequestBody(dataUrl, requestedTone);

            String response = restClient.post()
                    .uri(properties.getUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("HTTP-Referer", "http://localhost:5173")
                    .header("X-Title", "Magic Meme")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseSuggestions(response);
        } catch (Exception ex) {
            throw new AiServiceException("AI meme suggestions are unavailable right now. Please try again later.", ex);
        }
    }

    private String toDataUrl(MultipartFile image) throws IOException {
        String contentType = image.getContentType();
        String base64 = Base64.getEncoder().encodeToString(image.getBytes());
        return "data:" + contentType + ";base64," + base64;
    }

    private Map<String, Object> buildRequestBody(String dataUrl, String requestedTone) {
        Map<String, Object> textPart = new LinkedHashMap<>();
        textPart.put("type", "text");
        textPart.put("text", buildPrompt(requestedTone));

        Map<String, Object> imageUrl = new LinkedHashMap<>();
        imageUrl.put("url", dataUrl);

        Map<String, Object> imagePart = new LinkedHashMap<>();
        imagePart.put("type", "image_url");
        imagePart.put("image_url", imageUrl);

        Map<String, Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", List.of(textPart, imagePart));

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", properties.getModel());
        requestBody.put("messages", List.of(userMessage));
        requestBody.put("temperature", 1.05);

        return requestBody;
    }

    private String buildPrompt(String requestedTone) {
        String toneRule = StringUtils.hasText(requestedTone)
                ? USER_TONE_RULE.formatted(requestedTone.trim())
                : AI_TONE_RULE;
        return BASE_PROMPT.formatted(toneRule);
    }

    private List<MemeSuggestionDto> parseSuggestions(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        if (contentNode.isMissingNode() || !StringUtils.hasText(contentNode.asText())) {
            throw new AiServiceException("OpenRouter returned an empty AI response.");
        }

        String cleanedJson = extractJsonArray(cleanMarkdownFences(contentNode.asText()));
        List<MemeSuggestionDto> suggestions = objectMapper.readValue(
                cleanedJson,
                new TypeReference<List<MemeSuggestionDto>>() {
                }
        );

        if (suggestions.size() != 6) {
            throw new AiServiceException("OpenRouter did not return exactly 6 meme suggestions.");
        }

        return normalizeSuggestionIds(suggestions);
    }

    private String cleanMarkdownFences(String content) {
        String cleaned = content.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "");
            cleaned = cleaned.replaceFirst("\\s*```$", "");
        }
        return cleaned.trim();
    }

    private String extractJsonArray(String content) {
        int start = content.indexOf('[');
        int end = content.lastIndexOf(']');
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("No JSON array found in OpenRouter response");
        }
        return content.substring(start, end + 1);
    }

    private List<MemeSuggestionDto> normalizeSuggestionIds(List<MemeSuggestionDto> suggestions) {
        List<MemeSuggestionDto> normalized = new ArrayList<>();
        for (int i = 0; i < suggestions.size(); i++) {
            MemeSuggestionDto suggestion = suggestions.get(i);
            if (!StringUtils.hasText(suggestion.getId())) {
                suggestion.setId("suggestion-" + (i + 1));
            }
            normalized.add(suggestion);
        }
        return normalized;
    }
}
