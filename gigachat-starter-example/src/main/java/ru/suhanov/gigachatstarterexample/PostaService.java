package ru.suhanov.gigachatstarterexample;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PostaService {

    @Tool
    public SendInfoRs getSendInfo(
            String sendNum
    ) {
        return new SendInfoRs(new SendInfoRs.AdditionInfo("SUCCESS"), "send is ok", null);
    }
}
