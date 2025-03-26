package ru.suhanov.gigachatstarterexample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.gigachatstarter.gigachatapiservice.annotation.Description;
import ru.suhanov.gigachatstarter.gigachatapiservice.annotation.Required;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendInfoRs {
    @Description("additionInfo")
    private AdditionInfo additionInfo;

    @Required
    @Description("sendInfo")
    private String sendInfo;

    private List<String> strings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionInfo {
        @Description("status")
        private String status;
    }
}
