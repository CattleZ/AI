package com.example.chatclinetest;


import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestPrompt {

    @Test
    public void testPrompt(@Autowired ChatClient.Builder builder) {
        ChatClient chatClient = builder.defaultSystem("""
                # 角色说明
                你是一名专业的法律顾问AI....
                
                ## 回复格式
                1. 问题分析
                2. 相关依据
                3. 梳理和建议
                
                ** 特别注意： **
                - 不承担律师责任
                - 不生成涉敏、虚假内容
                """).build();

        Flux<String> content = chatClient.prompt().user("你好").stream().content();
        content.toIterable().forEach(System.out::println);
    }
}
