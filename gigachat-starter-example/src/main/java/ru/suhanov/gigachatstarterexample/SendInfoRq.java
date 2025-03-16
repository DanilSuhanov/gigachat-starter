package ru.suhanov.gigachatstarterexample;

import lombok.Data;
import ru.suhanov.gigachatstarter.generator.tooldesc.annotation.Description;
import ru.suhanov.gigachatstarter.generator.tooldesc.annotation.NotRequired;

@Data
public class SendInfoRq {
    @Description("Номер посылки")
    private String sendNum;
    @Description("Дополнительная информация")
    private DopInfo dopInfo;

    @Data
    public static class DopInfo {
        @Description("Информация а")
        @NotRequired
        private String aInfo;
        @Description("Информация б")
        @NotRequired
        private String bInfo;
    }
}
