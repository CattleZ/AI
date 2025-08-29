package com.example.chatclinetest.AutoAdvisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;


public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_RE_READING_CONTENT = """
            {re2_input_query}
            Read the question again: {re2_input_query}
            """;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获取之前的提示词
        String contents = chatClientRequest.prompt().getContents();
        // 将用户提示词 塞到DEFAULT_RE_READING_CONTENT 里面

        String re2InputQuery =  PromptTemplate.builder().template(DEFAULT_RE_READING_CONTENT).build().render(Map.of("re2_input_query", contents));

        ChatClientRequest chatClientRequest1 = chatClientRequest.mutate().prompt(Prompt.builder().content(re2InputQuery).build()).build();
        return chatClientRequest1;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
