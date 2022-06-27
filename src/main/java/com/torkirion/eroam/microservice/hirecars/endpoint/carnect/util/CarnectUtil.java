package com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util;

import lombok.Data;

public class CarnectUtil {
    private static CarnectUtil instance;

    public static CarnectUtil getInstance() {
        if(instance == null) {
            instance =  new CarnectUtil();
        }
        return instance;

    }

    public CarnectKey makeCarnectKeyFromVehicleId(String vehicleId) throws Exception {
        CarnectKey carnectKey = new CarnectKey();
        if(vehicleId == null || "".equals(vehicleId.trim())) {
            throw new Exception("miss vehicleId");
        }
        vehicleId = vehicleId.trim();
        String[] items = vehicleId.split("@");
        if(items.length < 4) {
            throw new Exception("invalid vehicleId");
        }
        carnectKey.setCodeContext(items[0]);
        carnectKey.setIdContext(items[1]);
        carnectKey.setPickupLocationCode(items[2]);
        carnectKey.setDropOffLocationCode(items[3]);
        return carnectKey;
    }

    @Data
    public static class CarnectKey {
        private String codeContext;
        private String idContext;
        private String pickupLocationCode;
        private String dropOffLocationCode;
    }
}
