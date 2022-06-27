package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class InnstantRCDataSearch
{
    @Autowired
    private DestinationSearchRQDataRepo searchRQDataRepo;

    public void deleteSearch(){
    	LocalDateTime then = LocalDateTime.now().minusDays(1);
        searchRQDataRepo.deleteByCreationDateTimeBefore(then);
    }
}
