package com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OTAUtils {

    public OTAUtils()
    {
    }

    private static OTAUtils instance;

    public static OTAUtils getInstance() {
        if(instance == null) {
            instance = new OTAUtils();
        }
        return instance;
    }

    public String getCodeTableValue(String codeTable, String code)
    {
        Map<String, String> otaTable = otaCodes.get(codeTable);
        if(otaTable == null) {
            log.error("getCodeTableValue:: not found codeTable = {}", codeTable);
            return "NOT_FOUND";
        }
        String value = otaTable.get(code);
        if(value == null) {
            log.error("getCodeTableValue:: not found codeTable = {} and code = {}", codeTable, code);
            return "NOT_FOUND";
        }
        return value;
    }

    public static class CodeValue
    {
        public CodeValue(String code, String value)
        {
            super();
            this.code = code;
            this.value = value;
        }

        private String code;

        private String value;

        public CodeValue()
        {
            super();
        }

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }
    }

    private static Map<String, Map<String, String>> otaCodes;

    static {
        Map<String, String> vehicleCategoriesOtaCode = new HashMap();
        Map<String, String> vehicleSizesOtaCode = new HashMap<>();
        Map<String, String> paymentTypeOtaCode = new HashMap<>();
        Map<String, String> pricedCoverageTypes = new HashMap<>();


        otaCodes = new HashMap<>();
        otaCodes.put("VEC", vehicleCategoriesOtaCode);
        otaCodes.put("SIZ", vehicleSizesOtaCode);
        otaCodes.put("PT", paymentTypeOtaCode);
        otaCodes.put("VCT", pricedCoverageTypes);

        vehicleCategoriesOtaCode.put("1", "Car");
        vehicleCategoriesOtaCode.put("2", "Van");
        vehicleCategoriesOtaCode.put("3", "SUV");
        vehicleCategoriesOtaCode.put("4", "Convertible");
        vehicleCategoriesOtaCode.put("5", "Truck");
        vehicleCategoriesOtaCode.put("6", "Motorcycle");
        vehicleCategoriesOtaCode.put("7", "Limo");
        vehicleCategoriesOtaCode.put("8", "Station Wagon");
        vehicleCategoriesOtaCode.put("9", "Pickup");
        vehicleCategoriesOtaCode.put("10", "Motorhome");
        vehicleCategoriesOtaCode.put("11", "All-Terrain");
        vehicleCategoriesOtaCode.put("12", "Recreational");
        vehicleCategoriesOtaCode.put("13", "Sport");
        vehicleCategoriesOtaCode.put("14", "Special");
        vehicleCategoriesOtaCode.put("15", "Extended cab pickup");
        vehicleCategoriesOtaCode.put("16", "Regular cab pickup");
        vehicleCategoriesOtaCode.put("17", "Special offer");
        vehicleCategoriesOtaCode.put("18", "Coupe");
        vehicleCategoriesOtaCode.put("19", "Monospace");
        vehicleCategoriesOtaCode.put("20", "2 wheel vehicle");
        vehicleCategoriesOtaCode.put("21", "Roadster");
        vehicleCategoriesOtaCode.put("22", "Crossover");
        vehicleCategoriesOtaCode.put("23", "Commercial van/truck");
        vehicleCategoriesOtaCode.put("24", "2-3 door car");
        vehicleCategoriesOtaCode.put("25", "2/4 door car");
        vehicleCategoriesOtaCode.put("26", "4-5 door car");
        vehicleCategoriesOtaCode.put("27", "Premium Elite");

        vehicleSizesOtaCode.put("1", "Mini");
        vehicleSizesOtaCode.put("2", "Subcompact");
        vehicleSizesOtaCode.put("3", "Economy");
        vehicleSizesOtaCode.put("4", "Compact");
        vehicleSizesOtaCode.put("5", "Midsize");
        vehicleSizesOtaCode.put("6", "Intermediate");
        vehicleSizesOtaCode.put("7", "Standard");
        vehicleSizesOtaCode.put("8", "Fullsize");
        vehicleSizesOtaCode.put("9", "Luxury");
        vehicleSizesOtaCode.put("10", "Premium");
        vehicleSizesOtaCode.put("11", "Minivan");
        vehicleSizesOtaCode.put("12", "12 passenger van");
        vehicleSizesOtaCode.put("13", "Moving van");
        vehicleSizesOtaCode.put("14", "15 passenger van");
        vehicleSizesOtaCode.put("15", "Cargo van");
        vehicleSizesOtaCode.put("16", "not offered");
        vehicleSizesOtaCode.put("17", "not offered");
        vehicleSizesOtaCode.put("18", "not offered");
        vehicleSizesOtaCode.put("19", "not offered");
        vehicleSizesOtaCode.put("20", "not offered");
        vehicleSizesOtaCode.put("21", "not offered");
        vehicleSizesOtaCode.put("22", "Regular");
        vehicleSizesOtaCode.put("23", "Unique");
        vehicleSizesOtaCode.put("24", "Exotic");
        vehicleSizesOtaCode.put("25", "Small/medium truck");
        vehicleSizesOtaCode.put("26", "Large truck");
        vehicleSizesOtaCode.put("27", "Small SUV");
        vehicleSizesOtaCode.put("28", "Medium SUV");
        vehicleSizesOtaCode.put("29", "Large SUV");
        vehicleSizesOtaCode.put("30", "Exotic SUV");
        vehicleSizesOtaCode.put("31", "Four wheel drive");
        vehicleSizesOtaCode.put("32", "Special");
        vehicleSizesOtaCode.put("33", "Mini elite");
        vehicleSizesOtaCode.put("34", "Economy elite");
        vehicleSizesOtaCode.put("35", "Compact elite");
        vehicleSizesOtaCode.put("36", "Intermediate elite");
        vehicleSizesOtaCode.put("37", "Standard elite");
        vehicleSizesOtaCode.put("38", "Fullsize elite");
        vehicleSizesOtaCode.put("39", "Premium elite");
        vehicleSizesOtaCode.put("40", "Luxury elite");
        vehicleSizesOtaCode.put("41", "Oversize");

        paymentTypeOtaCode.put("1", "Pay On Arrival");
        paymentTypeOtaCode.put("2", "Pay On Arrival");
        paymentTypeOtaCode.put("4", "Prepayment");
        paymentTypeOtaCode.put("5", "Prepayment");
        paymentTypeOtaCode.put("7", "Prepayment");
        paymentTypeOtaCode.put("8", "Pay On Arrival");
        paymentTypeOtaCode.put("9", "Prepayment");
        paymentTypeOtaCode.put("10", "Prepayment");
        paymentTypeOtaCode.put("11", "Pay On Arrival");
        paymentTypeOtaCode.put("12", "insurance policy");
        paymentTypeOtaCode.put("13", "Payment by invoice after reservation.");
        paymentTypeOtaCode.put("14", "Prepayment");
        paymentTypeOtaCode.put("16", "Prepayment");
        paymentTypeOtaCode.put("17", "Prepayment");
        paymentTypeOtaCode.put("18", "Payment");
        paymentTypeOtaCode.put("19", "Prepayment");

        pricedCoverageTypes.put("AD", "Additional Driver");
        pricedCoverageTypes.put("CDW", "Collision Damage Waiver");
        pricedCoverageTypes.put("LDW", "Loss Damage Waiver");
        pricedCoverageTypes.put("TP", "Theft Protection");
        pricedCoverageTypes.put("CFC", "Customer Facility Charge");
        pricedCoverageTypes.put("CPP", "Canellation Protection Insurance");
        pricedCoverageTypes.put("GPS", "Global Positioning System");
        pricedCoverageTypes.put("LSC", "Location Service Charge");
        pricedCoverageTypes.put("ASC", "Airport Service Charge");
        pricedCoverageTypes.put("CSS", "Child Safety Seat");
        pricedCoverageTypes.put("ALI", "Additional Liability Insurance");
        pricedCoverageTypes.put("FPO", "Fuel Petrol Option");
        pricedCoverageTypes.put("F2F", "Full to full");
        pricedCoverageTypes.put("E2E", "Empty to empty");
        pricedCoverageTypes.put("F2E", "Full to empty");
        pricedCoverageTypes.put("SLI", "Supplementary Liability Insurance / Extended Protection");
        pricedCoverageTypes.put("UNL", "Unlimited Mileage");
        pricedCoverageTypes.put("UMP", "Uninsured Motorist Protection");
        pricedCoverageTypes.put("YDS", "Young Driver Surcharge");
        pricedCoverageTypes.put("WPI", "Winterpackage");
        pricedCoverageTypes.put("PEP", "Personal Effects Protection / Coverage");
        pricedCoverageTypes.put("412", "Oneway fee");
        pricedCoverageTypes.put("416", "Limited mileage, Kilometers inclusive: xxx km");
        pricedCoverageTypes.put("417", "Limited mileage, Miles inclusive: xxx miles");
        pricedCoverageTypes.put("418", "Other taxes and service charges");
        pricedCoverageTypes.put("TAX", "Tax");

    }
}
