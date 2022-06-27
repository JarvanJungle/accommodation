package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.CountryStaticDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "innstant_country_static")
@Data
@ToString
@NoArgsConstructor
public class CountryStaticData implements Serializable{

    public CountryStaticData(CountryStaticDTO countryStaticDTO) throws JsonProcessingException {
        this.id = countryStaticDTO.getId();
        this.continent = countryStaticDTO.getContinent();
        this.name = countryStaticDTO.getName();
        this.region = countryStaticDTO.getRegion();
        this.regionsJson = new ObjectMapper().writeValueAsString(countryStaticDTO.getRegions());
    }

    @Id
    private String id;

    private String continent;

    private String name;

    private String region;

    @Column(columnDefinition = "TEXT")
    private String regionsJson = null;
}
