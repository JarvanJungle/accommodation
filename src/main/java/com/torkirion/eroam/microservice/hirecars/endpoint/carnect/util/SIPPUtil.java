package com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util;

import com.torkirion.eroam.microservice.hirecars.apidomain.SIPPBlock;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SIPPUtil {
    public SIPPBlock makeSIPPBlock(String s) throws Exception
    {
        log.debug("makeSIPPBlock::entered for " + s);
        // http://en.wikipedia.org/wiki/ACRISS_Car_Classification_Code
        // http://www.acriss.org/expanded-matrix.asp
        if (s.length() == 4)
        {
            SIPPBlock sippBlock = new SIPPBlock();

            String category = s.substring(0, 1);
            String type = s.substring(1, 2);
            String transmissionAndDrive = s.substring(2, 3);
            String fuelAndAir = s.substring(3);
            sippBlock.setSippCode(s);

            sippBlock.setCategory(getString(categoryMap, type));
            sippBlock.setCarType(getString(carTypeMap, category));
            sippBlock.setTransmission(getString(transmissionMap, transmissionAndDrive));
            sippBlock.setDrive(getString(driveMap, transmissionAndDrive));
            sippBlock.setFuel(getString(fuelMap, fuelAndAir));
            String airS = getString(airMap, fuelAndAir);
            sippBlock.setAircon(Boolean.parseBoolean(airS));
            return sippBlock;
        }
        else
        {
            log.warn("makeSIPPBlock::bypassing for unknown SIPP block " + s);
            return null;
        }
    }

    private String getString(Map<String, String> map, String s)
    {
        if ( map.get(s) != null )
            return map.get(s);
        else
            return "Unknown";
    }

    /**
     * @return the Log4J logger object
//     */
//    protected synchronized org.apache.log4j.Logger getLogger()
//    {
//        if (l4jLogger == null)
//        {
//            l4jLogger = org.apache.log4j.Logger.getLogger(this.getClass().getName());
//        }
//        return l4jLogger;
//    }

    /*
     * The logging interface
     */
//    private transient static org.apache.log4j.Logger l4jLogger = null;

    private static final Map<String, String> categoryMap;
    private static final Map<String, String> carTypeMap;
    private static final Map<String, String> transmissionMap;
    private static final Map<String, String> driveMap;
    private static final Map<String, String> fuelMap;
    private static final Map<String, String> airMap;
    static
    {
        categoryMap = new HashMap<String, String>();
        categoryMap.put("M", "Mini");
        categoryMap.put("N", "Mini Elite");
        categoryMap.put("E", "Economy");
        categoryMap.put("H", "Economy Elite");
        categoryMap.put("C", "Compact");
        categoryMap.put("D", "Compact Elite");
        categoryMap.put("I", "Intermediate");
        categoryMap.put("J", "Intermediate Elite");
        categoryMap.put("S", "Standard");
        categoryMap.put("R", "Standard Elite");
        categoryMap.put("F", "Fullsize");
        categoryMap.put("G", "Fullsize Elite");
        categoryMap.put("P", "Premium");
        categoryMap.put("U", "Premium Elite");
        categoryMap.put("L", "Luxury");
        categoryMap.put("W", "Luxury Elite");
        categoryMap.put("O", "Oversize");
        categoryMap.put("X", "Special");

        carTypeMap = new HashMap<String, String>();
        carTypeMap.put("B", "2-3 Door");
        carTypeMap.put("C", "2/4 Door");
        carTypeMap.put("D", "4-5 Door");
        carTypeMap.put("W", "Wagon/Estate");
        carTypeMap.put("V", "Passenger Van");
        carTypeMap.put("L", "Limousine");
        carTypeMap.put("S", "Sport");
        carTypeMap.put("T", "Convertible");
        carTypeMap.put("F", "SUV");
        carTypeMap.put("J", "Open Air All Terrain");
        carTypeMap.put("X", "Special");
        carTypeMap.put("P", "Pick up Regular Cab");
        carTypeMap.put("Q", "Pick up Extended Cab");
        carTypeMap.put("Z", "Special Offer Car");
        carTypeMap.put("E", "Coupe");
        carTypeMap.put("M", "Monospace");
        carTypeMap.put("R", "Recreational Vehicle");
        carTypeMap.put("H", "Motor Home");
        carTypeMap.put("Y", "2 Wheel Vehicle");
        carTypeMap.put("N", "Roadster");
        carTypeMap.put("G", "Crossover");
        carTypeMap.put("K", "Commercial  Van/Truck");

        transmissionMap = new HashMap<String, String>();
        transmissionMap.put("M", "Manual");
        transmissionMap.put("N", "Manual");
        transmissionMap.put("C", "Manual");
        transmissionMap.put("A", "Automatic");
        transmissionMap.put("B", "Automatic");
        transmissionMap.put("D", "Automatic");

        driveMap = new HashMap<String, String>();
        driveMap.put("M", "Unspecified ");
        driveMap.put("N", "4WD");
        driveMap.put("C", "AWD");
        driveMap.put("A", "Unspecified");
        driveMap.put("B", "4WD");
        driveMap.put("D", "AWD");

        fuelMap = new HashMap<String, String>();
        fuelMap.put("R", "Unspecified");
        fuelMap.put("N", "Unspecified");
        fuelMap.put("D", "Diesel");
        fuelMap.put("Q", "Diesel");
        fuelMap.put("H", "Hybrid");
        fuelMap.put("I", "Hybrid");
        fuelMap.put("E", "Electric");
        fuelMap.put("C", "Electric");
        fuelMap.put("L", "LPG/Compressed Gas");
        fuelMap.put("S", "LPG/Compressed Gas");
        fuelMap.put("A", "Hydrogen");
        fuelMap.put("B", "Hydrogen");
        fuelMap.put("M", "Multi Fuel");
        fuelMap.put("F", "Multi Fuel");
        fuelMap.put("V", "Petrol");
        fuelMap.put("Z", "Petrol");
        fuelMap.put("U", "Ethanol");
        fuelMap.put("X", "Ethanol");

        airMap = new HashMap<String, String>();
        airMap.put("R", "True");
        airMap.put("N", "False");
        airMap.put("D", "True");
        airMap.put("Q", "False");
        airMap.put("H", "True");
        airMap.put("I", "False");
        airMap.put("E", "True");
        airMap.put("C", "False");
        airMap.put("L", "True");
        airMap.put("S", "False");
        airMap.put("A", "True");
        airMap.put("B", "False");
        airMap.put("M", "True");
        airMap.put("F", "False");
        airMap.put("V", "True");
        airMap.put("Z", "False");
        airMap.put("U", "True");
        airMap.put("X", "False");
    }
}
