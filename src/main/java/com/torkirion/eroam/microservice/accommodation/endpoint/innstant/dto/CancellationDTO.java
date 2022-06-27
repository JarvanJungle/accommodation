package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CancellationDTO
{
    @Data
    public static class Frame {
        private String from;
        private String to;
        private AmountAndCurrencyDTO penalty;
    }
    private String type;
    private List<Frame> frames;
}
