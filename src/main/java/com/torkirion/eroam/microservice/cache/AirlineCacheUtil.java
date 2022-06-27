package com.torkirion.eroam.microservice.cache;

import com.torkirion.eroam.microservice.datadomain.*;
import com.torkirion.eroam.microservice.transport.datadomain.IcaoAircraft;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AirlineCacheUtil {

    private Map<String, IataAirportV2> cache = new HashMap<>();
    private Map<String, Airline> airlineCache = new HashMap<>();
    private Map<String, IcaoAircraft> icaoAircraftCache = new HashMap<>();

    @Autowired
    IataAirportV2Repo iataAirportV2Repo;
    @Autowired
    AirlineRepo airlineRepo;
    @Autowired
    IcaoAircraftRepo icaoAircraftRepo;

    public synchronized void setup() {
        List<IataAirportV2> airlines = iataAirportV2Repo.findAll();
        for(IataAirportV2 airline : airlines) {
            cache.put(airline.getIataCode(), airline);
        }
    }

    public synchronized void setupAirline() {
        List<Airline> airlineList = airlineRepo.findAll();
        airlineCache = airlineList.stream().collect(Collectors.toMap(Airline::getIataCode, Function.identity()));
    }

    public synchronized void setupIcaoAircraft() {
        List<IcaoAircraft> icaoAircraftList = icaoAircraftRepo.findAll();
        for(IcaoAircraft icaoAircraft : icaoAircraftList) {
            if (Objects.nonNull(icaoAircraft))
                icaoAircraftCache.put(icaoAircraft.getAircraftType(), icaoAircraft);
        }
    }

    public IataAirportV2 getByCode(String iataCode) {
        if(cache.isEmpty()) {
            setup();
        }
        if(!cache.containsKey(iataCode)) {
            return null;
        }
        return cache.get(iataCode);
    }

    public Airline getAirlineByCode(String iataCode) {
        if(airlineCache.isEmpty()) {
            setupAirline();
        }
        if(!airlineCache.containsKey(iataCode)) {
            return null;
        }
        return airlineCache.get(iataCode);
    }

    public IcaoAircraft getIcaoAircraftByCode(String aircraftType) {
        if(icaoAircraftCache.isEmpty()) {
            setupIcaoAircraft();
        }
        if(!icaoAircraftCache.containsKey(aircraftType)) {
            return null;
        }
        return icaoAircraftCache.get(aircraftType);
    }
}
