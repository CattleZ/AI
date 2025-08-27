package com.example.chatclinetest;


import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestChatClient {

    @Test
    public void testChatClient(@Autowired ChatClient.Builder chatClientBuilder) throws Exception {
        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient.prompt().user("你好").call().content();
        System.out.println( content);
    }
}
