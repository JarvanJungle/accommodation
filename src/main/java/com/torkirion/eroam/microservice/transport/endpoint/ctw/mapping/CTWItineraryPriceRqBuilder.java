package com.torkirion.eroam.microservice.transport.endpoint.ctw.mapping;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.DocumentedTraveller;
import com.torkirion.eroam.microservice.apidomain.TravellerSummary;
import com.torkirion.eroam.microservice.transport.apidomain.LegRQ;
import com.torkirion.eroam.microservice.transport.apidomain.SegmentRQ;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.CTWCommon;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.CTWItinenaryPriceRQ;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


public class CTWItineraryPriceRqBuilder {

    private CTWItinenaryPriceRQ request;

    public CTWItineraryPriceRqBuilder builder() {
        request = new CTWItinenaryPriceRQ();
        return this;
    }

    public CTWItineraryPriceRqBuilder segments(Set<SegmentRQ> segments) {
        int segmentId = 0;
        for(SegmentRQ segmentRQ : segments) {
            for(LegRQ legRQ : segmentRQ.getLegs()) {
                request.getFlights().add(convertLegtoFlight(legRQ, segmentId));
                request.getPreferences().getCabin().getCabins().add(new CTWItinenaryPriceRQ.CabinDetail(legRQ.getTransportClass().name()));
            }
            request.getSegments().add(new CTWItinenaryPriceRQ.Segment(segmentId, "CHEAPEST_SAME_CABIN"));//check later
            segmentId++;
        }

        return this;
    }

    public CTWItineraryPriceRqBuilder passengers(List<TravellerSummary> travellers, CurrencyValue rate) throws Exception {
        for(TravellerSummary traveller : travellers) {
            request.getPassengers().add(convertTravellerToPassenger(traveller, rate));
        }
        return this;
    }

    public CTWItineraryPriceRqBuilder passengersByDoc(List<DocumentedTraveller> travellers, CurrencyValue rate) throws Exception {
        for(TravellerSummary traveller : travellers) {
            request.getPassengers().add(convertTravellerToPassenger(traveller, rate));
        }
        return this;
    }

    public CTWItineraryPriceRqBuilder ticketingSeller(String pos,
                                                      String channel,
                                                      String country,
                                                      String currency,
                                                      String travelAgencyCode,
                                                      String iataNumber) {
        request.getTicketingSeller().setPos(pos);
        request.getTicketingSeller().setChannel(channel);
        request.getTicketingSeller().setCountry(country);
        request.getTicketingSeller().setCurrency(currency);
        request.getTicketingSeller().setTravelAgencyCode(travelAgencyCode);
        request.getTicketingSeller().setIataNumber(iataNumber);
        return this;
    }

    public CTWItinenaryPriceRQ build() {
        return request;
    }

    private CTWCommon.Passenger convertTravellerToPassenger(TravellerSummary traveller, CurrencyValue rate) throws Exception {
        CTWCommon.Passenger passenger = new CTWCommon.Passenger();
        CTWCommon.DateOfBirth dateOfBirth = new CTWCommon.DateOfBirth();
        Calendar cAtDate = Calendar.getInstance();
        cAtDate.setTime(traveller.getBirthDate());
        dateOfBirth.setDay(cAtDate.get(Calendar.DATE));
        dateOfBirth.setMonth((cAtDate.get(Calendar.MONTH) + 1) % 12);
        dateOfBirth.setYear(cAtDate.get(Calendar.YEAR));
        passenger.setDateOfBirth(dateOfBirth);

        passenger.setNationality(traveller.getNationality());
        passenger.setResidency(traveller.getResidency());
        passenger.setCurrencyOfPayment(rate.getCurrencyId());

        int age = traveller.getAge(Calendar.getInstance().getTime());
        if(age > 17) {
            passenger.getPtcs().add("ADT");
        } else if(age > 1) {
            passenger.getPtcs().add("CHD");
        } else {
            passenger.getPtcs().add("INF");
        }

        if(traveller instanceof  DocumentedTraveller) {
            DocumentedTraveller dTraveller = (DocumentedTraveller) traveller;
            passenger.getFormOfPayment().getBillingAddress().setEmailAddress(dTraveller.getEmail());
            passenger.getFormOfPayment().getBillingAddress().setPhoneNumber(dTraveller.getTelephone());
            passenger.setFirstName(dTraveller.getGivenName());
            passenger.setLastName(dTraveller.getSurname());

            passenger.setTitle(dTraveller.getTitle());
        }

        return passenger;
    }

    private CTWItinenaryPriceRQ.Flight convertLegtoFlight(LegRQ legRQ, int segmentId) {
        CTWItinenaryPriceRQ.Flight flight = new CTWItinenaryPriceRQ.Flight();
        flight.setDepartureAirport(legRQ.getDepartureIata());
        flight.setArrivalAirport(legRQ.getArrivalIata());
        flight.setMarketingCarrier(legRQ.getAirlineCode());
        flight.setMarketingFlightNumber(legRQ.getFlightNumber());
        flight.setDepartureDateTime(convertEroamDateTimeToCTW(legRQ.getTravelDateTime()));
        flight.setRequestSegment(segmentId);
        return flight;
    }

    private CTWItinenaryPriceRQ.DateTime convertEroamDateTimeToCTW(LocalDateTime ldt) {
        CTWItinenaryPriceRQ.Date date = new CTWItinenaryPriceRQ.Date();
        date.setDay(ldt.getDayOfMonth());
        date.setMonth(ldt.getMonthValue());
        date.setYear(ldt.getYear());
        CTWItinenaryPriceRQ.Time t = new CTWItinenaryPriceRQ.Time();
        t.setHour(ldt.getHour());
        t.setMinutes(ldt.getMinute());
        return new CTWItinenaryPriceRQ.DateTime(date, t);
    }
}
