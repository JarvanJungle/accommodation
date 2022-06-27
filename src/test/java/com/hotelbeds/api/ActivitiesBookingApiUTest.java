package com.hotelbeds.api;

import com.torkirion.eroam.microservice.util.HotelBedsUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.hotelbeds.activities.api.ActivitiesApi;
import com.hotelbeds.activities.api.ApiException;
import com.hotelbeds.activities.model.AvailabilitybyhotelcodeRequest;
import com.hotelbeds.activities.model.SearchFilterItem;
import com.hotelbeds.activities.model.Pagination;
import com.hotelbeds.activities.model.AvailabilityByHotelResponse;

import com.hotelbeds.activities.model.Filter;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HotelBedsApiTestConfiguration.class,
})
public class ActivitiesBookingApiUTest {

    @Autowired
    com.hotelbeds.activities.api.ApiClient apiClient;

    @Before
    public void init() {
        apiClient.setBasePath("/activity-api/3.0");
    }

    @Test
    public void test_activities_available_by_price() throws NoSuchAlgorithmException, ApiException {
        ActivitiesApi activitiesApi = new ActivitiesApi(apiClient);
        AvailabilitybyhotelcodeRequest request = new AvailabilitybyhotelcodeRequest();
        SearchFilterItem type = new SearchFilterItem();
        type.setType("destination");
        type.setValue("MCO");

        SearchFilterItem priceFrom = new SearchFilterItem();
        priceFrom.setType("priceFrom");
        priceFrom.setValue("50");

        SearchFilterItem priceTo = new SearchFilterItem();
        priceTo.setType("priceTo");
        priceTo.setValue("60");

        List<SearchFilterItem> searchFilterItems = List.of(type, priceFrom, priceTo);

        Filter filter = new Filter();
        filter.setSearchFilterItems(searchFilterItems);

        List<Filter> filters = List.of(filter);
        request.setFilters(filters);

        /*----------*/
        request.setFrom("2021-12-12");
        request.setTo("2021-12-22");
        request.setLanguage("en");

        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setItemsPerPage(2);
        request.setPagination(pagination);

        Object response = activitiesApi.availabilitybyhotelcode(
                HotelBedsApiTestConfiguration.APIKEY,
                HotelBedsUtil.getXSignature(HotelBedsApiTestConfiguration.APIKEY, HotelBedsApiTestConfiguration.SECRET),
                HotelBedsApiTestConfiguration.ACCEPT,
                HotelBedsApiTestConfiguration.ACCEPTENCODING,
                request);
        //System.out.println(response);
    }

    @Test
    public void test_activities_available_by_gps() throws NoSuchAlgorithmException, ApiException {
        ActivitiesApi activitiesApi = new ActivitiesApi(apiClient);
        AvailabilitybyhotelcodeRequest request = new AvailabilitybyhotelcodeRequest();
        SearchFilterItem searchFilterItem = new SearchFilterItem();
        searchFilterItem.setType("gps");
        searchFilterItem.setLatitude("41.49004");
        searchFilterItem.setLongitude("2.08161");

        List<SearchFilterItem> searchFilterItems = List.of(searchFilterItem);

        Filter filter = new Filter();
        filter.setSearchFilterItems(searchFilterItems);

        List<Filter> filters = List.of(filter);
        request.setFilters(filters);

        /*----------*/
        request.setFrom("2021-07-29");
        request.setTo("2021-09-05");
        request.setLanguage("en");

        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setItemsPerPage(2);
        request.setPagination(pagination);
        request.setOrder("DEFAULT");
        Object response = activitiesApi.availabilitybyhotelcode(
                HotelBedsApiTestConfiguration.APIKEY,
                HotelBedsUtil.getXSignature(HotelBedsApiTestConfiguration.APIKEY, HotelBedsApiTestConfiguration.SECRET),
                HotelBedsApiTestConfiguration.ACCEPT,
                HotelBedsApiTestConfiguration.ACCEPTENCODING,
                request);
        //System.out.println(response);
    }


    @Test
    public void test_activities_available_fail_missing_from() throws NoSuchAlgorithmException, ApiException {
        ActivitiesApi activitiesApi = new ActivitiesApi(apiClient);
        AvailabilitybyhotelcodeRequest request = new AvailabilitybyhotelcodeRequest();
        SearchFilterItem type = new SearchFilterItem();
        type.setType("destination");
        type.setValue("MCO");

        SearchFilterItem priceFrom = new SearchFilterItem();
        priceFrom.setType("priceFrom");
        priceFrom.setValue("50");

        SearchFilterItem priceTo = new SearchFilterItem();
        priceTo.setType("priceTo");
        priceTo.setValue("60");

        List<SearchFilterItem> searchFilterItems = List.of(type, priceFrom, priceTo);

        Filter filter = new Filter();
        filter.setSearchFilterItems(searchFilterItems);

        List<Filter> filters = List.of(filter);
        request.setFilters(filters);

        /*----------*/
        request.setFrom("2015-12-12");
        request.setTo("2015-12-22");
        request.setLanguage("en");

        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setItemsPerPage(2);
        request.setPagination(pagination);

        Object response = activitiesApi.availabilitybyhotelcode(
                HotelBedsApiTestConfiguration.APIKEY,
                HotelBedsUtil.getXSignature(HotelBedsApiTestConfiguration.APIKEY, HotelBedsApiTestConfiguration.SECRET),
                HotelBedsApiTestConfiguration.ACCEPT,
                HotelBedsApiTestConfiguration.ACCEPTENCODING,
                request);
        //System.out.println(response);
    }
}
