package ru.suhanov.gigachatstarterexample;

import org.springframework.stereotype.Service;
import ru.suhanov.gigachatstarter.gigachatapiservice.annotation.Description;
import ru.suhanov.gigachatstarter.gigachatapiservice.annotation.Required;
import ru.suhanov.gigachatstarter.gigachatapiservice.annotation.Tool;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.toolProvider.AvailableForToolParse;

@Service
public class PostaService implements AvailableForToolParse {

    @Tool(name = "getSendInfo", description = "Получение информации по посылке")
    public SendInfoRs getSendInfo(
            @Description("Номер посылки") @Required String sendNum
    ) {
        return new SendInfoRs(new SendInfoRs.AdditionInfo("SUCCESS"), "send is ok", null);
    }
}
