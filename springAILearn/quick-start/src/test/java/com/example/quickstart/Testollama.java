package com.example.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class Testollama {

    @Test
    public void testollama(@Autowired OllamaChatModel ollamaChatModel)
    {
        // 软删除思考模式
        String content = ollamaChatModel.call("你好，你是谁?/no_think");
        // 关闭think 在 ollama中执行命令 /set nothink （系统中全局关闭）
        System.out.println(content);
    }

    @Test
    public void testStreamollama(@Autowired OllamaChatModel ollamaChatModel)
    {
        Flux<String> content = ollamaChatModel.stream("你好，你是谁?");
        content.toIterable().forEach(System.out::println);
    }

}
