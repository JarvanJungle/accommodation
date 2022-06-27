package com.torkirion.eroam.microservice.transport.services;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.datadomain.GeoCoordinates;
import com.torkirion.eroam.microservice.transport.datadomain.SaveATrainVendorStation;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi.SaveATrainBookApiInterface;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi.VendorStationRSDTO;
import com.torkirion.eroam.microservice.transport.repository.SaveATrainVendorStationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class TransportBackgroundService {

    private SaveATrainVendorStationRepository saveATrainVendorStationRepository;

    private SystemPropertiesDAO propertiesDAO;

    public void cloneAllVendorsStations(String site) throws Exception {
        log.info("cloneAllVendorsStations:: start (site: {}, at: {})", site, Calendar.getInstance().getTime());
        SaveATrainBookApiInterface saveATrainInterface = new SaveATrainBookApiInterface(propertiesDAO, site, SaveATrainService.CHANNEL);
        VendorStationRSDTO[] vendorStations = saveATrainInterface.startGetVendorStations(site);
        if(log.isDebugEnabled()) {
            log.debug("cloneAllVendorsStations::number of vendorStations: {}", vendorStations.length);
        }
        if(vendorStations == null || vendorStations.length < 1) {
            log.error("cloneAllVendorsStations::vendorStations is empty");
            throw new Exception("cloneAllVendorsStations::vendorStations is empty");
        }
        cleanBeforeSave();
        saveAllVendorsStations(vendorStations);
        log.info("cloneAllVendorsStations:: end (site: {}, vendorRow= {}, at: {})", site, vendorStations.length, Calendar.getInstance().getTime());
    }

    private void cleanBeforeSave() {
        if(log.isDebugEnabled()) {
            log.debug("cleanBeforeSave at: {}", Calendar.getInstance().getTime());
        }
        saveATrainVendorStationRepository.deleteAll();
    }

    private void saveAllVendorsStations(VendorStationRSDTO[] vendorStations) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("save or update all vendors stations to database ");
        }
        List<SaveATrainVendorStation> stationEntityList = new ArrayList<>();
        for(VendorStationRSDTO vendorStation : vendorStations) {
            SaveATrainVendorStation stationEntity = new SaveATrainVendorStation();
            stationEntity.setUid(vendorStation.getUid());
            stationEntity.setName(vendorStation.getName());
            stationEntity.setSearchable(vendorStation.getSearchable());


            GeoCoordinates geoCoordinates = new GeoCoordinates();
            try {
                geoCoordinates.setLatitude(vendorStation.getLatitude());
                geoCoordinates.setLongitude(vendorStation.getLongitude());
            } catch (Exception e) {
                log.error("saveAllVendorsStations::message: {}", e.getMessage());
            }

            stationEntity.setGeoCoordinates(geoCoordinates);

            stationEntityList.add(stationEntity);
        }
        saveATrainVendorStationRepository.saveAll(stationEntityList);
        if(log.isDebugEnabled()) {
            log.debug("completely save or update all vendors stations to database ");
        }
    }
}
