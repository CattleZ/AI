package com.example.quickstart;


import ch.qos.logback.core.util.FileUtil;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@SpringBootTest
public class TestQw {

    @Test
    public void testQw(@Autowired DashScopeChatModel dashScopeChatModel)
    {
        String content = dashScopeChatModel.call("你好，你是谁");
        System.out.println(content);
    }

    /**
     * 测试文生图
     */

    @Test
    public void test2Img(@Autowired DashScopeImageModel imageModeldash) {
        DashScopeImageOptions imageOptions = DashScopeImageOptions.builder()
                .withModel("wanx2.1-t2i-turbo")
                //.withN()
                //.withWidth()
                .build();

        ImageResponse imageResponse = imageModeldash.call(new ImagePrompt("小朋友在打篮球", imageOptions));

        String imageUrl = imageResponse.getResult().getOutput().getUrl();

        System.out.println(imageUrl);
    }

    @Test
    public void test2Audio(@Autowired DashScopeSpeechSynthesisModel synthesisModel){
        DashScopeSpeechSynthesisOptions options = DashScopeSpeechSynthesisOptions.builder()
//                .voice("zh-cn-xiaoxiao-neural")
//                .speed()
//                .model()
//                .responseFormat()
                .build();

        SpeechSynthesisResponse response = synthesisModel.call(new SpeechSynthesisPrompt("你好，我是DashScope的语音合成模型", options));



        // 将byteBufffer写入文件
        File file = new File(System.getProperty("user.dir") + "/audio_dashscope.mp3");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ByteBuffer byteBuffer = response.getResult().getOutput().getAudio();
            fos.write(byteBuffer.array());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void test2Video() throws NoApiKeyException, InputRequiredException {
        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param = VideoSynthesisParam.builder()
                .model("wanx2.1-t2v-turbo")
                .prompt("两只小猫在打架")
                .size("1280*720")
                .apiKey(System.getenv("ALI_AI_KEY"))
                .build();

        System.out.println("please wait...");
        VideoSynthesisResult result = vs.call(param);
        System.out.println(result.getOutput().getVideoUrl());
    }
}
