package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.endpoint.SaveATrainDataResponse;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainAPIProperties;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SaveATrainSearchApiInterface {
    public static final String API_VERSION = "1.41";
    //users/sign_in?email={{search_email}}&password={{search_password}}
    private static final String USER_SIGN_IN_BASEBATH = "users/sign_in";
    private static final String SEARCH_BASEBATH = "search";

    private SystemPropertiesDAO properties;

    private UserSignInRQDTO userSignInRQ;

    private HttpService httpService;

    private SaveATrainAPIProperties saveATrainAPIProperties;

    private String site;
    private String channel;

    public SaveATrainSearchApiInterface(SystemPropertiesDAO properties, String site, String channel) {
        this.properties = properties;
        init(site, channel);
    }

    private void init(String site, String channel)
    {
        if (log.isDebugEnabled())
            log.debug("init::entering with site " + site + ", channel " + channel + ", properties=" + properties);
        this.site = site;
        this.channel = channel;
        saveATrainAPIProperties = new SaveATrainAPIProperties(properties, site);
        userSignInRQ = UserSignInRQDTO.builder()
                .email(saveATrainAPIProperties.searchApiEmail)
                .password(saveATrainAPIProperties.searchApiPassword)
                .build();
        httpService = new SaveATrainSearchApiHttpService(saveATrainAPIProperties);
    }

    public SearchApiSearchRSDTO startSearchInSearchApi(SearchApiStartSearchRQDTO searchByCityRQ) throws Exception{
        if(log.isDebugEnabled()) {
            log.debug("startSearchByCities Start :: searchByCityRQ: \n {}", JsonUtil.convertToPrettyJson(searchByCityRQ));
        }
        SearchApiSearchRQDTO searchRQ = SearchApiSearchRQDTO.builder()
                .origin(searchByCityRQ.getOrigin())
                .destination(searchByCityRQ.getDestination())
                .departureDate(searchByCityRQ.getDepartureDate())
                .returnDate(null)
                .tripType(searchByCityRQ.getTripType())
                .passengers(searchByCityRQ.getPassengers())
                .build();
        SaveATrainDataResponse<SearchApiSearchRSDTO> searchApiSearchRS = doCallSearch(searchRQ);
        if(searchApiSearchRS.getCode() == SaveATrainDataResponse.Code.SUCCESS) {
            return searchApiSearchRS.getData();
        }
        return null;
    }

    private void refreshToken(String site) throws Exception {
        UserSignInRQDTO userSignInRQ = UserSignInRQDTO.builder()
                .email(saveATrainAPIProperties.searchApiEmail)
                .password(saveATrainAPIProperties.searchApiPassword)
                .build();
        UserSignInRSDTO userSignInRSDTO = doCallUserSignIn(userSignInRQ);
        String token = userSignInRSDTO.getUser().getToken();
        saveATrainAPIProperties.saveSearchApiToken(properties, site, token);
    }

    private UserSignInRSDTO doCallUserSignIn(UserSignInRQDTO userSignInRQ) throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("doCallUserSignIn start login (Search Api)");
        }
        String requestUrl = USER_SIGN_IN_BASEBATH + "?email=" + userSignInRQ.getEmail() + "&password=" + userSignInRQ.getPassword();
        String userSignInStr = httpService.doCallPost(requestUrl, null);
        if(userSignInStr == null || StringUtils.isBlank(userSignInStr)) {
            throw new Exception("Login to save a train (Search api) fail");
        }
        UserSignInRSDTO userSignInRS = JsonUtil.parse(userSignInStr, UserSignInRSDTO.class);
        if(userSignInRS == null || userSignInRS.getUser() == null || StringUtils.isBlank(userSignInRS.getUser().getToken())) {
            throw new Exception("Login to save a train (Search api) fail");
        }
        return userSignInRS;
    }

    //https://apisearch.saveatrain.com/search/Paris/London?triptype=2&passengers=1&ddate=2021-10-28&rdate=2021-10-30&email={{search_email}}&password={{search_password}}
    private SaveATrainDataResponse<SearchApiSearchRSDTO> doCallSearch(SearchApiSearchRQDTO searchRQ) {
        String requestUrl = SEARCH_BASEBATH + "/" + searchRQ.getOrigin() + "/" + searchRQ.getDestination();
        Map<String, String> params = new HashMap<>();
        params.put("triptype", String.valueOf(searchRQ.getTripType()));
        params.put("ddate", searchRQ.getDepartureDate());
        if(TRIP_TYPE_ROUND_TRIP == searchRQ.getTripType() && searchRQ.getDepartureDate() != null) {
            params.put("rdate", searchRQ.getReturnDate());
        }
        params.put("passengers", "1");
        params.put("email", saveATrainAPIProperties.searchApiEmail);
        params.put("password", saveATrainAPIProperties.searchApiPassword);
        String searchRSStr = httpService.doCallGet(requestUrl, params);
        if(log.isDebugEnabled()) {
            log.debug("doCallSearch::searchRSStr: {}", searchRSStr);
        }
        if(searchRSStr == null || "".equals(searchRSStr)) {
            log.error("doCallSearch::searchRSStr empty");
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.CALL_FAIL, "Search Error");
        }
        SearchApiSearchRSDTO searchRS = JsonUtil.parse(searchRSStr, SearchApiSearchRSDTO.class);
        if(searchRS == null) {
            log.error("doCallSearch::searchRSStr cast fail");
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.UNKNOWN, "Search Error");
        }
        if(searchRS.getErrors() != null && !"".equals(searchRS.getErrors())) {
            log.error("doCallSearch::errors: {}", searchRS.getErrors());
            return SaveATrainDataResponse.fail(SaveATrainDataResponse.Code.TOKE_EXPIRED, searchRS.getErrors());
        }
//        if(log.isDebugEnabled()) {
//            log.debug("doCallSearch::searchRS: \n {}", JsonUtil.convertToPrettyJson(searchRS));
//        }
        return SaveATrainDataResponse.success(searchRS);
    }

    public static int TRIP_TYPE_ONE_WAT = 1;
    private static int TRIP_TYPE_ROUND_TRIP = 2;
    private static final int NUMBER_RECALL = 1;
}
