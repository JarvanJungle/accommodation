package com.torkirion.eroam.microservice.cruise.endpoint.traveltek;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data.SidData;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.traveltek.schemas.messages.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;

@Slf4j
public class TravelTekInterface {
    public static final String API_VERSION = "1.0";

    private final SystemPropertiesDAO properties;

    private TravelTekProperties travelTekProperties;

    private SidData sidData;

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public TravelTekProperties getTravelTekProperties() {
        return travelTekProperties;
    }

    public TravelTekInterface(SystemPropertiesDAO properties, SidData sidData, String site, String channel) throws Exception {
        this.properties = properties;
        this.sidData = sidData;
        init(site, channel);
    }

    private void init(String site, String channel) throws JAXBException {
        if (log.isDebugEnabled())
            log.debug("init::entering with site " + site + ", channel " + channel + ", properties=" + properties);

        travelTekProperties = new TravelTekProperties(properties, site, channel);
        var jaxbContext = JAXBContext.newInstance("com.traveltek.schemas.messages");
        marshaller = jaxbContext.createMarshaller();
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    public List<Region> getCruiseRegions() throws Exception {
        var method = new Method();
        method.setAction("getcruiseregions");
        var response = handleCallApi(method);
        return response.getResults().getRegion();
    }

    public List<Line> getCruiseLines() throws Exception {
        var method = new Method();
        method.setAction("getcruiselines");
        var response = handleCallApi(method);
        return response.getResults().getLine();
    }

    public Ship getShipContent(BigInteger shipId) throws Exception {
        var method = new Method();
        method.setAction("getshipcontent");
        method.setShipid(shipId);
        var response = handleCallApi(method);
        return response.getResults().getShip();
    }

    public Result getResults(Method method) throws Exception {
        method.setAction("getresults");
        var response = handleCallApi(method);
        return response.getResults();
    }

    public Cruise getCruiseContent(BigInteger codeToCruiseId) throws Exception {
        var method = new Method();
        method.setAction("getcruisecontent");
        method.setCodetocruiseid(codeToCruiseId);
        var response = handleCallApi(method);
        return response.getResults().getCruise().stream().findFirst().orElse(null);
    }

    public Port getPortInfo(BigInteger portId) throws Exception {
        var method = new Method();
        method.setAction("getportinfo");
        method.setPortid(portId);
        var response = handleCallApiHideError(method);
        if (response == null || response.getResults() == null)
            return null;
        return response.getResults().getPortinfo();
    }

    public Search performSearch(Method method) throws Exception {
        method.setAction("performsearch");
        var response = handleCallApi(method);
        return response.getResults().getSearch();
    }

    public Search createSearch(Method method) throws Exception {
        method.setAction("createsearch");
        var response = handleCallApi(method);
        return response.getResults().getSearch();
    }

    public String createSession() throws Exception {
        var method = new Method();
        method.setAction("createsession");
        var response = handleCallApi(method);
        return response.getSessionkey();
    }

    private Response handleCallApi(Method method) throws Exception {
        HttpService httpService = new TravelTekHttpService(travelTekProperties);
        var responseString = httpService.doCallPost(null, createStringRequest(method));
        var result = unMarshal(fixResponseNamespaces(responseString));
        if (!result.getErrors().getError().isEmpty()){
            throw new Exception(result.getErrors().getError().get(0).getText());
        }
        return result;
    }
    private Response handleCallApiHideError(Method method) throws Exception {
        HttpService httpService = new TravelTekHttpService(travelTekProperties);
        var responseString = httpService.doCallPost(null, createStringRequest(method));
        var result = unMarshal(fixResponseNamespaces(responseString));
        if (!result.getErrors().getError().isEmpty()){
            return null;
        }
        return result;
    }

    private String createStringRequest(Method method) throws JAXBException {
        var request = new Request();
        var auth = new Auth();
        auth.setUsername(sidData.getUsername());
        auth.setPassword(sidData.getPassword());
        method.setSitename(sidData.getSite());
        method.setCurrency(sidData.getCurrency());
        request.setAuth(auth);
        request.setMethod(method);
        var sw = new StringWriter();
        marshaller.marshal(request, sw);
        return fixRequestNamespaces(sw.toString());
    }

    private Response unMarshal(String response) throws JAXBException {
        if (unmarshaller == null) {
            var jaxbContext = JAXBContext.newInstance("com.hotelbeds.schemas.messages");
            unmarshaller = jaxbContext.createUnmarshaller();
        }
        var bin = new ByteArrayInputStream(response.getBytes());
        return (Response) unmarshaller.unmarshal(bin);
    }

    private String fixRequestNamespaces(String s) {
        if (s == null)
            return "";
        s = s.replace("<ns2:request xmlns:ns2=\"http://www.traveltek.com/schemas/messages\">", "<request>");
        s = s.replace("</ns2:request>", "</request>");
        return s;
    }

    private String fixResponseNamespaces(String s) {
        if (s == null)
            return "";
        s = s.replace("<response", "<ns2:response xmlns:ns2=\"http://www.traveltek.com/schemas/messages\"");
        s = s.replace("</response>", "</ns2:response>");
        return s;
    }
}
