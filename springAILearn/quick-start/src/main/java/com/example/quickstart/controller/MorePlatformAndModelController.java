package com.example.quickstart.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.quickstart.dao.MorePlatformAndModelOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@RestController
public class MorePlatformAndModelController {

    HashMap<String, ChatModel> modelHashMap = new HashMap<>();

    public MorePlatformAndModelController(DashScopeChatModel dashScopeChatModel,
    OllamaChatModel ollamaChatModel) {
       modelHashMap.put("dashscope", dashScopeChatModel);
       modelHashMap.put("ollama", ollamaChatModel);
    }
    @RequestMapping("/chat")
    public Flux<String> chat(String message, MorePlatformAndModelOptions options) {
        String platform = options.getPlntform();
        ChatModel model = modelHashMap.get(platform);
        ChatClient.Builder builder = ChatClient.builder(model);

        ChatClient chatClient = builder.defaultOptions(ChatOptions.builder().temperature(options.getTemperature()).model(options.getModel()).build())
                .build();

        Flux<String> content =  chatClient.prompt().user(message).stream().content();
        content.toIterable().forEach(System.out::println);
        return Flux.just("hello world");
    }

}
