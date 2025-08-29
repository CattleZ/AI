package com.example.chatclinetest;


import com.example.chatclinetest.AutoAdvisor.ReReadingAdvisor;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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


    /**
     * 敏感词拦截
     * @param chatClientBuilder
     */
    @Test
    public void testSensitiveAdvisor(@Autowired ChatClient.Builder chatClientBuilder)
    {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor(), new SafeGuardAdvisor(List.of("sb"))).build();
        String content = chatClient.prompt().user("你好,gorge,sb").call().content();
        System.out.println( content);
    }

    @Test
    public void testReReadAdvisor(@Autowired ChatClient.Builder chatClientBuilder)
    {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor(), new ReReadingAdvisor()).build();
        String content = chatClient.prompt().user("你好,gorge,sb").call().content();
        System.out.println( content);
    }
}
