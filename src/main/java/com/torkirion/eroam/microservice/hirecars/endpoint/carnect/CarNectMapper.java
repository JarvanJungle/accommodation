package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.carnect.schemas.message.*;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.dto.HireCarSearchRQDTO;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util.CarnectUtil;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util.OTAUtils;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util.SIPPUtil;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CarNectMapper extends VehicleDetail {

    private CarNectMapper() {

    }

    private static CarNectMapper instance;

    public static CarNectMapper getInstance() {
        if(instance == null) {
            instance = new CarNectMapper();
        }
        return instance;
    }

    public static VehAvailRateRQ makeVehAvailRateRQ(HireCarSearchRQDTO searchRQ, CredentialDTO credential) throws DatatypeConfigurationException {
        VehAvailRateRQ vehAvailRateRQ = new VehAvailRateRQ();
        vehAvailRateRQ.setPOS(makePOS(credential));
        vehAvailRateRQ.setVehAvailRQCore(makeVehAvailRQCore(searchRQ));
        vehAvailRateRQ.setMaxResponses(CarNectService.MAX_RESPONSE_OF_SEARCH);
        return vehAvailRateRQ;
    }

    private static ArrayOfSourceType makePOS(CredentialDTO credential) {
        SourceType credentialSourceType = new SourceType();
        credentialSourceType.setISOCountry("EN");

        SourceType.RequestorID credentialRequestorID = new SourceType.RequestorID();
        credentialRequestorID.setType(credential.getUsername());
        credentialRequestorID.setIDContext(credential.getPassword());

        credentialSourceType.setRequestorID(credentialRequestorID);

        ArrayOfSourceType arrayOfSourceType = new ArrayOfSourceType();
        arrayOfSourceType.getSource().add(credentialSourceType);
        return arrayOfSourceType;
    }

    private static VehicleAvailRQCoreType makeVehAvailRQCore(HireCarSearchRQDTO searchRQ) throws DatatypeConfigurationException {
        VehicleAvailRQCoreType vehicleAvailRQCoreType = new VehicleAvailRQCoreType();
        vehicleAvailRQCoreType.setRateQueryType(RateQueryType.LIVE);
        vehicleAvailRQCoreType.setRateQueryParameterType("6"); //search by Geo coordinates

        VehicleRentalCoreType vehRentalCore = new VehicleRentalCoreType();

        String pickupDateStartStr = CarNectService.df2YYYYMMDDTHHMMSS.format(searchRQ.getPickupDateTime());
        //log.debug("makeVehAvailRQCore::pickupDateStartStr: {}", pickupDateStartStr);

        XMLGregorianCalendar pickupDateStart = DatatypeFactory.newInstance().newXMLGregorianCalendar(pickupDateStartStr);
        vehRentalCore.setPickUpDateTime(pickupDateStart);

        String returnDateTimeStr = CarNectService.df2YYYYMMDDTHHMMSS.format(searchRQ.getDropoffDateTime());
        //log.debug("makeVehAvailRQCore::returnDateTimeStr: {}", returnDateTimeStr);

        XMLGregorianCalendar returnDateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(returnDateTimeStr);
        vehRentalCore.setReturnDateTime(returnDateTime);

        vehRentalCore.getPickUpLocation().add(makePickUpLocation(searchRQ.getPickupLocation()));
        vehRentalCore.setReturnLocation(makeReturnLocation(searchRQ.getPickupLocation()));


        vehicleAvailRQCoreType.setVehRentalCore(vehRentalCore);
        return vehicleAvailRQCoreType;
    }

    private static VehicleRentalCoreType.PickUpLocation makePickUpLocation(HireCarSearchRQ.BoundingBox boundingBox) {
        LatitudeLongitude northwest = boundingBox.getNorthwest();
        LatitudeLongitude southeast = boundingBox.getSoutheast();
        BigDecimal centerlatitude = northwest.getLatitude().add(southeast.getLatitude()).divide(BIGDECIMAL_2);
        BigDecimal centerLongitude = northwest.getLongitude().add(southeast.getLongitude()).divide(BIGDECIMAL_2);
        VehicleRentalCoreType.PickUpLocation pickUpLocation = new VehicleRentalCoreType.PickUpLocation();
        pickUpLocation.setCodeContext("3"); ////search by Geo coordinates
        pickUpLocation.setLocationCode(centerlatitude.toString() + "," + centerLongitude.toString());
        return pickUpLocation;
    }

    private static VehicleRentalCoreType.ReturnLocation makeReturnLocation(HireCarSearchRQ.BoundingBox boundingBox) {
        LatitudeLongitude northwest = boundingBox.getNorthwest();
        LatitudeLongitude southeast = boundingBox.getSoutheast();
        BigDecimal centerlatitude = northwest.getLatitude().add(southeast.getLatitude()).divide(BIGDECIMAL_2);
        BigDecimal centerLongitude = northwest.getLongitude().add(southeast.getLongitude()).divide(BIGDECIMAL_2);
        VehicleRentalCoreType.ReturnLocation returnLocation = new VehicleRentalCoreType.ReturnLocation();
        returnLocation.setCodeContext("3"); ////search by Geo coordinates
        returnLocation.setLocationCode(centerlatitude.toString() + "," + centerLongitude.toString());
        return returnLocation;
    }

    /*---Map for response-----------------------------------------*/
    public HireCarResult makeHireCarResult(VehAvailRateRS rateRS) throws Exception {
        log.debug("makeHireCarResult::entered");
        SIPPUtil sippUtil = new SIPPUtil();
        HireCarResult results = new HireCarResult();
        Map<String, SIPPBlock> sipps = new HashMap<String, SIPPBlock>();
        log.debug("makeCarSearchResults::avail entries=" + rateRS.getVehAvailRSCore().getVehVendorAvails().getVehVendorAvail().size());
        for (VehicleVendorAvailabilityType vendorAvail : rateRS.getVehAvailRSCore().getVehVendorAvails().getVehVendorAvail())
        {
            log.debug("makeCarSearchResults::vehAvail entries=" + vendorAvail.getVehAvails().getVehAvail().size());
            for (VehicleVendorAvailabilityType.VehAvails.VehAvail vehAvail : vendorAvail.getVehAvails().getVehAvail())
            {
                if (vehAvail.getVehAvailCore().getStatus().equals(InventoryStatusType.AVAILABLE))
                {
                    //ACRISS code of the vehicle
                    String sipp = vehAvail.getVehAvailCore().getVehicle().getVehMakeModel().getCode();
                    SIPPBlock sippBlock = sipps.get(sipp);
                    if (sippBlock == null)
                    {
                        sippBlock = sippUtil.makeSIPPBlock(sipp);
                        if ( sippBlock == null )
                        {
                            log.debug("makeCarSearchResults::empty SIPP, bypassing");
                            continue;
                        }
                        sipps.put(sipp, sippBlock);
                    }
                    CarSearchEntry carSearchEntry = makeCarSearchEntry(vehAvail, vendorAvail, rateRS);
                    sippBlock.getCarSearchEntries().add(carSearchEntry);
                }
                else
                {
                    log.debug("makeCarSearchResults::vehAvail rejected, status=" + vehAvail.getVehAvailCore().getStatus() + " for vendor " + vendorAvail.getVendor().getCompanyShortName());
                }
            }
        }

        if (rateRS.getErrors() != null && rateRS.getErrors().getError() != null && rateRS.getErrors().getError().size() > 0)
        {
            List<Error> errors = new ArrayList<Error>();
            for (ErrorType otaError : rateRS.getErrors().getError())
            {
                String code = otaError.getCode();
                if ( code == null && otaError.getType() != null )
                {
                    code = otaError.getType();
                }
                Error error = new Error("code: " + code + " - ShortText: " + otaError.getShortText() + " value: " + otaError.getValue());
                errors.add(error);
            }
            results.setErrors(errors);
        }
        SortedSet<SIPPBlock> sippSet = new TreeSet<SIPPBlock>();
        sippSet.addAll(sipps.values());
        List<SIPPBlock> sippList = new ArrayList<SIPPBlock>();
        sippList.addAll(sippSet);
        results.setSippBlocks(sippList);
        return results;
    }

    /*-------------------------------------------------------------*/



    /*------vehAvailRateRule----------------------------------------*/
    public VehRateRuleRQ makVehRateRuleRQ(StartDetailRQDTO startDetailRQ, CredentialDTO credential) {
        VehRateRuleRQ rateRuleRQ = new VehRateRuleRQ();
        rateRuleRQ.setPOS(makePOS(credential));
        UniqueIDType uniqueIDType = new UniqueIDType();
        uniqueIDType.setType("16");
        uniqueIDType.setIDContext(startDetailRQ.getIdContext());
        rateRuleRQ.setReference(uniqueIDType);
        return rateRuleRQ;
    }

    public VehRateRuleRQ makVehRateRuleRQCarnectIdContext(String carnectIdContext, CredentialDTO credential) {
        VehRateRuleRQ rateRuleRQ = new VehRateRuleRQ();
        rateRuleRQ.setPOS(makePOS(credential));
        UniqueIDType uniqueIDType = new UniqueIDType();
        uniqueIDType.setType("16");
        uniqueIDType.setIDContext(carnectIdContext);
        rateRuleRQ.setReference(uniqueIDType);
        return rateRuleRQ;
    }

    public HireCarDetailResult makeHireCarDetailResult(VehRateRuleRS rateRuleRS, StartDetailRQDTO startDetailRQ) throws Exception{
        log.debug("makeCarDetail::entered");
        HireCarDetailResult results = new HireCarDetailResult();
        ResponseLocationCodes locationCodes = new ResponseLocationCodes(startDetailRQ.getPickupLocationCode(), startDetailRQ.getDropOffLocationCode());
        results.setCarSearchEntryDetailed(makeCarSearchEntryDetail(rateRuleRS, locationCodes));
        return results;
    }
    /*--------------------------------------------------------------*/

    /*----Start book------------------------------------------------*/
    public VehResRQ makeVehResRQ(HireCarBookRQ bookRQ, CredentialDTO credential) throws Exception {
        VehResRQ vehResRQ = new VehResRQ();
        vehResRQ.setPOS(makePOS(credential));
        vehResRQ.setVehResRQCore(makeVehResRQCore(bookRQ));
        vehResRQ.setVehResRQInfo(makeVehResRQInfo(bookRQ));
        return vehResRQ;
    }

    private VehResRQ.VehResRQInfo makeVehResRQInfo(HireCarBookRQ bookRQ) {
        VehResRQ.VehResRQInfo vehResRQInfo = new VehResRQ.VehResRQInfo();
        if(bookRQ.getVehicleData() == null || bookRQ.getVehicleData() == null ) {
            return vehResRQInfo;
        }
        HireCarBookRQ.VehicleData vehicleData = bookRQ.getVehicleData();
        if(vehicleData.getFlightNumber() == null || "".equals(vehicleData.getFlightNumber())) {
            return vehResRQInfo;
        }
        /* add fight number
        VehicleArrivalDetailsType vehicleArrivalDetailsType = new VehicleArrivalDetailsType();
        /*TransportationCode=14 indicates plane, as given by the OTA Transportation Code table
        vehicleArrivalDetailsType.setTransportationCode("14");
        vehicleArrivalDetailsType.setNumber(vehicleData.getFlightNumber());
        vehResRQInfo.setArrivalDetails(vehicleArrivalDetailsType);
        */
        return vehResRQInfo;
    }

    private VehResRQ.VehResRQCore makeVehResRQCore(HireCarBookRQ bookRQ) throws Exception {
        CarnectUtil.CarnectKey carnectKey = CarnectUtil.getInstance().makeCarnectKeyFromVehicleId(bookRQ.getVehicleData().getVehicleID());
        VehResRQ.VehResRQCore vehResRQCore = new VehResRQ.VehResRQCore();

//        UniqueIDType internalKey = new UniqueIDType();
//        internalKey.setID(bookRQ.getInternalBookingReference());
//        internalKey.setType("14"); //
//        vehResRQCore.getUniqueID().add(internalKey);

        vehResRQCore.setCustomer(makeCustomer(bookRQ));

        VehiclePrefType vehiclePref = new VehiclePrefType();
        vehiclePref.setCode(carnectKey.getIdContext());
        vehResRQCore.setVehPref(vehiclePref);
        if(bookRQ.getVehicleData() != null) {
            vehResRQCore.setSpecialEquipPrefs(makeSpecialEquipPrefs(bookRQ.getVehicleData().getExtras()));
        }
        return vehResRQCore;
    }

    private VehicleReservationRQCoreType.SpecialEquipPrefs makeSpecialEquipPrefs(List<HireCarBookRQ.VehicleExtra> extras) {
        if(extras == null || extras.isEmpty()) {
            return new VehicleReservationRQCoreType.SpecialEquipPrefs();
        }
        VehicleReservationRQCoreType.SpecialEquipPrefs specialEquipPrefs = new VehicleReservationRQCoreType.SpecialEquipPrefs();
        for(HireCarBookRQ.VehicleExtra extra : extras) {
            VehicleReservationRQCoreTypeSpecialEquipPrefsSpecialEquipPref equipPref = new VehicleReservationRQCoreTypeSpecialEquipPrefsSpecialEquipPref();
            equipPref.setEquipType(extra.getExtrasID());
            equipPref.setQuantity(new BigInteger(String.valueOf(extra.getExtrasCount())));
            specialEquipPrefs.getSpecialEquipPref().add(equipPref);
        }
        return specialEquipPrefs;
    }

    private CustomerPrimaryAdditionalType makeCustomer(HireCarBookRQ bookRQ) throws Exception {
        HireCarBookRQ.Booker booker = bookRQ.getBooker();
        if(booker == null ||  booker.getTelephone() == null || booker.getSurname() == null || booker.getGivenName() == null) {
            throw new Exception("miss input booker");
        }
        CustomerPrimaryAdditionalType customer = new CustomerPrimaryAdditionalType();
        CustomerPrimaryAdditionalType.Primary primary = new CustomerPrimaryAdditionalType.Primary();
        //primary.setGender();
        //primary.setBirthDate();
        primary.setLanguage("EN");
        String birthDateStr = CarNectService.df2YYYYMMDD.format(booker.getBirthDate());
        primary.setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(birthDateStr));
        PersonNameType personName = new PersonNameType();
        personName.getGivenName().add(booker.getGivenName());
        personName.setSurname(booker.getSurname());
        personName.getNameTitle().add(booker.getTitle());
        primary.setPersonName(personName);

        CustomerType.Telephone telephone = new CustomerType.Telephone();
        telephone.setPhoneTechType("1");
        telephone.setPhoneNumber(booker.getTelephone());
        primary.getTelephone().add(telephone);

        EmailType email = new EmailType();
        email.setValue(booker.getEmail());
        primary.getEmail().add(email);

        CustomerType.Address address = new CustomerType.Address();
        if(booker.getAddress() != null) {
            AddressType.StreetNmbr streetNmbr = new AddressType.StreetNmbr();
            streetNmbr.setValue(booker.getAddress().getStreetNmbr());
            address.setStreetNmbr(streetNmbr);
            address.setCityName(booker.getAddress().getCityName());
            address.setPostalCode(booker.getAddress().getPostalCode());
        }
        primary.getAddress().add(address);

        CustomerType.CitizenCountryName citizenCountry = new CustomerType.CitizenCountryName();
        citizenCountry.setCode(bookRQ.getCountryCodeOfOrigin());
        primary.getCitizenCountryName().add(citizenCountry);
        customer.setPrimary(primary);
        return customer;
    }

    public HireCarBookRS makeHireCarBookRS(VehResRS vehResRS, VehRateRuleRS vehRateRuleRS, ResponseLocationCodes locationCodes,
                                           HireCarBookRQ bookRQ,  CarSearchEntry carSearchEntryBase) throws Exception {
        log.debug("makeHireCarBookRS::entered");
        HireCarBookRS bookRS = new HireCarBookRS();
        CarSearchEntryDetailed carSearchEntryDetailed = makeCarSearchEntryDetail(vehRateRuleRS, locationCodes);
        bookRS.setCarSearchResult(carSearchEntryBase);
        String reservationStatus = vehResRS.getVehResRSCore().getReservationStatus();
        if("Confirmed".equals(reservationStatus)) {
            bookRS.setStatus(HireCarBookRS.BookingStatus.BOOKED);
        } else {
            bookRS.setStatus(HireCarBookRS.BookingStatus.FAILED);
        }
        VehicleReservationType.VehSegmentCore vehSegmentCore = vehResRS.getVehResRSCore().getVehReservation().getVehSegmentCore();
        Optional<UniqueIDType> idContextOptional = vehSegmentCore.getConfID().stream().filter(c -> "2".equals(c.getType())).findFirst();
        if(idContextOptional.isPresent()) {
            bookRS.setBookingReference(idContextOptional.get().getIDContext());
        }

        List<Element> tpaExtensions = vehResRS.getVehResRSInfo().getTPAExtensions().getAny().stream().map(extension -> (Element) extension).collect(Collectors.toList());
        Optional<Element> productInformationOptional = tpaExtensions.stream().filter(tpaE -> "ProductInformation".equals(tpaE.getLocalName())).findFirst();
        if(productInformationOptional.isPresent()) {
            bookRS.setEndpointReference(productInformationOptional.get().getAttribute("url"));
        }

        List<HireCarEntryExtra> extrasProvided = carSearchEntryDetailed.getExtras();
        for(HireCarBookRQ.VehicleExtra extraRQ : bookRQ.getVehicleData().getExtras()) {
            for(HireCarEntryExtra extraProvided : extrasProvided) {
                if(!extraRQ.getExtrasID().equals(extraProvided.getCode())) {
                    continue;
                }
                HireCarBookRS.Extra extra = new HireCarBookRS.Extra(extraProvided);
                extra.setExtrasCount(extraRQ.getExtrasCount());
                bookRS.getExtrasSelected().add(extra);
            }
        }

        bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
        bookRS.setPickupLocation(new HireCarBookingLocation(carSearchEntryDetailed.getPickupDetail(), carSearchEntryBase.getPickup()));
        bookRS.setDropoffLocation(new HireCarBookingLocation(carSearchEntryDetailed.getDropoffDetail(), carSearchEntryBase.getDropoff()));
        bookRS.setCancellationPolicies(carSearchEntryDetailed.getCancellationPolicies());
        return bookRS;
    }

    /*----End book------------------------------------------------*/

    /*--Start cancel----------------------------------------------*/
    public VehCancelResRQ makeVehCancelResRQ(HireCarCancelRQ cancelRQ, CredentialDTO credential) {
        VehCancelResRQ vehCancelResRQ = new VehCancelResRQ();
        vehCancelResRQ.setPOS(makePOS(credential));
        vehCancelResRQ.setVehCancelRQCore(makeVehCancelRQCore(cancelRQ));
        return vehCancelResRQ;
    }

    private CancelInfoRQType makeVehCancelRQCore(HireCarCancelRQ cancelRQ) {
        CancelInfoRQType vehCancelRQCore = new CancelInfoRQType();
        vehCancelRQCore.setCancelType(TransactionActionType.BOOK);
        UniqueIDType uniqueID = new UniqueIDType();
        uniqueID.setIDContext(cancelRQ.getBookingReference());
        vehCancelRQCore.getUniqueID().add(uniqueID);
        PersonNameType personName = new PersonNameType();
        personName.setSurname(cancelRQ.getBooker().getSurname());
        vehCancelRQCore.setPersonName(personName);
        return vehCancelRQCore;
    }

    public HireCarCancelRS makeHireCarCancelRS(VehCancelResRS vehCancelResRS, HireCarCancelRQ hireCarCancelRQ) {
        HireCarCancelRS hireCarCancelRS = new HireCarCancelRS();
        hireCarCancelRS.setInternalBookingReference(hireCarCancelRQ.getInternalBookingReference());
        if(vehCancelResRS == null || vehCancelResRS.getVehCancelRSCore() == null) {
            hireCarCancelRS.setStatus(HireCarBookRS.BookingStatus.FAILED);
            return hireCarCancelRS;
        }
        if(TransactionStatusType.CANCELLED == vehCancelResRS.getVehCancelRSCore().getCancelStatus()) {
            hireCarCancelRS.setStatus(HireCarBookRS.BookingStatus.CANCELLED);
        } else {
            hireCarCancelRS.setStatus(HireCarBookRS.BookingStatus.FAILED);
        }
        VehicleCancelRSAdditionalInfoType vehCancelRSInfo = vehCancelResRS.getVehCancelRSInfo();
        if(vehCancelRSInfo != null && vehCancelRSInfo.getVehReservation() != null) {
            hireCarCancelRS.setCancellationFrees(makeCancellationFree(vehCancelRSInfo));
            hireCarCancelRS.setNotes(makeHireCarCancelRSNotes(vehCancelRSInfo));
        }
        return hireCarCancelRS;
    }

    private List<HireCarCancelRS.CancellationFree> makeCancellationFree(VehicleCancelRSAdditionalInfoType vehCancelRSInfo) {
        VehicleReservationType vehReservation = vehCancelRSInfo.getVehReservation();
        if(vehReservation == null || vehReservation.getVehSegmentCore() == null
                || vehReservation.getVehSegmentCore().getFees() == null
                || vehReservation.getVehSegmentCore().getFees().getFee().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<VehicleChargePurposeType> fees = vehReservation.getVehSegmentCore().getFees().getFee();
        if(fees == null || fees.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<HireCarCancelRS.CancellationFree> feeResults = new ArrayList<>();
        for(VehicleChargePurposeType fee : fees) {
            HireCarCancelRS.CancellationFree feeResult = new HireCarCancelRS.CancellationFree();
            feeResult.setFee(new CurrencyValue(fee.getCurrencyCode(), fee.getAmount()));
            feeResult.setDescription(fee.getDescription());
            feeResult.setTaxInclusive(fee.isTaxInclusive() != null && fee.isTaxInclusive());
            feeResult.setIncludedInRate(fee.isIncludedInEstTotalInd() != null && fee.isIncludedInEstTotalInd());
            feeResults.add(feeResult);
        }
        return feeResults;
    }

    private String makeHireCarCancelRSNotes(VehicleCancelRSAdditionalInfoType vehCancelRSInfo) {
        if(vehCancelRSInfo.getVehReservation() == null) {
            return null;
        }
        return vehCancelRSInfo.getVehReservation().getReservationStatus();
    }

    /*--End cancel----------------------------------------------*/
    private static BigDecimal BIGDECIMAL_2 = new BigDecimal(2);

    @Override
    protected CarSearchEntry createCarSearchEntry() {
        return new CarSearchEntry();
    }

    @Override
    public OTAUtils getOtaUtils() {
        return new OTAUtils();
    }
}
