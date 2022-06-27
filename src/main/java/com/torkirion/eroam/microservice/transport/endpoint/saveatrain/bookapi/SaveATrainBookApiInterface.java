package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.endpoint.SaveATrainHttpExecution;
import com.torkirion.eroam.microservice.transport.endpoint.SaveATrainDataResponse;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainAPIProperties;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SaveATrainBookApiInterface {

    public static final String API_VERSION = "1.21";
    private static final String SIGN_IN_BASEBATH = "api/sales_agent_sessions";
    private static final String SEARCH_BASEBATH = "api/searches";
    private static final String VENDOR_STATIONS_BASEBATH = "api/vendor_stations";
    private static final String SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG = "{identifier}";
    private static final String SAVE_A_TRAIN_ROUTE_ID_REG = "{id}";
    private static final String SAVE_A_TRAIN_FARE_ID_REG = "{fare_id}";
    private static final String SUB_ROUTE_BASEBATH = "api/searches/"+ SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG +"/results/" + SAVE_A_TRAIN_ROUTE_ID_REG + "/sub_routes";
    private static final String CONFIRM_SELECTION_BASEBATH = "api/searches/" + SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG + "/confirm_selection";
    private static final String BOOKINGS_BASEBATH = "api/bookings";
    private static final String BOOKINGS_CONFIRM_BASEBATH = "api/bookings/confirm";
    private static final String BOOKINGS_TICKET_BASEBATH = "api/order_images/" + SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG;
    private static final String TARIFF_CONDITION_BASEBATH = "api/searches/" + SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG + "/results/" + SAVE_A_TRAIN_ROUTE_ID_REG + "/tariff_conditions/" + SAVE_A_TRAIN_FARE_ID_REG;


    private String site;
    private String channel;

    private SystemPropertiesDAO properties;

    private HttpService httpService;

    public SaveATrainBookApiInterface(SystemPropertiesDAO properties, String site, String channel) throws Exception
    {
        this.properties = properties;
        this.site = site;
        this.channel = channel;
        init(site, channel);
    }

    private void init(String site, String channel)
    {
        if (log.isDebugEnabled())
            log.debug("init::entering with site " + site + ", channel " + channel + ", properties=" + properties);
        SaveATrainAPIProperties saveATrainAPIProperties = new SaveATrainAPIProperties(properties, site);
        httpService = new SaveATrainBookApiHttpService(saveATrainAPIProperties);
    }

    private synchronized void refreshHttpService(String site) throws Exception {
        log.info("refreshHttpService::(site: {}, datetime: {}", site, Calendar.getInstance().getTime());
        SaveATrainAPIProperties saveATrainAPIProperties = new SaveATrainAPIProperties(properties, site);
        if(httpService == null) {
            httpService = new SaveATrainBookApiHttpService(saveATrainAPIProperties);
        }
        SignInRQDTO signInRQ = SignInRQDTO.builder()
                .email(saveATrainAPIProperties.bookApiEmail)
                .password(saveATrainAPIProperties.bookApiPassword).build();
        SaveATrainBookApiLoginRSDTO signInRs = signInSalesAgent(signInRQ);
        //get new token
        saveATrainAPIProperties.saveBookApiToken(properties, site, signInRs.getAccessToken());

        // refresh httpSaveATrain
        httpService = new SaveATrainBookApiHttpService(saveATrainAPIProperties);
    }

    private SaveATrainBookApiLoginRSDTO signInSalesAgent(SignInRQDTO signInRQ) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("signInSalesAgent");
        }
        String responseStr = httpService.doCallPost(SIGN_IN_BASEBATH, signInRQ);
        if(log.isDebugEnabled()) {
            log.debug("signInSalesAgent::responseStr: {}", responseStr);
        }
        SaveATrainBookApiLoginRSDTO response = JsonUtil.parse(responseStr, SaveATrainBookApiLoginRSDTO.class);
        if(log.isDebugEnabled()) {
            log.debug("signInSalesAgent::response: {}", response);
        }
        if(response == null || StringUtils.isBlank(response.getAccessToken())) {
            throw new Exception("Sign In Sales Agent fail");
        }
        return response;
    }

    /*vendor_stations*/
    public VendorStationRSDTO[] startGetVendorStations(String site) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("startGetVendorStations::(site: {})", site);
        }
        VendorStationRSDTO[] VendorStationRSs = executeHttp(() -> doCallVendorStations());
        return VendorStationRSs;
    }

    private SaveATrainDataResponse<VendorStationRSDTO[]> doCallVendorStations() {
        String vendorStationsStr = httpService.doCallGet(VENDOR_STATIONS_BASEBATH, null);
        if(vendorStationsStr == null || StringUtils.isBlank(vendorStationsStr)) {
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.CALL_FAIL, "stations empty");
        }
        String errorMessage = getResponseErrorMessage(vendorStationsStr);
        if(errorMessage != null) {
            log.error("doCallVendorStations::error: {}", errorMessage);
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.CALL_FAIL, errorMessage);
        }
        VendorStationRSDTO[]  vendorStations = JsonUtil.parse(vendorStationsStr, VendorStationRSDTO[].class);
        if(vendorStations == null) {
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.CALL_FAIL, "stations empty");
        }
        if(log.isDebugEnabled()) {
            log.debug("doCallVendorStations::load all vendor stations from saveAtrain::vendorStations size: {}", vendorStations.length);
        }
        return SaveATrainDataResponse.success(vendorStations);
    }

    private String getResponseErrorMessage(String responseStr) {
        VendorStationErrorRSDTO errorRS = JsonUtil.parse(responseStr, VendorStationErrorRSDTO.class);
        if(errorRS == null) {
            return null;
        }
        if(errorRS.getErrors() == null || StringUtils.isBlank(errorRS.getErrors())) {
            return null;
        }
        return errorRS.getErrors();
    }

    private static final VendorStationRSDTO VENDOR_STATION_ARRAY_EMPTY[] = {};

    /*---search---*/
