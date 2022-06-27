package com.torkirion.eroam.microservice.cruise.services;

import com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.cruise.endpoint.CruiseServiceIF;
import com.torkirion.eroam.microservice.cruise.endpoint.traveltek.TravelTekRCController;
import com.torkirion.eroam.microservice.cruise.endpoint.traveltek.TravelTekService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import javax.annotation.Resource;

@Service
@AllArgsConstructor
@Slf4j
public class CruiseChannelService {
    @Autowired
    private SystemPropertiesDAO propertiesDAO;
    @Resource
    private SidDataRepo sidDataRepo;
    @Resource
    private ShipDataRepo shipDataRepo;
    @Resource
    private CruiseDataRepo cruiseDataRepo;
    @Resource
    private PortDataRepo portDataRepo;
    @Resource
    private RegionDataRepo regionDataRepo;
    @Resource
    private CruiseLineDataRepo cruiseLineDataRepo;
    @Autowired
    private TravelTekRCController travelTekRCController;

    public CruiseServiceIF getCruiseService(String channel) {
        log.debug("getCruiseService::channel=" + channel);
        if (channel.equals(TravelTekService.CHANNEL)) {
            TravelTekService travelTekService =
                    new TravelTekService(propertiesDAO, sidDataRepo, shipDataRepo, cruiseDataRepo, portDataRepo, regionDataRepo, travelTekRCController, cruiseLineDataRepo);
            return travelTekService;
        }
        log.warn("getCruiseService::unknown channel" + channel);
        return null;
    }

    public void clearChannelCaches() {
        if (log.isDebugEnabled())
            log.debug("clearChannelCaches::enter");
    }

    public SystemPropertiesDescription.ProductType getSystemPropertiesDescription() {
        SystemPropertiesDescription.ProductType productPropertiesDescription = new SystemPropertiesDescription.ProductType();
        productPropertiesDescription.getChannels().put(TravelTekService.CHANNEL, TravelTekService.getSystemPropertiesDescription());
        return productPropertiesDescription;
    }
}
