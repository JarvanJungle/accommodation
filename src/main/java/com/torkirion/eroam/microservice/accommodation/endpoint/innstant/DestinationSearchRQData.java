package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "innstant_destination_search")
@Data
@ToString
@NoArgsConstructor
public class DestinationSearchRQData implements Serializable{

    public DestinationSearchRQData(List<InnstantAvailabilityRQ.Destination> destinations) throws JsonProcessingException {
        this.value = new ObjectMapper().writeValueAsString(destinations);
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private LocalDateTime creationDateTime = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String value = null;
}
