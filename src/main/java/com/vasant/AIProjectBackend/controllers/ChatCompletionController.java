package com.vasant.AIProjectBackend.controllers;


import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openai")
@CrossOrigin("*")
public class ChatCompletionController {
    private OpenAiChatModel chatModel;
    private OpenAiImageModel imageModel;

    public ChatCompletionController(OpenAiChatModel chatModel, OpenAiImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    @GetMapping("/chat-completion/{message}")
    public ResponseEntity<String> getAnswer(@PathVariable String message){

        String response = chatModel.call(message);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/image-generation/{message}")
    public String getImage(@PathVariable String message) {
        OpenAiImageOptions imageOptions = OpenAiImageOptions.builder()
                .width(1024)
                .height(1024)
                .N(1)
                .quality("standard")
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(message, imageOptions);

        return imageModel.call(imagePrompt).getResult().getOutput().getUrl();
    }
}
