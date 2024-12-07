package com.subha.sanskritisafar;

public class RequestBody {
    private String prompt;

    public RequestBody(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}

