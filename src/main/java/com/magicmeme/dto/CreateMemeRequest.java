package com.magicmeme.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateMemeRequest {

    @NotBlank(message = "imageDataUrl is required")
    private String imageDataUrl;

    @NotBlank(message = "topText is required")
    private String topText;

    @NotBlank(message = "bottomText is required")
    private String bottomText;

    @NotBlank(message = "template is required")
    private String template;

    @NotBlank(message = "tone is required")
    private String tone;

    public String getImageDataUrl() {
        return imageDataUrl;
    }

    public void setImageDataUrl(String imageDataUrl) {
        this.imageDataUrl = imageDataUrl;
    }

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }
}
