package com.vasant.AIProjectBackend.controllers;


import com.vasant.AIProjectBackend.entities.AudioResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/openai")
//@CrossOrigin("*")
public class ChatCompletionController {
    private static final Logger logger = LoggerFactory.getLogger(ChatCompletionController.class);

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

    @PostMapping("/image-detection")
    public String detectObjects(
            @RequestParam("image") MultipartFile image,
            @RequestParam("prompt") String prompt) throws Exception {
        logger.info("detectObjects called with image: {} and prompt: {}", image.getOriginalFilename(), prompt);

        // Use image.getInputStream() to access the image data
        var userMessage = UserMessage.builder()
                .text(prompt)
                .media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, new InputStreamResource(image.getInputStream()))))
                .build();

        ChatResponse response = chatModel.call(
                new Prompt(userMessage,
                        OpenAiChatOptions.builder()
                                .model(OpenAiApi.ChatModel.GPT_4_O.getValue())
                                .build())
        );
        String result = response.getResult().getOutput().getText();
        logger.info("detectObjects result: {}", result);
        return result;
    }

    @GetMapping("/audio-generation/{prompt}")
    public AudioResult generateAudio(@PathVariable String prompt) throws Exception {
        var userMessage = new UserMessage(prompt);

        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_AUDIO_PREVIEW)
                        .outputModalities(List.of("text", "audio"))
                        .outputAudio(new OpenAiApi.ChatCompletionRequest.AudioParameters(OpenAiApi.ChatCompletionRequest.AudioParameters.Voice.ALLOY, OpenAiApi.ChatCompletionRequest.AudioParameters.AudioResponseFormat.WAV))
                        .build()));

        String text = response.getResult().getOutput().getText(); // audio transcript

        byte[] waveAudio = response.getResult().getOutput().getMedia().get(0).getDataAsByteArray(); // audio data

        return new AudioResult(text, waveAudio);
    }


}
