package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.carnect.schemas.message.*;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.datadomain.CarSearchEntryRCData;
import com.torkirion.eroam.microservice.hirecars.datadomain.CarSearchEntryRCRepo;
import com.torkirion.eroam.microservice.hirecars.dto.DetailRQDTO;
import com.torkirion.eroam.microservice.hirecars.dto.HireCarSearchRQDTO;
import com.torkirion.eroam.microservice.hirecars.endpoint.ApiResponse;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.util.CarnectUtil;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class CarnectInterface {
    private CarnectApiProperties carnectApiProperties;
    final static String PING_SOAP_ACTION = "http://www.opentravel.org/OTA/2003/05/getPing";
    final static String VEH_AVAIL_RATE_SOAP_ACTION = "http://www.opentravel.org/OTA/2003/05/getVehAvailRate";
    final static String VEH_RATE_RULE_SOAP_ACTION = "http://www.opentravel.org/OTA/2003/05/getVehRateRule";
    final static String VEH_RESERVATION_SOAP_ACTION = "http://www.opentravel.org/OTA/2003/05/getVehReservation";
    final static String VEH_RET_RESERVATION_SOAP_ACTION = "http://www.opentravel.org/OTA/2003/05/getVehRetReservation";
    final static String VEH_CANCELLATION_SOAP_ACTION = "http://www.opentravel.org/OTA/2003/05/getVehCancelReservation";
    final static String CARNECT_SERVICE_URI = "service.asmx";

    private final CredentialDTO credential;
    private final CarSearchEntryRCRepo carSearchEntryRCRepo;

    public CarnectInterface(SystemPropertiesDAO properties, String site, CarSearchEntryRCRepo carSearchEntryRCRepo) {
        carnectApiProperties = new CarnectApiProperties(properties, site);
        credential = new CredentialDTO(carnectApiProperties.username, carnectApiProperties.password);
        this.carSearchEntryRCRepo = carSearchEntryRCRepo;
    }

    private HttpService getHttpService(String soapAction) {
        return new CarnectHttpService(carnectApiProperties, soapAction);
    }

    public void startPing() throws Exception {
        log.debug("startPing::");
        PingRS pingRS = doCallPing();
        log.debug("startPing::pingRSApiResponse: \n{}", JsonUtil.convertToPrettyJson(pingRS));
    }

    public List<HireCarResult> startSearch(HireCarSearchRQDTO searchRQ) throws Exception {
        log.debug("startSearch::");
        VehAvailRateRQ vehAvailRateRQ = CarNectMapper.makeVehAvailRateRQ(searchRQ, credential);
        VehAvailRateRS vehAvailRateRS = doCallVehAvailRate(vehAvailRateRQ);
        return List.of(CarNectMapper.getInstance().makeHireCarResult(vehAvailRateRS));
    }

    public HireCarDetailResult startDetail(StartDetailRQDTO startDetailRQ) throws Exception {
        log.debug("startDetail::");
        VehRateRuleRQ rateRuleRQ = CarNectMapper.getInstance().makVehRateRuleRQ(startDetailRQ, credential);
        VehRateRuleRS vehRateRuleRS = doCallVehRateRule(rateRuleRQ);
        return CarNectMapper.getInstance().makeHireCarDetailResult(vehRateRuleRS, startDetailRQ);
    }

    public HireCarBookRS startBook(HireCarBookRQ bookRQ) throws Exception {
        log.debug("startBook::bookRQ: {}", JsonUtil.convertToPrettyJson(bookRQ));
        /*firstly we have to get rate rule to get some fields for response data*/
        CarnectUtil.CarnectKey carnectKey = CarnectUtil.getInstance().makeCarnectKeyFromVehicleId(bookRQ.getVehicleData().getVehicleID());
        VehRateRuleRQ rateRuleRQ = CarNectMapper.getInstance().makVehRateRuleRQCarnectIdContext(carnectKey.getIdContext(), credential);
        VehRateRuleRS vehRateRuleRS = doCallVehRateRule(rateRuleRQ);
        Optional<CarSearchEntryRCData> carSearchEntryRCDataOptional = carSearchEntryRCRepo.findById(bookRQ.getVehicleData().getVehicleID());
        if(!carSearchEntryRCDataOptional.isPresent()) {
            throw new Exception("internal error");
        }
        CarSearchEntryRCData carSearchEntryRCData = carSearchEntryRCDataOptional.get();
        CarSearchEntry carSearchEntry =  JsonUtil.parse(carSearchEntryRCData.getCarSearchEntryJson(), CarSearchEntry.class);
        if(carSearchEntry == null) {
            throw new Exception("internal error");
        }
        VehResRQ vehResRQ = CarNectMapper.getInstance().makeVehResRQ(bookRQ, credential);
        VehResRS vehResRS = doCallVehReservation(vehResRQ);
        ResponseLocationCodes pickupAndDropOffLocationCodes = new ResponseLocationCodes(carnectKey.getPickupLocationCode(), carnectKey.getDropOffLocationCode());

        return CarNectMapper.getInstance().makeHireCarBookRS(vehResRS, vehRateRuleRS, pickupAndDropOffLocationCodes, bookRQ, carSearchEntry);
    }

    public HireCarCancelRS startCancel(HireCarCancelRQ cancelRQ) throws Exception {
        log.debug("startCancel::");
        VehCancelResRQ vehCancelResRQ = CarNectMapper.getInstance().makeVehCancelResRQ(cancelRQ, credential);
        VehCancelResRS vehCancelResRS = doCallVehCancellation(vehCancelResRQ);
        return CarNectMapper.getInstance().makeHireCarCancelRS(vehCancelResRS, cancelRQ);
    }

    private PingRS doCallPing() throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("doCallPing::ping 1.2.3");
        }
        PingRQ pingRQ = new PingRQ();
        pingRQ.setEchoData("Ping 1.2.3");
        String soapMessageRQ = makeSoapMessageRQ(pingRQ, PingRQ.class);
        String pingRsStr = getHttpService(PING_SOAP_ACTION).doCallPost(CARNECT_SERVICE_URI, soapMessageRQ);
        return makeSoapMessageRS(pingRsStr, PingRS.class);
    }

    private VehAvailRateRS doCallVehAvailRate(VehAvailRateRQ vehAvailRateRQ) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("doCallVehAvailRate::vehAvailRateRQ: \n{}", JsonUtil.convertToPrettyJson(vehAvailRateRQ));
        }
        String soapMessageRQ = makeSoapMessageRQ(vehAvailRateRQ, VehAvailRateRQ.class);
        String soapMessageRS = getHttpService(VEH_AVAIL_RATE_SOAP_ACTION).doCallPost(CARNECT_SERVICE_URI, soapMessageRQ);
        VehAvailRateRS vehAvailRateRS = makeSoapMessageRS(soapMessageRS, VehAvailRateRS.class);
        if(vehAvailRateRS.getErrors() != null && !vehAvailRateRS.getErrors().getError().isEmpty()) {
            throw new Exception(vehAvailRateRS.getErrors().getError().stream().map(e -> e.getValue()).collect(Collectors.joining()));
        }
        return vehAvailRateRS;
    }

    private VehRateRuleRS doCallVehRateRule(VehRateRuleRQ vehAvailRateRQ) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("doCallVehRateRule::vehAvailRateRQ: \n{}", JsonUtil.convertToPrettyJson(vehAvailRateRQ));
        }
        String soapMessageRQ = makeSoapMessageRQ(vehAvailRateRQ, VehRateRuleRQ.class);
        String soapMessageRS = getHttpService(VEH_RATE_RULE_SOAP_ACTION).doCallPost(CARNECT_SERVICE_URI, soapMessageRQ);
        log.debug("doCallVehRateRule::soapMessageRS: {}", soapMessageRS);
        VehRateRuleRS vehRateRuleRS = makeSoapMessageRS(soapMessageRS, VehRateRuleRS.class);
        if(vehRateRuleRS.getErrors() != null && !vehRateRuleRS.getErrors().getError().isEmpty()) {
            throw new Exception(vehRateRuleRS.getErrors().getError().stream().map(e -> e.getValue()).collect(Collectors.joining()));
        }
        return vehRateRuleRS;
    }

    private VehResRS doCallVehReservation(VehResRQ vehResRQ) throws Exception {
        log.debug("doCallVehRetReservation::vehRetResRQ: \n{}", JsonUtil.convertToPrettyJson(vehResRQ));
        String soapMessageRQ = makeSoapMessageRQ(vehResRQ, VehResRQ.class);
        String soapMessageRS = getHttpService(VEH_RESERVATION_SOAP_ACTION).doCallPost(CARNECT_SERVICE_URI, soapMessageRQ);
        log.debug("doCallVehRetReservation::soapMessageRS: {}", soapMessageRS);
        VehResRS vehResRS = makeSoapMessageRS(soapMessageRS, VehResRS.class);
        if(vehResRS.getErrors() != null && !vehResRS.getErrors().getError().isEmpty()) {
            throw new Exception(vehResRS.getErrors().getError().stream().map(e -> e.getValue()).collect(Collectors.joining()));
        }
        return vehResRS;
    }

    private VehCancelResRS doCallVehCancellation(VehCancelResRQ vehCancelResRQ) throws Exception {
        log.debug("doCallVehCancellation::vehCancelResRQ: \n{}", JsonUtil.convertToPrettyJson(vehCancelResRQ));
        String soapMessageRQ = makeSoapMessageRQ(vehCancelResRQ, VehCancelResRQ.class);
        String soapMessageRS = getHttpService(VEH_CANCELLATION_SOAP_ACTION).doCallPost(CARNECT_SERVICE_URI, soapMessageRQ);
        log.debug("doCallVehCancellation::soapMessageRS: {}", soapMessageRS);
        VehCancelResRS vehCancelResRS = makeSoapMessageRS(soapMessageRS, VehCancelResRS.class);
        if(vehCancelResRS.getErrors() != null && !vehCancelResRS.getErrors().getError().isEmpty()) {
            throw new Exception(vehCancelResRS.getErrors().getError().stream().map(e -> e.getValue()).collect(Collectors.joining()));
        }
        return vehCancelResRS;
    }

    /*-------common method------*/
    private String makeSoapMessageRQ(Object objectRQ, Class requestClass) throws Exception {
        try {
            Marshaller marshaller = JAXBContext.newInstance(requestClass).createMarshaller();
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE); // required to stop <?xml ... being added ?>
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            marshaller.marshal(objectRQ, outputStream);
            String payload = new String(outputStream.toByteArray());
            String soapMessageRQStr = String.format(SOAP_ENVELOPE_TEMPLATE, payload);
            log.debug("makeSoapMessageRQ::soapMessageRQStr: {}", soapMessageRQStr);
            return soapMessageRQStr;
        } catch (JAXBException e) {
            log.error("makeSoapMessageRQ::error: {}", e.toString());
            throw new Exception("input invalid");
        }
    }

    private  <T> T makeSoapMessageRS(String responseStr, Class<T> clazz) throws Exception {
        if(responseStr == null || "".equals(responseStr)) {
            throw new Exception("Call Carnect failed");
        }
        T responseBody = makeResponseBody(responseStr, clazz);

        if(responseBody == null) {
            throw new Exception("Call Carnect failed");
        }
        return responseBody;
    }

    private InformationHeader makeInformationHeader(String responseStr) {
        int startHeader = responseStr.indexOf("<soap:Header>") + 13;
        Matcher matcherHeader = Pattern.compile("</[^>]*soap:Header>").matcher(responseStr);
        matcherHeader.find();
        int endHeader = matcherHeader.start();
        String headerStr = responseStr.substring(startHeader, endHeader);
        log.debug("makeInformationHeader::headerStr: {}", headerStr);
        try {
            Unmarshaller unmarshallerHeader = JAXBContext.newInstance(InformationHeader.class).createUnmarshaller();
            StringReader headerReader = new StringReader(headerStr);
            return  (InformationHeader)unmarshallerHeader.unmarshal(headerReader);
        } catch (JAXBException e) {
            log.error("makeInformationHeader::error: {}", e.getMessage());
            return null;
        }
    }

    private <T> T makeResponseBody(String responseStr, Class<T> bodyClass) {
        int start = responseStr.indexOf("<soap:Body>") + 11;
        Matcher m = Pattern.compile("</[^>]*soap:Body>").matcher(responseStr);
        m.find();
        int end = m.start();
        String bodyStr = responseStr.substring(start, end);
        //log.debug("makeResponseBody::bodyStr: {}", bodyStr);
        try {
            Unmarshaller unmarshallerHeader = JAXBContext.newInstance(bodyClass).createUnmarshaller();
            StringReader reader = new StringReader(bodyStr);
            return (T) unmarshallerHeader.unmarshal(reader);
        } catch (Exception e) {
            log.error("makeResponseBody::error: {}", e.getMessage());
            return null;
        }
    }

    private static String SOAP_ENVELOPE_TEMPLATE = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soapenv:Header /><soapenv:Body>%s</soapenv:Body></soapenv:Envelope>";
}
