package com.example.chatclinetest;


import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.Map;

@SpringBootTest
public class TestPrompt {


    @Value("classpath:system-prompt.txt")
    private Resource systemPromptResource;

    @Test
    public void testPrompt(@Autowired ChatClient.Builder builder) {
        // 提示词模板文件
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPromptResource);

        // 提示词模板
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                        告诉我5部<composer>电影
                        """).build();

        String prompt = promptTemplate.render(Map.of("composer","John Williams"));

        // 设置接入外部系统提示词
        ChatClient chatClient1 = builder.defaultSystem(systemPromptResource).build();

        // 系统提示词
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
                
                当前服务的用户 姓名{name}, 性别：{sex}
                """).build();

        Flux<String> content = chatClient.prompt().system(p -> p.param("name", "张三").param("sex", "男"))
                // 用户提示词
                .user("你好").stream().content();
        content.toIterable().forEach(System.out::println);
    }
}
