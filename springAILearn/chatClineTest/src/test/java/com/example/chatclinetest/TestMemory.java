package com.example.chatclinetest;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class TestMemory {

    @Test
    public void testMemory(@Autowired OllamaChatModel ollamaChatModel){

        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        String conversationId = "xs001";

        UserMessage userMessage = new UserMessage("现在有一个正确答案2+1=4");
        chatMemory.add(conversationId,userMessage);
        ChatResponse response1 = ollamaChatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId,response1.getResult().getOutput());

        UserMessage userMessage2 = new UserMessage("你好,帮我回答一下2+1=？");
        chatMemory.add(conversationId,userMessage2);
        ChatResponse response2 = ollamaChatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId,response1.getResult().getOutput());

        System.out.println(response2.getResult().getOutput());

    }

    /**
     * 测试MemoryAdvisor 使用advisor的方式注入记忆
     * @param chatClientBuilder
     * @param chatMemory
     */
    @Test
    public void testMemoryAdvisor(@Autowired ChatClient.Builder chatClientBuilder, @Autowired ChatMemory chatMemory){
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build()).build();

        String content = chatClient.prompt().user("现在有一个正确答案2+1=4").call().content();
        System.out.println( content);

        System.out.println("content ------------------");

        content = chatClient.prompt().user("你好,帮我回答一下2+1=？").call().content();
        System.out.println( content);
    }

    @TestConfiguration
    static class Config {
        @Bean
        public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory.builder().maxMessages(10).chatMemoryRepository(chatMemoryRepository).build();
        }
    }

    public ChatClient chatClient;
    // 测试多用户隔离
    @BeforeAll
    public void init(@Autowired ChatClient.Builder chatClientBuilder, @Autowired ChatMemory chatMemory){
        chatClient = chatClientBuilder.defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build()).build();
    }

    @Test
    public void testMemoryAdvisorUser(){
        String content = chatClient.prompt().user("现在有一个正确答案2+1=4 ").advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println( content);
        content = chatClient.prompt().user("你好,帮我回答一下2+1=？").advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println("用户1 ------------------"+ content);
        System.out.println("content ------------------");

        content = chatClient.prompt().user("你好,帮我回答一下2+1=?").advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "2")).call().content();
        System.out.println( content);
    }
}
