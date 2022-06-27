package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.ViatorV2Activity;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PaxMix {
    @JsonProperty("ageBand")
    private String ageBand;

    @JsonProperty("numberOfTravelers")
    private Integer numberOfTravelers = 0;

    public PaxMix() {

    }

    public static List<PaxMix> listOf(TravellerMix travellers, ViatorV2Activity viatorSpecificData) {
        Map<String, PaxMix> ageBandCounts = new HashMap<>();
        PaxMix paxmix = new PaxMix();
        paxmix.setAgeBand("ADULT");
        paxmix.setNumberOfTravelers(travellers.getAdultCount());
        ageBandCounts.put(paxmix.getAgeBand(), paxmix);
        // given an age, find the band
        for (Integer childAge : travellers.getChildAges())
        {
            if (childAge >= viatorSpecificData.getInfantMinAge().intValue() && childAge <= viatorSpecificData.getInfantMaxAge().intValue())
            {
                paxmix = ageBandCounts.get("INFANT");
                if (paxmix == null)
                {
                    paxmix = new PaxMix();
                    paxmix.setAgeBand("INFANT");
                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
                }
                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
            }
            if (childAge >= viatorSpecificData.getChildMinAge().intValue() && childAge <= viatorSpecificData.getChildMaxAge().intValue())
            {
                paxmix = ageBandCounts.get("CHILD");
                if (paxmix == null)
                {
                    paxmix = new PaxMix();
                    paxmix.setAgeBand("CHILD");
                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
                }
                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
            }
            if (childAge >= viatorSpecificData.getYouthMinAge() && childAge <= viatorSpecificData.getYouthMaxAge())
            {
                paxmix = ageBandCounts.get("YOUTH");
                if (paxmix == null)
                {
                    paxmix = new PaxMix();
                    paxmix.setAgeBand("YOUTH");
                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
                }
                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
            }
            if (childAge >= viatorSpecificData.getAdultMinAge().intValue() && childAge <= viatorSpecificData.getAdultMaxAge().intValue())
            {
                paxmix = ageBandCounts.get("ADULT");
                if (paxmix == null)
                {
                    paxmix = new PaxMix();
                    paxmix.setAgeBand("ADULT");
                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
                }
                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
            }
            if (childAge >= viatorSpecificData.getSeniorMinAge().intValue() && childAge <= viatorSpecificData.getSeniorMaxAge().intValue())
            {
                paxmix = ageBandCounts.get("SENIOR");
                if (paxmix == null)
                {
                    paxmix = new PaxMix();
                    paxmix.setAgeBand("SENIOR");
                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
                }
                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
            }
        }
        List<PaxMix> list = new ArrayList<>();
        for (PaxMix p : ageBandCounts.values())
            list.add(p);
        return list;
    }

//    public static List<PaxMix> listOf(List<Traveller> travellers, ViatorV2Activity viatorSpecificData) {
//        Map<String, PaxMix> ageBandCounts = new HashMap<>();
//        PaxMix paxmix = new PaxMix();
//        paxmix.setAgeBand("ADULT");
//        paxmix.setNumberOfTravelers(travellers.getAdultCount());
//        ageBandCounts.put(paxmix.getAgeBand(), paxmix);
//        // given an age, find the band
//        for (Integer childAge : travellers.getChildAges())
//        {
//            if (childAge >= viatorSpecificData.getInfantMinAge().intValue() && childAge <= viatorSpecificData.getInfantMaxAge().intValue())
//            {
//                paxmix = ageBandCounts.get("INFANT");
//                if (paxmix == null)
//                {
//                    paxmix = new PaxMix();
//                    paxmix.setAgeBand("INFANT");
//                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
//                }
//                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
//            }
//            if (childAge >= viatorSpecificData.getChildMinAge().intValue() && childAge <= viatorSpecificData.getChildMaxAge().intValue())
//            {
//                paxmix = ageBandCounts.get("CHILD");
//                if (paxmix == null)
//                {
//                    paxmix = new PaxMix();
//                    paxmix.setAgeBand("CHILD");
//                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
//                }
//                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
//            }
//            if (childAge >= viatorSpecificData.getYouthMinAge() && childAge <= viatorSpecificData.getYouthMaxAge())
//            {
//                paxmix = ageBandCounts.get("YOUTH");
//                if (paxmix == null)
//                {
//                    paxmix = new PaxMix();
//                    paxmix.setAgeBand("YOUTH");
//                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
//                }
//                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
//            }
//            if (childAge >= viatorSpecificData.getAdultMinAge().intValue() && childAge <= viatorSpecificData.getAdultMaxAge().intValue())
//            {
//                paxmix = ageBandCounts.get("ADULT");
//                if (paxmix == null)
//                {
//                    paxmix = new PaxMix();
//                    paxmix.setAgeBand("ADULT");
//                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
//                }
//                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
//            }
//            if (childAge >= viatorSpecificData.getSeniorMinAge().intValue() && childAge <= viatorSpecificData.getSeniorMaxAge().intValue())
//            {
//                paxmix = ageBandCounts.get("SENIOR");
//                if (paxmix == null)
//                {
//                    paxmix = new PaxMix();
//                    paxmix.setAgeBand("SENIOR");
//                    ageBandCounts.put(paxmix.getAgeBand(), paxmix);
//                }
//                paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
//            }
//        }
//        List<PaxMix> list = new ArrayList<>();
//        for (PaxMix p : ageBandCounts.values())
//            list.add(p);
//        return list;
//    }
}
