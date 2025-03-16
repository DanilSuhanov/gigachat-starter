package ru.suhanov.gigachatstarterexample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.gigachatstarter.generator.tooldesc.annotation.Description;
import ru.suhanov.gigachatstarter.generator.tooldesc.annotation.NotRequired;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendInfoRs {
    @Description("Дополнительная информация")
    @NotRequired
    private AdditionInfo additionInfo;

    @Description("Основная информация")
    private String sendInfo;

    @NotRequired
    private List<String> strings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionInfo {
        @Description("Статус")
        private String status;
    }
}
