package com.magicmeme.controller;

import com.magicmeme.dto.CreateMemeRequest;
import com.magicmeme.dto.MemeResponse;
import com.magicmeme.dto.MemeSuggestionDto;
import com.magicmeme.dto.ReactionRequest;
import com.magicmeme.exception.InvalidRequestException;
import com.magicmeme.service.MemeService;
import com.magicmeme.service.OpenRouterService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/memes")
public class MemeController {

    private final MemeService memeService;
    private final OpenRouterService openRouterService;

    public MemeController(MemeService memeService, OpenRouterService openRouterService) {
        this.memeService = memeService;
        this.openRouterService = openRouterService;
    }

    @PostMapping(value = "/suggest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<MemeSuggestionDto> suggestMemes(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "tone", required = false) String tone) {
        validateImage(image);
        return openRouterService.generateSuggestions(image, tone);
    }

    @PostMapping
    public MemeResponse saveMeme(@Valid @RequestBody CreateMemeRequest request) {
        return memeService.saveMeme(request);
    }

    @GetMapping
    public List<MemeResponse> getGalleryMemes() {
        return memeService.getGalleryMemes();
    }

    @GetMapping("/{id}")
    public MemeResponse getMeme(@PathVariable String id) {
        return memeService.getMeme(id);
    }

    @PostMapping("/{id}/react")
    public MemeResponse reactToMeme(@PathVariable String id, @Valid @RequestBody ReactionRequest request) {
        return memeService.reactToMeme(id, request.getEmoji());
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidRequestException("image file is required");
        }

        String contentType = image.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            throw new InvalidRequestException("image must have a content type starting with image/");
        }
    }
}
