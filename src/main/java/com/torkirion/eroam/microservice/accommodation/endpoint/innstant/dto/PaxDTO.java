package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PaxDTO
{
    private Integer adults;
    private List<Integer> children;
}
