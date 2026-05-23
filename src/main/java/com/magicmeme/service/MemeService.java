package com.magicmeme.service;

import com.magicmeme.dto.CreateMemeRequest;
import com.magicmeme.dto.MemeResponse;
import com.magicmeme.exception.InvalidRequestException;
import com.magicmeme.exception.ResourceNotFoundException;
import com.magicmeme.model.Meme;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemeService {

    private static final List<String> ALLOWED_EMOJIS = List.of("😂", "🔥", "💀", "🤯", "❤️");

    private final ConcurrentHashMap<String, Meme> memes = new ConcurrentHashMap<>();

    public MemeResponse saveMeme(CreateMemeRequest request) {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        Meme meme = new Meme(
                id,
                request.getImageDataUrl(),
                request.getTopText(),
                request.getBottomText(),
                request.getTemplate(),
                request.getTone(),
                defaultReactions(),
                LocalDateTime.now()
        );

        memes.put(id, meme);
        return toResponse(meme);
    }

    public List<MemeResponse> getGalleryMemes() {
        return memes.values().stream()
                .sorted(Comparator.comparing(Meme::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public MemeResponse getMeme(String id) {
        return toResponse(findMeme(id));
    }

    public MemeResponse reactToMeme(String id, String emoji) {
        if (!ALLOWED_EMOJIS.contains(emoji)) {
            throw new InvalidRequestException("Unsupported emoji. Allowed emojis: " + String.join(" ", ALLOWED_EMOJIS));
        }

        Meme meme = findMeme(id);
        meme.getReactions().compute(emoji, (key, count) -> count == null ? 1 : count + 1);
        return toResponse(meme);
    }

    private Meme findMeme(String id) {
        Meme meme = memes.get(id);
        if (meme == null) {
            throw new ResourceNotFoundException("Meme not found: " + id);
        }
        return meme;
    }

    private Map<String, Integer> defaultReactions() {
        Map<String, Integer> reactions = new LinkedHashMap<>();
        for (String emoji : ALLOWED_EMOJIS) {
            reactions.put(emoji, 0);
        }
        return reactions;
    }

    private MemeResponse toResponse(Meme meme) {
        return new MemeResponse(
                meme.getId(),
                "/m/" + meme.getId(),
                meme.getImageDataUrl(),
                meme.getTopText(),
                meme.getBottomText(),
                meme.getTemplate(),
                meme.getTone(),
                orderedReactions(meme),
                meme.getCreatedAt()
        );
    }

    private Map<String, Integer> orderedReactions(Meme meme) {
        Map<String, Integer> ordered = new LinkedHashMap<>();
        for (String emoji : ALLOWED_EMOJIS) {
            ordered.put(emoji, meme.getReactions().getOrDefault(emoji, 0));
        }
        return ordered;
    }
}
