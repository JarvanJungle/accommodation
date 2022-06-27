package com.torkirion.eroam.microservice.transport.dto;

import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.transport.apidomain.TransportChooseRQ;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class TransportChosenRQDTO {

    private List<ChosenItem> items = new ArrayList<>();
    private TravellerMix travellers = new TravellerMix();
    private String channel = SaveATrainService.CHANNEL;

    @Data
    public static class ChosenItem {
        private LocalDateTime departureDatetime;
        private String originStationUid;
        private String destinationStationUid;
    }

    public static TransportChosenRQDTO makeTransportChosenRQDTO(TransportChooseRQ chooseRQ) {
        TransportChosenRQDTO dto = new TransportChosenRQDTO();
        dto.setChannel(chooseRQ.getChannel());
        dto.getTravellers().setAdultCount(chooseRQ.getTotalPassenger().getTotalAdult());
        if (chooseRQ.getTotalPassenger().getChild() != null)
        {
            for (List<Integer> l1 : chooseRQ.getTotalPassenger().getChild())
            {
                for (Integer l2 : l1)
                {
                    dto.getTravellers().getChildAges().add(l2);
                }
            }
        }
        dto.getItems().addAll(makeItems(chooseRQ.getChosenRouteId()));
        return dto;
    }

    private static List<ChosenItem> makeItems(String chosenRouteId) {
        List<ChosenItem> items = new ArrayList<>();
        if(chosenRouteId == null || "".equals(chosenRouteId)) {
            return Collections.EMPTY_LIST;
        }
        /*get list of route*/
        chosenRouteId = chosenRouteId.substring((SaveATrainService.CHANNEL_PREFIX + "_").length());
        String[] routeStrs = chosenRouteId.split("-");
        if(routeStrs.length < 1) {
            return Collections.EMPTY_LIST;
        }
        /* remove ST_ prefix */
        for(int i = 0; i < routeStrs.length; i++) {
            String routeStr = routeStrs[i];
            String[] routeElements = routeStr.split("\\|");
            if(routeElements.length < 3) {
                continue;
            }
            ChosenItem item = new ChosenItem();
            item.setDepartureDatetime(LocalDateTime.parse(routeElements[0], SaveATrainService.yyyymmddHHmm));
            item.setOriginStationUid(routeElements[1]);
            item.setDestinationStationUid(routeElements[2]);
            items.add(item);
        }
        return items;
    }
}
