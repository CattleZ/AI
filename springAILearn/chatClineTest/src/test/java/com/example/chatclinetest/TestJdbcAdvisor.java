package com.example.chatclinetest;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class TestJdbcAdvisor {

    /**
     * 只有在当前的测试类中生效
     */
    @TestConfiguration
    static class Config {
        @Bean
        public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository){

            return MessageWindowChatMemory.builder()
                    .maxMessages(1)
                    .chatMemoryRepository(chatMemoryRepository)
                    .build();
        }
    }

}