//    public SaveATrainBookApiSearchRSDTO startSearch(SaveATrainBookApiSearchRQDTO searchTrainRS) throws Exception {
//        if(log.isDebugEnabled()) {
//            log.debug("startSearch::searchTrainRS");
//        }
//        int callTurnNumber = 0;
//        do {
//            SaveATrainDataResponse<SaveATrainBookApiSearchRSDTO> saveATrainRS = doCallSearchTrain(searchTrainRS);
//            if(saveATrainRS.getCode() == SaveATrainDataResponse.Code.SUCCESS) {
//                return saveATrainRS.getData();
//            }
//            callTurnNumber++;
//            refreshHttpService(site);
//        } while (callTurnNumber < NUMBER_RECALL + 1);
//        return null;
//    }
    public SaveATrainStartChooseRSDTO startChoose(SaveATrainStartChooseRQDTO startChooseRQ) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("startChoose::startChooseRQ: \n {}", JsonUtil.convertToPrettyJson(startChooseRQ));
        }
        SaveATrainBookApiSearchRQDTO searchRQ = SaveATrainBookApiSearchRQDTO.makeSaveATrainBookApiSearchRQ(startChooseRQ);
        SaveATrainBookApiSearchRSDTO searchRS = executeHttp(() -> doCallSearchTrain(searchRQ));
        if(log.isDebugEnabled()) {
            log.debug("startChoose::searchRS: \n {}", JsonUtil.convertToPrettyJson(searchRS));
        }
        List<SaveATrainBookApiSearchRSDTO.Result> results = searchRS.getResults();
        if(results == null || CollectionUtils.isEmpty(results)) {
            return null;
        }
        LocalDateTime searchDepartureLocalDateTime = LocalDateTime.parse(startChooseRQ.getDepartureDatetime(), SaveATrainService.df2YYYYMMDDHHMM);
        results = results.stream().filter(r -> r.getDepartureDatetime().isAfter(searchDepartureLocalDateTime)).collect(Collectors.toList());
        if(results == null || CollectionUtils.isEmpty(results)) {
            return null;
        }
        results.sort((r1, r2) -> r1.getDepartureDatetime().compareTo(r2.getDepartureDatetime()));
        Optional<SaveATrainBookApiSearchRSDTO.Result> optional = results.stream().findFirst();
        if(!optional.isPresent()) {
            return null;
        }
        SaveATrainBookApiSearchRSDTO.Result chosenResult = optional.get();
        if(log.isDebugEnabled()) {
            log.debug("startChoose::chosenResult: \n {}", JsonUtil.convertToPrettyJson(chosenResult));
        }
        SaveATrainBookApiSubRouteRQDTO subRouteRQ = SaveATrainBookApiSubRouteRQDTO.builder()
                .identifier(searchRS.getIdentifier())
                .id(String.valueOf(chosenResult.getId()))
                .build();
        SaveATrainBookApiSubRouteRSDTO subRouteRS = executeHttp(() -> doCallSubRoute(subRouteRQ));
        if(log.isDebugEnabled()) {
            log.debug("startChoose::subRouteRS: \n {}", JsonUtil.convertToPrettyJson(subRouteRS));
        }
        //TODO
        return SaveATrainStartChooseRSDTO.builder()
                .chosenResult(chosenResult)
                .subRouteRS(subRouteRS)
                .build();
    }

    public SaveATrainStartBookRSDTO startBook(SaveATrainStartBookRQDTO bookRQ) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("startBook::bookRQ: \n {}", JsonUtil.convertToPrettyJson(bookRQ));
        }
        SaveATrainConfirmSelectionRQDTO confirmSelectionRQ = makeSaveATrainConfirmSelectionRQDTO(bookRQ);
        SaveATrainConfirmSelectionRSDTO confirmSelectionRs = executeHttp(() -> doCallConfirmSelection(confirmSelectionRQ));
        if(log.isDebugEnabled()) {
            log.debug("startBook::confirmSelectionRs: \n {}", JsonUtil.convertToPrettyJson(confirmSelectionRs));
        }

        SaveATrainMakeBookingRQDTO saveATrainMakeBookingRQ = SaveATrainMakeBookingRQDTO.builder().booking(bookRQ.getBooking()).build();
        SaveATrainMakeBookingRSDTO makeBookingRs = executeHttp(() -> doCallMakeBooking(saveATrainMakeBookingRQ));
        if(log.isDebugEnabled()) {
            log.debug("startBook::makeBookingRs: \n {}", JsonUtil.convertToPrettyJson(makeBookingRs));
        }

        SaveATrainBookingsConfirmRQDTO saveATrainBookingsConfirmRQ = makeSaveATrainBookingsConfirmRSDTO(bookRQ);
        SaveATrainBookingsConfirmRSDTO bookingsConfirmRs = executeHttp(() -> doCallBookingsConfirm(saveATrainBookingsConfirmRQ));
        if(log.isDebugEnabled()) {
            log.debug("startBook::bookingsConfirmRS: \n {}", JsonUtil.convertToPrettyJson(bookingsConfirmRs));
        }

        SaveATrainGetTicketRQDTO saveATrainGetTicketRQ = makeSaveATrainGetTicketRQ(bookRQ);
        SaveATrainGetTicketRSDTO ticketRs = executeHttp(() -> doCallGetTicket(saveATrainGetTicketRQ));
        if(log.isDebugEnabled()) {
            log.debug("startBook::ticketRs: \n {}", JsonUtil.convertToPrettyJson(ticketRs));
        }

        Map<String, String> remarkDetail = new HashMap<>();
        remarkDetail.put("ticketUrl", ticketRs.getUrl());
        remarkDetail.put("ticketConfirmationCode", ticketRs.getConfirmationCode());

        String remark = "ticketUrl: " + ticketRs.getUrl() + ", ticketConfirmationCode: " + ticketRs.getConfirmationCode();


        SaveATrainTariffConditionRQDTO trainTariffConditionRQ = makeSaveATrainTariffConditionRQ(bookRQ);
        SaveATrainTariffConditionRSDTO trainTariffConditionRS = executeHttp(() -> doCallTariffCondition(trainTariffConditionRQ));
        if(log.isDebugEnabled()) {
            log.debug("startBook::trainTariffConditionRS: \n {}", JsonUtil.convertToPrettyJson(trainTariffConditionRS));
        }

        List<SaveATrainTariffConditionRSDTO.Condition> conditions = trainTariffConditionRS.getTariffConditions().getConditions();
        for(SaveATrainTariffConditionRSDTO.Condition condition : conditions) {
            remark += ", " + condition.getName() + ": " + condition.getDescription();
            remarkDetail.put(condition.getName(), condition.getDescription());
        }
        return SaveATrainStartBookRSDTO.builder()
                .confirmSelectionRs(confirmSelectionRs)
                .makeBookingRs(makeBookingRs)
                .bookingsConfirmRs(bookingsConfirmRs)
                .ticketRs(ticketRs)
                .internalItemReference(bookRQ.getInternalItemReference())
                .isSuccess(ticketRs.isSuccess())
                .remark(remark)
                .remarkDetail(remarkDetail)
                .build();
    }

    public SaveATrainStartCancelRSDTO startCancel(SaveATrainStartCancelRQDTO startCancelRQ) {
        if(log.isDebugEnabled()) {
            log.debug("startBook::startCancel: \n {}", JsonUtil.convertToPrettyJson(startCancelRQ));
        }
        SaveATrainDataResponse<SaveATrainCancelRSDTO> saveATranRs = doCallCancel(new SaveATrainCancelRQDTO(startCancelRQ.getConfirmationCode()));
        SaveATrainStartCancelRSDTO cancelRS = new SaveATrainStartCancelRSDTO();
        if(saveATranRs.getCode() == SaveATrainDataResponse.Code.SUCCESS) {
            cancelRS.setSuccess(true);
            cancelRS.setCancelFee(saveATranRs.getData().getCancelFee());
            cancelRS.setConfirmationCode(saveATranRs.getData().getConfirmationCode());
        }
        cancelRS.setSuccess(false);
        cancelRS.setErrors(saveATranRs.getErrors());
        return cancelRS;
    }

    private SaveATrainDataResponse<SaveATrainBookApiSearchRSDTO> doCallSearchTrain(SaveATrainBookApiSearchRQDTO searchTrainRS) {
        long timer1 = System.currentTimeMillis();
        if(log.isDebugEnabled()) {
            log.debug("doCallSearchTrain::start::searchTrainRS::at: {}, searchTrainRS: \n{}", timer1, JsonUtil.convertToPrettyJson(searchTrainRS));
        }
        String saveATrainBookApiSearchRSDTOStr = httpService.doCallPost(SEARCH_BASEBATH, searchTrainRS);
        if(log.isDebugEnabled()) {
            long timer2 = System.currentTimeMillis();
            log.debug("doCallSearchTrain::saveATrainBookApiSearchRSDTOStr: {}, executeTime: {}", saveATrainBookApiSearchRSDTOStr, timer2 - timer1);
        }
        return parseSaveATrain(saveATrainBookApiSearchRSDTOStr, SaveATrainBookApiSearchRSDTO.class);
    }

    /*----Start book------*/
    private SaveATrainDataResponse<SaveATrainBookApiSubRouteRSDTO> doCallSubRoute(SaveATrainBookApiSubRouteRQDTO subRouteRQ) {
        if(log.isDebugEnabled()) {
            log.debug("doCallSubRoute::subRouteRQ: {}", subRouteRQ);
        }
        String subRouteUri = SUB_ROUTE_BASEBATH.replace(SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG, subRouteRQ.getIdentifier())
                .replace(SAVE_A_TRAIN_ROUTE_ID_REG, subRouteRQ.getId());
        String subRouteRsStr = httpService.doCallGet(subRouteUri, null);
        if(log.isDebugEnabled()) {
            log.debug("doCallSubRoute::subRouteRsStr: {}", subRouteRsStr);
        }
        return parseSaveATrain(subRouteRsStr, SaveATrainBookApiSubRouteRSDTO.class);
    }

    /*--group of confirm selection--*/
    private SaveATrainDataResponse<SaveATrainConfirmSelectionRSDTO> doCallConfirmSelection(SaveATrainConfirmSelectionRQDTO confirmSelectionRQ) {
        if(log.isDebugEnabled()) {
            log.debug("doCallConfirmSelection::confirmSelectionRQ: \n {}", JsonUtil.convertToPrettyJson(confirmSelectionRQ));
        }
        String confirmSelectionUrl = CONFIRM_SELECTION_BASEBATH.replace(SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG,
                confirmSelectionRQ.getSelectResultsAttributes().getSearchIdentifier());
        String confirmSelectionRsStr = httpService.doCallPost(confirmSelectionUrl, confirmSelectionRQ);
        if(log.isDebugEnabled()) {
            log.debug("doCallConfirmSelection::confirmSelectionRsStr: {}", confirmSelectionRsStr);
        }
        return parseSaveATrain(confirmSelectionRsStr, SaveATrainConfirmSelectionRSDTO.class);
    }

    private SaveATrainConfirmSelectionRQDTO makeSaveATrainConfirmSelectionRQDTO(SaveATrainStartBookRQDTO bookRQ) {
        SaveATrainConfirmSelectionRQDTO.SelectResultsAttributes selectResultsAttributes = SaveATrainConfirmSelectionRQDTO.SelectResultsAttributes
                .builder()
                .searchIdentifier(bookRQ.getSearchIdentifier())
                .resultId(bookRQ.getSearchResultId())
                .transfersAttributes(bookRQ.getTransfersAttributes())
                .build();
        return SaveATrainConfirmSelectionRQDTO.builder()
                .selectResultsAttributes(selectResultsAttributes)
                .build();
    }
    /*--end group of confirm selection--*/

    private SaveATrainDataResponse<SaveATrainMakeBookingRSDTO> doCallMakeBooking(SaveATrainMakeBookingRQDTO makeBookingRQ) {
        if(log.isDebugEnabled()) {
            log.debug("start::doCallMakeBooking::makeBookingRQ: \n {}", JsonUtil.convertToPrettyJson(makeBookingRQ));
        }
        String makeBookingRsStr = httpService.doCallPost(BOOKINGS_BASEBATH, makeBookingRQ);
        return parseSaveATrain(makeBookingRsStr, SaveATrainMakeBookingRSDTO.class);
    }

    /*----confirm book-----*/
    private SaveATrainDataResponse<SaveATrainBookingsConfirmRSDTO> doCallBookingsConfirm(SaveATrainBookingsConfirmRQDTO confirmRQ) {
        if(log.isDebugEnabled()) {
            log.debug("start::doCallBookingsConfirm::confirmRS: \n {}", JsonUtil.convertToPrettyJson(confirmRQ));
        }
        String bookingsConfirmRsStr = httpService.doCallPost(BOOKINGS_CONFIRM_BASEBATH, confirmRQ);
        if(log.isDebugEnabled()) {
            log.debug("doCallBookingsConfirm::bookingsConfirmRsStr: {}", bookingsConfirmRsStr);
        }
        return parseSaveATrain(bookingsConfirmRsStr, SaveATrainBookingsConfirmRSDTO.class);
    }

    private SaveATrainBookingsConfirmRQDTO makeSaveATrainBookingsConfirmRSDTO(SaveATrainStartBookRQDTO bookRQ) {
        SaveATrainBookingsConfirmRQDTO.Booking booking = SaveATrainBookingsConfirmRQDTO.Booking.builder()
                .searchIdentifier(bookRQ.getSearchIdentifier())
                .build();
        return SaveATrainBookingsConfirmRQDTO.builder()
                .booking(booking)
                .build();
    }
    /*-----------------------*/

    private SaveATrainDataResponse<SaveATrainGetTicketRSDTO> doCallGetTicket(SaveATrainGetTicketRQDTO getTicketRQ) {
        if(log.isDebugEnabled()) {
            log.debug("start::doCallGetTicket::getTicketRQ: \n {}", JsonUtil.convertToPrettyJson(getTicketRQ));
        }
        String getTicketUri = BOOKINGS_TICKET_BASEBATH.replace(SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG, getTicketRQ.getIdentifier());
        String getTicketRsStr = httpService.doCallGet(getTicketUri, null);
//        if(log.isDebugEnabled()) {
//            log.debug("doCallGetTicket::getTicketRsStr: {}", getTicketRsStr);
//        }
        return parseSaveATrain(getTicketRsStr, SaveATrainGetTicketRSDTO.class);
    }

    private SaveATrainGetTicketRQDTO makeSaveATrainGetTicketRQ(SaveATrainStartBookRQDTO bookRQ) {
        return new SaveATrainGetTicketRQDTO(bookRQ.getSearchIdentifier());
    }

    /*----------------------*/

    private SaveATrainDataResponse<SaveATrainTariffConditionRSDTO> doCallTariffCondition(SaveATrainTariffConditionRQDTO tariffConditionRQ) {
        if(log.isDebugEnabled()) {
            log.debug("start::doCallTariffCondition::tariffConditionRQ: \n {}", JsonUtil.convertToPrettyJson(tariffConditionRQ));
        }
        String tariffConditionUri = TARIFF_CONDITION_BASEBATH.replace(SAVE_A_TRAIN_SEARCH_IDENTIFIER_REG, tariffConditionRQ.getIdentifier())
                                                            .replace(SAVE_A_TRAIN_ROUTE_ID_REG, String.valueOf(tariffConditionRQ.getResultId()))
                                                            .replace(SAVE_A_TRAIN_FARE_ID_REG, String.valueOf(tariffConditionRQ.getFareId()));
        if(log.isDebugEnabled()) {
            log.debug("start::doCallTariffCondition::tariffConditionUri: {}", tariffConditionUri);
        }
        String tariffConditionRsStr = httpService.doCallGet(tariffConditionUri, null);
        if(log.isDebugEnabled()) {
            log.debug("start::doCallTariffCondition::tariffConditionRsStr: {}", tariffConditionRsStr);
        }
        SaveATrainDataResponse<SaveATrainTariffConditionRSDTO> response = parseSaveATrain(tariffConditionRsStr, SaveATrainTariffConditionRSDTO.class);
        if(log.isDebugEnabled()) {
            log.debug("start::doCallTariffCondition::response: \n{}", JsonUtil.convertToPrettyJson(response));
        }
        return response;
    }

    private SaveATrainTariffConditionRQDTO makeSaveATrainTariffConditionRQ(SaveATrainStartBookRQDTO bookRQ) {
        return SaveATrainTariffConditionRQDTO.builder()
                .identifier(bookRQ.getSearchIdentifier())
                .resultId(bookRQ.getSearchResultId())
                .fareId(bookRQ.getTransfersAttributes().get(0).getFareId())
                .build();
    }

    /*----Edd book------*/

    /*-----Cancel---------*/
    private SaveATrainDataResponse<SaveATrainCancelRSDTO> doCallCancel(SaveATrainCancelRQDTO cancelRQ) {
        if(log.isDebugEnabled()) {
            log.debug("doCallCancel::cancelRQ: \n{}", JsonUtil.convertToPrettyJson(cancelRQ));
        }
        //TODO
        //send email to agents@saveatrain.com
        // subject: AGENT _____ Ticket Cancelation XXXXX (XXXXX is confirmation code)
        return SaveATrainDataResponse.success(new SaveATrainCancelRSDTO());
    }

    /*--------------------*/

    //Auto refresh token
    private <T> T executeHttp(SaveATrainHttpExecution execution) throws Exception {
        int callTurnNumber = 0;
        if(log.isDebugEnabled()) {
            log.debug("executeHttp::execution: \n {}", execution);
        }
        SaveATrainDataResponse<T> saveATranRs;
        do {
            saveATranRs = execution.execute();
            if(saveATranRs.getCode() == SaveATrainDataResponse.Code.SUCCESS) {
                return saveATranRs.getData();
            }
            if(saveATranRs.getCode() == SaveATrainDataResponse.Code.TOKE_EXPIRED) {
                refreshHttpService(site);
            }
            callTurnNumber++;
        } while (callTurnNumber < NUMBER_RECALL + 1);
        throw new Exception(saveATranRs.getErrors());
    }

    private <T extends AbstractSaveATrainRSDTO> SaveATrainDataResponse<T> parseSaveATrain(String responseStr, Class<T> responseClass) {
        if(responseStr == null || "".equals(responseStr)) {
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.CALL_FAIL, "response is empty");
        }
        T response = JsonUtil.parse(responseStr, responseClass);
//        if(log.isDebugEnabled()) {
//            log.debug("parseSaveATrain::response: \n {}", JsonUtil.convertToPrettyJson(response));
//        }
        if(response.isSuccess()) {
            return SaveATrainDataResponse.success(response);
        }
        if(SEARCH_TRAIN_INVALID_ACCESS_TOKEN.equals(response.getErrors())) {
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.TOKE_EXPIRED, SEARCH_TRAIN_INVALID_ACCESS_TOKEN);
        }
        if(response.getErrors() != null) {
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.UNKNOWN, response.getErrors());
        }
        return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.UNKNOWN, responseStr);
    }

    private static final BigDecimal NUMBER_2 = new BigDecimal(2);
    private static final int NUMBER_RECALL = 1;
    private static final String SEARCH_TRAIN_INVALID_ACCESS_TOKEN = "Invalid access token";
}
