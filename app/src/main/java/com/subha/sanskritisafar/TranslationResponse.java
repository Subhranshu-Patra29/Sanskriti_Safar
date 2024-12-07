package com.subha.sanskritisafar;

public class TranslationResponse {
    private String original_text;
    private String translated_text;
    private String source_language;
    private String target_language;

    // Getters and setters
    public String getOriginal_text() {
        return original_text;
    }

    public void setOriginal_text(String original_text) {
        this.original_text = original_text;
    }

    public String getTranslated_text() {
        return translated_text;
    }

    public void setTranslated_text(String translated_text) {
        this.translated_text = translated_text;
    }

    public String getSource_language() {
        return source_language;
    }

    public void setSource_language(String source_language) {
        this.source_language = source_language;
    }

    public String getTarget_language() {
        return target_language;
    }

    public void setTarget_language(String target_language) {
        this.target_language = target_language;
    }
}