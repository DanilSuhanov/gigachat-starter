package ru.suhanov.gigachatstarterexample;

import org.springframework.stereotype.Service;
import ru.suhanov.gigachatstarter.generator.tooldesc.annotation.Description;
import ru.suhanov.gigachatstarter.generator.tooldesc.annotation.Tool;

@Service
public class PostaService {

    @Tool(name = "getSendInfo", description = "Получение информации по посылке")
    public SendInfoRs getSendInfo(
            @Description("Запрос на получение инфомации по посылке") SendInfoRq sendInfoRq
    ) {
        return new SendInfoRs(new SendInfoRs.AdditionInfo("SUCCESS"), "send is ok", null);
    }
}
