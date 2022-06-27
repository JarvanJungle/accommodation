package com.torkirion.eroam.microservice.transport.repository;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.transport.datadomain.SaveATrainVendorStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SaveATrainVendorStationRepository extends JpaRepository<SaveATrainVendorStation, String> {
    @Query("select satvs from SaveATrainVendorStation satvs " +
            "where satvs.geoCoordinates.latitude is not null " +
            "and satvs.geoCoordinates.longitude is not null " +
//            "and satvs.searchable = true " +
//            "and satvs.recommendedSearch = true " +
            "and satvs.geoCoordinates.latitude <= ?1 and satvs.geoCoordinates.longitude >= ?2 " +
            "and satvs.geoCoordinates.latitude >= ?3 and satvs.geoCoordinates.longitude <= ?4 ")
    List<SaveATrainVendorStation> findAllInnerNorthwestAndSoutheast(BigDecimal northwestLatitude, BigDecimal northwestLongitude,
                                                                    BigDecimal southeastLatitude, BigDecimal southeastLongitude);

    List<SaveATrainVendorStation> findDistinctByUidIn(List<String> uids);
}
