package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FormalViatorV2ScheduleData  {
    private String productCode;
    private String productOptionCode;
    private LocalTime time;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal latitude;
    private BigDecimal longitude;

    private Boolean sunday = false;
    private Boolean monday = false;
    private Boolean tuesday = false;
    private Boolean wednesday = false;
    private Boolean thursday = false;
    private Boolean friday = false;
    private Boolean saturday = false;

    private String currencyId;
    private Boolean special = false;

    private BigDecimal infantPriceNet;
    private BigDecimal infantPriceRrp;

    private BigDecimal childPriceNet;
    private BigDecimal childPriceRrp;

    private BigDecimal youthPriceNet;
    private BigDecimal youthPriceRrp;

    private BigDecimal adultPriceNet;
    private BigDecimal adultPriceRrp;

    private BigDecimal seniorPriceNet;
    private BigDecimal seniorPriceRrp;

    public String key() {
        return  new StringBuilder(productCode).append(startDate).append(time).append(productOptionCode).toString();
    }

    public static List<FormalViatorV2ScheduleData> transform(List<ViatorV2ScheduleData> origin) {
        Map<String, FormalViatorV2ScheduleData> storeMap = new HashMap<>();
        for(ViatorV2ScheduleData originItem : origin) {
            FormalViatorV2ScheduleData formal = map(originItem);
            if(storeMap.containsKey(formal.key())) {
                merger(storeMap.get(formal.key()), originItem);
            } else {
                merger(formal, originItem);
                storeMap.put(formal.key(), formal);
            }
        }
        return new ArrayList<FormalViatorV2ScheduleData>(storeMap.values());
    }

    private static void merger(FormalViatorV2ScheduleData root, ViatorV2ScheduleData originItem) {
        if("ADULT".equals(originItem.getAgeBand())) {
            root.setAdultPriceNet(originItem.getPriceNet());
            root.setAdultPriceRrp(originItem.getPriceRrp());
        }
        if("CHILD".equals(originItem.getAgeBand())) {
            root.setChildPriceNet(originItem.getPriceNet());
            root.setChildPriceRrp(originItem.getPriceRrp());
        }
        if("INFANT".equals(originItem.getAgeBand())) {
            root.setInfantPriceNet(originItem.getPriceNet());
            root.setInfantPriceRrp(originItem.getPriceRrp());
        }
        if("SENIOR".equals(originItem.getAgeBand())) {
            root.setSeniorPriceNet(originItem.getPriceNet());
            root.setSeniorPriceRrp(originItem.getPriceRrp());
        }
        if("YOUTH".equals(originItem.getAgeBand())) {
            root.setYouthPriceNet(originItem.getPriceNet());
            root.setYouthPriceRrp(originItem.getPriceRrp());
        }
    }

    private static FormalViatorV2ScheduleData map(ViatorV2ScheduleData  originItem) {
        FormalViatorV2ScheduleData formal = new FormalViatorV2ScheduleData();
        formal.setProductCode(originItem.getProductCode());
        formal.setStartDate(originItem.getStartDate());
        formal.setEndDate(originItem.getEndDate());
        formal.setTime(originItem.getTime());
        formal.setLatitude(originItem.getLatitude());
        formal.setLongitude(originItem.getLongitude());

        formal.setSunday(originItem.getSunday());
        formal.setMonday(originItem.getMonday());
        formal.setTuesday(originItem.getTuesday());
        formal.setWednesday(originItem.getWednesday());
        formal.setThursday(originItem.getThursday());
        formal.setFriday(originItem.getFriday());
        formal.setSaturday(originItem.getSaturday());

        formal.setProductOptionCode(originItem.getProductOptionCode());
        formal.setCurrencyId(originItem.getCurrencyId());
        formal.setSpecial(originItem.getSpecial());

        return formal;
    }
}
