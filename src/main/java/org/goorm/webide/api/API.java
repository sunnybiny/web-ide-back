package org.goorm.webide.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = SnakeCaseStrategy.class)
public class API<T>{
    private String resultCode;

    private String resultMessage;

    private T data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(value = SnakeCaseStrategy.class)
    public static class Error{
        private List<String> errorMessages;
    }
}
