package com.example.chatclinetest;


import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
