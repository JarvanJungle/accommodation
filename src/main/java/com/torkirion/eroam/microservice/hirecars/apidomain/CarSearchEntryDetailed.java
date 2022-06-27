package com.torkirion.eroam.microservice.hirecars.apidomain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Data
public class CarSearchEntryDetailed {

    private CarSearchLocationDetail pickupDetail = new CarSearchLocationDetail();

    private CarSearchLocationDetail dropoffDetail = new CarSearchLocationDetail();

    private List<Charge> charges = new ArrayList<>();

    private List<HireCarEntryExtra> extras = new ArrayList<>();

    private List<Insurance> insuranceOption = new ArrayList<>();

    private List<Insurance> insuranceIncluded = new ArrayList<>();

    private SortedSet<CancellationPolicy> cancellationPolicies = new TreeSet<>();

    private TermsAndCondition termsAndCondition = new TermsAndCondition();

    @Data
    public static class Charge
    {
        private String description;

        private Boolean taxInclusive;

        private Boolean includedInRate;

        private String chargeCurrency;

        private BigDecimal chargeAmount;
    }

    @Data
    public static class Insurance
    {
        private String code;

        private String description;

        private String costCurrency;

        private BigDecimal costAmount;

        private String costPer;

        private String coveredCurrency;

        private BigDecimal coveredAmount;

        private Boolean taxInclusive;

        private String termsAndConditions;
    }

    @Data
    public static class CancellationPolicy implements Comparable<CancellationPolicy> {
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
        private CurrencyValue fee;
        private String description;

        @Override
        public int compareTo(CancellationPolicy o) {
            return this.startTime.compareTo(o.startTime);
        }
    }

    @Data
    public static class TermsAndCondition {
        private String link;
        private String description;
    }
}
