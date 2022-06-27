package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.carnect.schemas.message.*;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util.OTAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class VehicleDetail {

    protected abstract CarSearchEntry createCarSearchEntry();

    public abstract OTAUtils getOtaUtils();



    protected CarSearchEntry makeCarSearchEntry(VehicleVendorAvailabilityType.VehAvails.VehAvail vehAvail,
                                                VehicleVendorAvailabilityType vendorAvail, VehAvailRateRS rateRS)
    {
        log.debug("makeCarSearchEntry::entered");
        CompanyNameType vendor = vehAvail.getVehAvailCore().getVendor();
        //CompanyNameType vendor = vendorAvail.getVendor();
        CarSearchEntry carSearchEntry = createCarSearchEntry();
        carSearchEntry.setCarName(vehAvail.getVehAvailCore().getVehicle().getVehMakeModel().getName());
        if (vehAvail.getVehAvailCore().getTotalCharge().size() > 1)
        {
            log.warn("makeCarSearchEntry::vehAvail.getVehAvailCore().getTotalCharge().size()=" + vehAvail.getVehAvailCore().getTotalCharge().size());
        }
        boolean oneWay = true;
        String returnCode = rateRS.getVehAvailRSCore().getVehRentalCore().getReturnLocation().getCodeContext() + "@" +  rateRS.getVehAvailRSCore().getVehRentalCore().getReturnLocation().getLocationCode();
        for ( VehicleRentalCoreType.PickUpLocation p : rateRS.getVehAvailRSCore().getVehRentalCore().getPickUpLocation() )
        {
            String pc = p.getCodeContext() + "@" + p.getLocationCode();
            log.debug("makeCarSearchEntry::checking for oneWay: compare pickup " + pc + " with return " + returnCode);
            if ( pc.equals(returnCode ))
            {
                oneWay = false;
            }
        }
        log.debug("makeCarSearchEntry::oneWay is " + oneWay);
        String currencyCode = vehAvail.getVehAvailCore().getTotalCharge().get(0).getCurrencyCode();
        java.math.BigDecimal amount = vehAvail.getVehAvailCore().getTotalCharge().get(0).getEstimatedTotalAmount();
        carSearchEntry.setPrice(new CurrencyValue(currencyCode, amount));
        carSearchEntry.setPaymentTimingStr(OTAUtils.getInstance().getCodeTableValue("PT", vehAvail.getVehAvailInfo().getPaymentRules().getPaymentRule().get(0).getPaymentType()));
        carSearchEntry.setPaymentMessage(vehAvail.getVehAvailInfo().getPaymentRules().getPaymentRule().get(0).getValue());
        if("Prepayment".equals(carSearchEntry.getPaymentTimingStr())) {
            carSearchEntry.setPaymentTiming(PaymentTiming.PREPAID);
            carSearchEntry.setCreditCardRequired(true);
        }
        VehicleAvailCoreType.TotalCharge totalCharge = vehAvail.getVehAvailCore().getTotalCharge().get(0);
        carSearchEntry.setAmountToBeCharged(new CurrencyValue(totalCharge.getCurrencyCode(), totalCharge.getEstimatedTotalAmount()));
        carSearchEntry.setEndPoint(vendor.getCodeContext());
        if (vehAvail.getVehAvailCore().getVehicle().getPictureURL().startsWith("http"))
            carSearchEntry.setImage(vehAvail.getVehAvailCore().getVehicle().getPictureURL());
        else
            carSearchEntry.setImage(vehAvail.getVehAvailCore().getVehicle().getPictureURL());

        if (vehAvail.getVehAvailCore().getRentalRate().get(0).getRateDistance() != null && vehAvail.getVehAvailCore().getRentalRate().get(0).getRateDistance().size() > 0)
        {
            VehicleRentalRateType.RateDistance rateDistance = vehAvail.getVehAvailCore().getRentalRate().get(0).getRateDistance().get(0);
            if (!rateDistance.isUnlimited())
            {
                if (rateDistance.getQuantity() != null)
                {
                    carSearchEntry.setLimitedDistance(rateDistance.getQuantity());
                    if (rateDistance.getDistUnitName() != null)
                    {
                        carSearchEntry.setLimitedDistanceUnits(rateDistance.getDistUnitName().toString());
                    }
                    else
                    {
                        carSearchEntry.setLimitedDistanceUnits(DistanceUnitNameType.KM.toString());
                    }
                    carSearchEntry.setLimitedDistancePer(VehiclePeriodUnitNameType.RENTAL_PERIOD.toString());
                    if (rateDistance.getVehiclePeriodUnitName() != null)
                    {
                        carSearchEntry.setLimitedDistancePer(rateDistance.getVehiclePeriodUnitName().toString());
                    }
                }
                else
                {
                    log.debug("makeCarSearchEntry::vehAvail.getVehAvailCore().getRentalRate().get(0).getRateDistance()/.get(0)/getQuantity() == null");
                }
            }
        }
        else
        {
            log.debug("makeCarSearchEntry::vehAvail.getVehAvailCore().getRentalRate().get(0).getRateDistance() == null");
        }

        try
        {
            String passengerQuantity = vehAvail.getVehAvailCore().getVehicle().getPassengerQuantity();
            if ( passengerQuantity != null )
            {
                if ( passengerQuantity.endsWith("+"))
                {
                    carSearchEntry.setSeats(Integer.parseInt(passengerQuantity.substring(0, passengerQuantity.length() - 1)));
                }
                else
                {
                    carSearchEntry.setSeats(Integer.parseInt(passengerQuantity));
                }
            }
        }
        catch (NumberFormatException e)
        {
            log.warn("makeCarSearchResults::paxQuantity is not numeric:'" + vehAvail.getVehAvailCore().getVehicle().getPassengerQuantity() + "'");
            carSearchEntry.setSeats(0);
        }

        carSearchEntry.setDoors(0);
        if (vehAvail.getVehAvailCore().getVehicle().getVehType() != null)
        {
            carSearchEntry.setCategoryCode(vehAvail.getVehAvailCore().getVehicle().getVehType().getVehicleCategory());
            if (carSearchEntry.getCategoryCode() != null)
            {
                carSearchEntry.setCategory(getOtaUtils().getCodeTableValue("VEC", carSearchEntry.getCategoryCode()));
            }
            try
            {
                if ( vehAvail.getVehAvailCore().getVehicle().getVehType().getDoorCount() != null )
                {
                    carSearchEntry.setDoors(Integer.parseInt(vehAvail.getVehAvailCore().getVehicle().getVehType().getDoorCount()));
                }
            }
            catch (NumberFormatException e)
            {
                log.warn("makeCarSearchResults::doorCount is not numeric:'" + vehAvail.getVehAvailCore().getVehicle().getVehType().getDoorCount() + "'");
                carSearchEntry.setDoors(0);
            }
        }
        if (vehAvail.getVehAvailCore().getVehicle().getVehClass() != null && vehAvail.getVehAvailCore().getVehicle().getVehClass().getSize() != null)
        {
            carSearchEntry.setSize(getOtaUtils().getCodeTableValue("SIZ", vehAvail.getVehAvailCore().getVehicle().getVehClass().getSize()));
        }
        carSearchEntry.setSupplierName(vendor.getValue());
        if ( vendorAvail.getInfo() != null && vendorAvail.getInfo().getVendorMessages() != null && vendorAvail.getInfo().getVendorMessages().getVendorMessage().size() > 0 && vendorAvail.getInfo().getVendorMessages().getVendorMessage().get(0).getInfoType().equals("23") )
        {
            //TODO need research
            carSearchEntry.setSupplierImage(vendorAvail.getInfo().getVendorMessages().getVendorMessage().get(0).getTitle());
        }
        CarSearchLocationAndDate pickupDetails = new CarSearchLocationAndDate();
        carSearchEntry.setPickup(pickupDetails);
        pickupDetails.setDateTime(rateRS.getVehAvailRSCore().getVehRentalCore().getPickUpDateTime()
                .toGregorianCalendar().getTime());
        VehicleAvailCoreType.VendorLocation vendorLocation = vehAvail.getVehAvailCore().getVendorLocation();
        String vendorLocationValue = vendorLocation.getValue();
        if(vendorLocationValue != null) {
            String[] latLongValue = vendorLocationValue.split(",");
            pickupDetails.setLocation(new LatitudeLongitude(new BigDecimal(latLongValue[0]), new BigDecimal(latLongValue[1])));
        }
        pickupDetails.setLocationCode(vendorLocation.getLocationCode());


        CarSearchLocationAndDate dropoffDetails = new CarSearchLocationAndDate();
        BeanUtils.copyProperties(pickupDetails, dropoffDetails);
        dropoffDetails.setDateTime(rateRS.getVehAvailRSCore().getVehRentalCore().getReturnDateTime()
                .toGregorianCalendar().getTime());

        VehicleAvailCoreType.DropOffLocation dropOffLocation = vehAvail.getVehAvailCore().getDropOffLocation();
        String dropOffLocationValue = dropOffLocation.getValue();
        if(dropOffLocationValue != null) {
            String[] latLongValue = dropOffLocationValue.split(",");
            pickupDetails.setLocation(new LatitudeLongitude(new BigDecimal(latLongValue[0]), new BigDecimal(latLongValue[1])));
        }
        pickupDetails.setLocationCode(dropOffLocation.getLocationCode());
        carSearchEntry.setDropoff(dropoffDetails);
        carSearchEntry.getMessages().add(carSearchEntry.getPaymentMessage());

        carSearchEntry.setId(vendor.getCodeContext() + "@" + vehAvail.getVehAvailCore().getReference().getIDContext()
                + "@" + carSearchEntry.getPickup().getLocationCode() + "@" + carSearchEntry.getDropoff().getLocationCode());
        return carSearchEntry;
    }


    /*---------Start makeCarSearchEntryDetail -------------*/
    protected CarSearchEntryDetailed makeCarSearchEntryDetail(VehRateRuleRS rateRuleRS, ResponseLocationCodes locationCodes) throws Exception {
        CarSearchEntryDetailed carSearchEntryDetailed = new CarSearchEntryDetailed();
        List<VehRateRuleRS.LocationDetails> locationDetails = rateRuleRS.getLocationDetails();
        if(locationDetails.isEmpty()) {
            log.warn("makeCarSearchEntryDetail::locationDetails is empty");
            return carSearchEntryDetailed;
        }
        VehRateRuleRS.LocationDetails pickupLocationDetail = null;
        VehRateRuleRS.LocationDetails dropOffLocationDetail = null;
        for(VehRateRuleRS.LocationDetails locationDetail : locationDetails) {
            if(locationCodes.getPickupLocationCode().equals(locationDetail.getCode())) {
                pickupLocationDetail = locationDetail;
            }
            if(locationCodes.getDropOffLocationCode().equals(locationDetail.getCode())) {
                dropOffLocationDetail = locationDetail;
            }
        }
        if(pickupLocationDetail == null || dropOffLocationDetail == null) {
            throw new Exception("can't find localDetails");
        }

        carSearchEntryDetailed.setPickupDetail(makeCarSearchLocationDetail(pickupLocationDetail));
        carSearchEntryDetailed.setDropoffDetail(makeCarSearchLocationDetail(dropOffLocationDetail));
        if(rateRuleRS.getPricedCoverages() != null && rateRuleRS.getPricedCoverages().getPricedCoverage() != null
                && !rateRuleRS.getPricedCoverages().getPricedCoverage().isEmpty()) {
            log.warn("makeCancellationPolicies::pricedCoverages is empty");
            carSearchEntryDetailed.setCharges(makeCarSearchEntryDetailedCharges(rateRuleRS));
            carSearchEntryDetailed.setExtras(makeExtras(rateRuleRS));
            carSearchEntryDetailed.setInsuranceOption(makeOptionalInsurance(rateRuleRS));
            carSearchEntryDetailed.setInsuranceIncluded(makeIncludedInsurance(rateRuleRS));
            carSearchEntryDetailed.setCancellationPolicies(makeCancellationPolicies(rateRuleRS));
        }
        if(rateRuleRS.getVendorMessages() != null && !rateRuleRS.getVendorMessages().getVendorMessage().isEmpty()) {
            carSearchEntryDetailed.setTermsAndCondition(makeTermsAndCondition(rateRuleRS.getVendorMessages().getVendorMessage()));
        }
        return carSearchEntryDetailed;
    }

    private CarSearchEntryDetailed.TermsAndCondition makeTermsAndCondition(List<VendorMessageType> vendorMessage) {
        Optional<VendorMessageType> first = vendorMessage.stream().filter(vm -> "Rental Terms of".equals(vm.getTitle())).findFirst();
        if(!first.isPresent()) {
            return new CarSearchEntryDetailed.TermsAndCondition();
        }
        try {
            String url = first.get().getSubSection().get(0).getParagraph().get(0).getText().get(0).getValue();
            CarSearchEntryDetailed.TermsAndCondition termsAndCondition = new CarSearchEntryDetailed.TermsAndCondition();
            termsAndCondition.setDescription(first.get().getTitle());
            termsAndCondition.setLink(url);
            return termsAndCondition;
        } catch (Exception e) {
            log.warn("makeTermsAndCondition::error: {}", e.getMessage());
        }
        return new CarSearchEntryDetailed.TermsAndCondition();
    }
    
    private SortedSet<CarSearchEntryDetailed.CancellationPolicy> makeCancellationPolicies(VehRateRuleRS rateRuleRS) {
        log.debug("makeOptionalInsurance::entered");
        SortedSet<CarSearchEntryDetailed.CancellationPolicy> cancellationPolicies = new TreeSet<>();
        List<CoveragePricedType> coveragePricedTypes = rateRuleRS.getPricedCoverages().getPricedCoverage();
        for(CoveragePricedType coveragePricedType : coveragePricedTypes) {
            if(!"CF".equals(coveragePricedType.getCoverage().getCode())) {
                continue;
            }
            List<CoveragePricedType> details = coveragePricedType.getCoverage().getDetails();
            if(details == null || details.isEmpty()) {
                return cancellationPolicies;
            }
            for(CoveragePricedType detail : details) {
                String[] startEnd = detail.getCoverage().getCoverageType().split("_");
                if(startEnd.length != 2) {
                    continue;
                }
                CarSearchEntryDetailed.CancellationPolicy cancellationPolicy = new CarSearchEntryDetailed.CancellationPolicy();
                cancellationPolicy.setStartTime(LocalDateTime.parse(startEnd[0], CarNectService.df2YYYYMMDDTHHMMSS));
                cancellationPolicy.setEndTime(LocalDateTime.parse(startEnd[1], CarNectService.df2YYYYMMDDTHHMMSS));
                cancellationPolicy.setFee(new CurrencyValue(detail.getCharge().getCurrencyCode(), detail.getCharge().getAmount()));
                cancellationPolicy.setDescription(detail.getCharge().getDescription());
                cancellationPolicies.add(cancellationPolicy);
            }
        }
        return cancellationPolicies;
    }

    private List<CarSearchEntryDetailed.Insurance> makeOptionalInsurance(VehRateRuleRS rateRuleRS) {
        log.debug("makeOptionalInsurance::entered");
        List<CoveragePricedType> coveragePricedTypes = rateRuleRS.getPricedCoverages().getPricedCoverage();
        List<CarSearchEntryDetailed.Insurance> insurances = new ArrayList<>();
        for (CoveragePricedType coveragePriced : coveragePricedTypes) {
            if (coveragePriced.getCharge() != null && !testB(coveragePriced.getCharge().isIncludedInRate())
                    && !testB(coveragePriced.getCharge().isIncludedInEstTotalInd())
                    && !"CF".equals(coveragePriced.getCoverage().getCode()))
            {
                CarSearchEntryDetailed.Insurance insurance = new CarSearchEntryDetailed.Insurance();
                if (coveragePriced.getCoverage() != null)
                {
                    insurance.setCode(coveragePriced.getCoverage().getCode());
                }
                else
                {
                    log.warn("makeOptionalInsurance::coveragePriced.getCoverage() is null");
                }
                insurance.setDescription(coveragePriced.getCharge().getDescription());
                log.debug(
                        "makeOptionalInsurance::coveragePriced.getCharge().getDescription()='" + coveragePriced.getCharge().getDescription()
                                + "', coveragePriced.getCoverage().getCoverageType()='" + coveragePriced.getCoverage().getCoverageType() + "'");
                if (coveragePriced.getCharge().getDescription() == null || coveragePriced.getCharge().getDescription().length() == 0)
                {
                    if (coveragePriced.getCoverage().getCoverageType() != null)
                    {
                        insurance.setDescription(coveragePriced.getCoverage().getCoverageType());
                    }
                }
                if(coveragePriced.getCoverage().getDetails() != null  && !coveragePriced.getCoverage().getDetails().isEmpty()) {
                    insurance.setCoveredAmount(coveragePriced.getCoverage().getDetails().get(0).getCharge().getAmount());
                    insurance.setCoveredCurrency(CarNectService.CURRENCY_DEFAULT);
                } else {
                    insurance.setCoveredAmount(CarNectService.AMOUNT_0);
                    insurance.setCoveredCurrency(CarNectService.CURRENCY_DEFAULT);
                }
                insurance.setTaxInclusive(coveragePriced.getCharge().isTaxInclusive());
                if (coveragePriced.getCharge().getAmount() != null) {
                    insurance.setCostAmount(coveragePriced.getCharge().getAmount());
                    insurance.setCostPer(VehiclePeriodUnitNameType.RENTAL_PERIOD.value());
                } else {
                    insurance.setCostAmount(CarNectService.AMOUNT_0);
                    insurance.setCostPer(VehiclePeriodUnitNameType.RENTAL_PERIOD.value());
                }
            }
        }
        return insurances;
    }

    private List<CarSearchEntryDetailed.Insurance> makeIncludedInsurance(VehRateRuleRS rateRuleRS) {
        log.debug("makeIncludedInsurance::entered");
        List<CoveragePricedType> coveragePricedTypes = rateRuleRS.getPricedCoverages().getPricedCoverage();
        List<CarSearchEntryDetailed.Insurance> insurances = new ArrayList<>();
        for (CoveragePricedType coveragePriced : coveragePricedTypes) {
            if (coveragePriced.getCharge() != null && (testB(coveragePriced.getCharge().isIncludedInRate())
                    || testB(coveragePriced.getCharge().isIncludedInEstTotalInd()))
                    && !"CF".equals(coveragePriced.getCoverage().getCode())) { // Cancellation fee move to the cancellation policy
                CarSearchEntryDetailed.Insurance insurance = new CarSearchEntryDetailed.Insurance();
                if (coveragePriced.getCoverage() != null)
                {
                    insurance.setCode(coveragePriced.getCoverage().getCode());
                }
                else
                {
                    log.warn("makeIncludedInsurance::coveragePriced.getCoverage() is null");
                }
                insurance.setDescription(coveragePriced.getCharge().getDescription());
                log.debug(
                        "makeIncludedInsurance::coveragePriced.getCharge().getDescription()='" + coveragePriced.getCharge().getDescription()
                                + "', coveragePriced.getCoverage().getCoverageType()='" + coveragePriced.getCoverage().getCoverageType() + "'");
                if (coveragePriced.getCharge().getDescription() == null || coveragePriced.getCharge().getDescription().length() == 0)
                {
                    if (coveragePriced.getCoverage().getCoverageType() != null)
                    {
                        insurance.setDescription(coveragePriced.getCoverage().getCoverageType());
                    }
                }
                if(coveragePriced.getCoverage().getDetails() != null  && !coveragePriced.getCoverage().getDetails().isEmpty()) {
                    insurance.setCoveredAmount(coveragePriced.getCoverage().getDetails().get(0).getCharge().getAmount());
                    insurance.setCoveredCurrency("EUR");
                } else {
                    insurance.setCoveredAmount(CarNectService.AMOUNT_0);
                    insurance.setCoveredCurrency(CarNectService.CURRENCY_DEFAULT);
                }
                insurance.setTaxInclusive(coveragePriced.getCharge().isTaxInclusive());
                if (coveragePriced.getCharge().getAmount() != null)
                {
                    insurance.setCostAmount(coveragePriced.getCharge().getAmount());
                    insurance.setCostPer(VehiclePeriodUnitNameType.RENTAL_PERIOD.value());
                } else {
                    insurance.setCostAmount(CarNectService.AMOUNT_0);
                    insurance.setCostPer(VehiclePeriodUnitNameType.RENTAL_PERIOD.value());
                }
                insurances.add(insurance);
            }

        }
        return insurances;
    }

    protected List<HireCarEntryExtra> makeExtras(VehRateRuleRS rateRuleRS) {
        log.debug("makeExtras::entered");
        List<VehicleEquipmentPricedType> pricedEquips = rateRuleRS.getPricedEquips().getPricedEquip();
        List<HireCarEntryExtra> extras = new ArrayList<>();
        for (VehicleEquipmentPricedType vehicleEquipmentPriced : pricedEquips) {
            HireCarEntryExtra extra = new HireCarEntryExtra();
            if (vehicleEquipmentPriced.getCharge() != null)
            {
                extra.setAmount(vehicleEquipmentPriced.getCharge().getAmount());
                extra.setIncludedInRate(vehicleEquipmentPriced.getCharge().isIncludedInRate());
                extra.setTaxInclusive(vehicleEquipmentPriced.getCharge().isTaxInclusive());
            }
            extra.setCode(vehicleEquipmentPriced.getEquipment().getEquipType());
            extra.setDescription(vehicleEquipmentPriced.getEquipment().getDescription());
            if ( vehicleEquipmentPriced.getCharge().isGuaranteedInd() != null && !vehicleEquipmentPriced.getCharge().isGuaranteedInd() )
            {
                extra.setRequestOnly(true);
            }
            extras.add(extra);
        }
        return extras;
    }

    private List<CarSearchEntryDetailed.Charge> makeCarSearchEntryDetailedCharges(VehRateRuleRS rateRuleRS) {
        List<VehicleChargePurposeType> vehicleCharges = rateRuleRS.getRentalRate().get(0)
                .getVehicleCharges().getVehicleCharge();
        List<CarSearchEntryDetailed.Charge> charges = new ArrayList<>();
        for(VehicleChargePurposeType vehicleCharge : vehicleCharges) {
            CarSearchEntryDetailed.Charge charge = new CarSearchEntryDetailed.Charge();
            charge.setChargeCurrency(vehicleCharge.getCurrencyCode());
            charge.setChargeAmount(vehicleCharge.getAmount());

            charge.setTaxInclusive(vehicleCharge.isTaxInclusive() == null ? false : vehicleCharge.isTaxInclusive());
            if("original".equals(vehicleCharge.getPurpose())) {
                charge.setIncludedInRate(true);
            } else {
                charge.setIncludedInRate(false);
            }

            if(vehicleCharge.getDescription() == null) {
                if("original".equals(vehicleCharge.getPurpose())) {
                    charge.setDescription("Original price");
                }
                if("preferred".equals(vehicleCharge.getPurpose())) {
                    charge.setDescription("Preferred price");
                }
            } else {
                charge.setDescription(vehicleCharge.getDescription());
            }
            charges.add(charge);
        }
        return charges;
    }

    private String makeSupplierAddress(VehRateRuleRS.LocationDetails locationDetail) {

        StringBuilder supplieraddress = new StringBuilder(locationDetail.getName());
        List<AddressInfoType> addresses = locationDetail.getAddress();
        if(addresses != null && addresses.size() > 0) {
            AddressInfoType addressInfoType = addresses.get(0);
            supplieraddress.append(", ").append(addressInfoType.getStreetNmbr().getValue())
                    .append(", ").append(addressInfoType.getCityName())
                    .append(", ").append(addressInfoType.getCountryName().getValue());
        }
        return supplieraddress.toString();
    }

    private List<String> makeCarSearchEntryDetailPhones(List<VehicleLocationDetailsType.Telephone> telephoneList) {
        if(telephoneList == null || telephoneList.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return telephoneList.stream().map(t -> t.getPhoneNumber()).collect(Collectors.toList());
    }

    private List<CarSearchLocationDetail.OperationSchedule> makeOperationSchedules(VehicleLocationAdditionalDetailsType additionalInfo) {
        if(additionalInfo == null || additionalInfo.getOperationSchedules() == null
                || additionalInfo.getOperationSchedules().getOperationSchedule() == null || additionalInfo.getOperationSchedules().getOperationSchedule().size() == 0) {
            return Collections.EMPTY_LIST;
        }
        OperationScheduleType operationScheduleType = additionalInfo.getOperationSchedules().getOperationSchedule().get(0);
        if(operationScheduleType == null || operationScheduleType.getOperationTimes() == null
                || operationScheduleType.getOperationTimes().getOperationTime() == null || operationScheduleType.getOperationTimes().getOperationTime().size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<ArrayOfOperationScheduleTypeOperationTime.OperationTime> operationTimes = operationScheduleType.getOperationTimes().getOperationTime();
        List<CarSearchLocationDetail.OperationSchedule> operationScheduleResults = new ArrayList<>();
        for(ArrayOfOperationScheduleTypeOperationTime.OperationTime operationTime : operationTimes) {
            String dayOfWeekStr = makeDayOfWeekStr(operationTime);
            if(dayOfWeekStr == null) {
                break;
            }
            CarSearchLocationDetail.OperationSchedule operationScheduleResult = new CarSearchLocationDetail.OperationSchedule();
            operationScheduleResult.setDayOfTheWeek(dayOfWeekStr);
            operationScheduleResult.setOpeningTime(operationTime.getStart());
            operationScheduleResult.setClosingTime(operationTime.getEnd());
            operationScheduleResults.add(operationScheduleResult);
        }
        return operationScheduleResults;
    }

    private String makeDayOfWeekStr(ArrayOfOperationScheduleTypeOperationTime.OperationTime operationTime) {
        if(operationTime.isMon() != null && operationTime.isMon()) {
            return "Monday";
        }
        if(operationTime.isTue() != null && operationTime.isTue()) {
            return "Tuesday";
        }
        if(operationTime.isWeds() != null && operationTime.isWeds()) {
            return "Wednesday";
        }
        if(operationTime.isThur() != null && operationTime.isThur()) {
            return "Thursday";
        }
        if(operationTime.isFri() != null && operationTime.isFri()) {
            return "Friday";
        }
        if(operationTime.isSat() != null && operationTime.isSat()) {
            return "Saturday";
        }
        if(operationTime.isSun() != null && operationTime.isSun()) {
            return "Sunday";
        }
        return null;
    }

    private CarSearchLocationDetail makeCarSearchLocationDetail(VehRateRuleRS.LocationDetails locationDetail) {
        CarSearchLocationDetail carSearchLocationDetail = new CarSearchLocationDetail();
        carSearchLocationDetail.setSupplierAddress(makeSupplierAddress(locationDetail));
        carSearchLocationDetail.setSupplierLocationCode(locationDetail.getCodeContext());
        carSearchLocationDetail.setAtAirport(locationDetail.isAtAirport());
        carSearchLocationDetail.setPhones(makeCarSearchEntryDetailPhones(locationDetail.getTelephone()));
        carSearchLocationDetail.setOperationSchedules(makeOperationSchedules(locationDetail.getAdditionalInfo()));
        return carSearchLocationDetail;
    }

    /*---------end makeCarSearchEntryDetail -------------*/

    protected boolean testB(Boolean b)
    {
        if (b == null)
            return false;
        else
            return b.booleanValue();
    }


    protected String makeSupplierAccess(VehicleLocationAdditionalDetailsType vehicleLocationAdditionalDetails)
    {
        log.debug("makeSupplierAccess::entered");
        if (vehicleLocationAdditionalDetails != null && vehicleLocationAdditionalDetails.getCounterLocation() != null && vehicleLocationAdditionalDetails.getCounterLocation().getLocation() != null)
        {
            String ct = makeCarTrawlerSupplierAccess(vehicleLocationAdditionalDetails.getCounterLocation().getLocation());
            if (ct != null)
            {
                log.debug("makeSupplierAccess::ct special=" + ct);
                return ct;
            }
        }

        StringBuffer buf = new StringBuffer();
        if (vehicleLocationAdditionalDetails != null && vehicleLocationAdditionalDetails.getParkLocation() != null)
        {
            String val = getOtaUtils().getCodeTableValue("VWF", vehicleLocationAdditionalDetails.getParkLocation().getLocation());
            if (val != null && val.length() > 0)
            {
                if (buf.length() > 0)
                    buf.append(", ");
                buf.append("Car Rental Parking:" + val);
            }
        }
        if (vehicleLocationAdditionalDetails != null && vehicleLocationAdditionalDetails.getCounterLocation() != null)
        {
            String val = getOtaUtils().getCodeTableValue("VWF", vehicleLocationAdditionalDetails.getCounterLocation().getLocation());
            if (val != null && val.length() > 0)
            {
                if (buf.length() > 0)
                    buf.append(", ");
                buf.append("Car Rental Counter:" + val);
            }
        }
        log.debug("makeSupplierAccess::buf=" + buf.toString());
        if (buf.length() > 0)
        {
            return buf.toString();
        }
        return null;
    }

    private String makeCarTrawlerSupplierAccess(String s)
    {
        log.debug("makeCarTrawlerSupplierAccess::entered for " + s);
        if (s.equals("VWF_1.VWF.X"))
            return "Terminal Counter And Car";
        if (s.equals("VWF_2.VWF.X"))
            return "Shuttle To Counter And Car";
        if (s.equals("VWF_3.VWF.X"))
            return "Terminal Counter Shuttle To Car";
        if (s.equals("VWF_4.VWF.X"))
            return "Meet And Greet";
        if (s.equals("VWF_5.VWF.X"))
            return null; // unknown
        if (s.equals("VWF_6.VWF.X"))
            return "Car And Driver";
        return null;
    }

    private Integer makeCarTrawlerSupplierAccessCode(String s)
    {
        log.debug("makeCarTrawlerSupplierAccess::entered for " + s);
        if (s.equals("VWF_1.VWF.X"))
            return 1;
        if (s.equals("VWF_2.VWF.X"))
            return 2;
        if (s.equals("VWF_3.VWF.X"))
            return 3;
        if (s.equals("VWF_4.VWF.X"))
            return 4;
        if (s.equals("VWF_5.VWF.X"))
            return 5; // unknown
        if (s.equals("VWF_6.VWF.X"))
            return 6;
        return null;
    }


}
