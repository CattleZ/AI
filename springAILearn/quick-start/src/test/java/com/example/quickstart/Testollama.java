package com.example.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Testollama {

    @Test
    public void testollama(@Autowired OllamaChatModel ollamaChatModel)
    {
        String content = ollamaChatModel.call("你好，你是谁?");
        System.out.println(content);
    }
}
