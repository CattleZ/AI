package com.example.chatclinetest;


import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestAdvisor {

    @Test
    public void testAdvisor(@Autowired ChatClient.Builder chatClientBuilder)
    {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor()).build();
        String content = chatClient.prompt().user("你好").call().content();
        System.out.println( content);
    }
}
