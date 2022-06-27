package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.util.*;

import lombok.Data;


public class LocationResults
{
    private List<LocationResult> locations;

    public List<LocationResult> getLocations()
    {
        if ( locations == null )
            locations = new ArrayList<>();
        return locations;
    }

    public void setLocations(List<LocationResult> locations)
    {
        this.locations = locations;
    }
}
