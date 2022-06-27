package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.torkirion.eroam.microservice.hirecars.dto.DetailRQDTO;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util.CarnectUtil;
import lombok.Data;

@Data
public class StartDetailRQDTO {
    private String client;
    private String codeContext;
    private String idContext;
    private String pickupLocationCode;
    private String dropOffLocationCode;

    public static StartDetailRQDTO makeStartDetailRQDtoFromDetailRQDto(DetailRQDTO detailsRQ) throws Exception{
        StartDetailRQDTO startDetailRQ = new StartDetailRQDTO();
        startDetailRQ.setClient(detailsRQ.getClient());
        String vehicleId = detailsRQ.getVehicleId();
        CarnectUtil.CarnectKey carnectKey = CarnectUtil.getInstance().makeCarnectKeyFromVehicleId(vehicleId);
        startDetailRQ.setCodeContext(carnectKey.getCodeContext());
        startDetailRQ.setIdContext(carnectKey.getIdContext());
        startDetailRQ.setPickupLocationCode(carnectKey.getPickupLocationCode());
        startDetailRQ.setDropOffLocationCode(carnectKey.getDropOffLocationCode());
        return startDetailRQ;
    }
}
