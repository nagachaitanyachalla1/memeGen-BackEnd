package com.magicmeme.dto;

import jakarta.validation.constraints.NotBlank;

public class ReactionRequest {

    @NotBlank(message = "emoji is required")
    private String emoji;

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
