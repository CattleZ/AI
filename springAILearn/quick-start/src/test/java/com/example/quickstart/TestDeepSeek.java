package com.example.quickstart;


import org.junit.jupiter.api.Test;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestDeepSeek {

    @Test
    public void testDeepSeek(@Autowired DeepSeekChatModel deepSeekChatModel) {
        String content = deepSeekChatModel.call("你好，你是谁");
        System.out.println(content);
    }


}
