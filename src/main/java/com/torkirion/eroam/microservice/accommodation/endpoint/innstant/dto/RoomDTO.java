package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomDTO
{
    //data room
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Quantity
    {
        private Integer max;
        private Integer min;
    }

    private Integer hotelId;

    private String name;

    private String category;

    private String board;

    private String bedding;

    private PaxDTO pax;

    private Quantity quantity;
}
