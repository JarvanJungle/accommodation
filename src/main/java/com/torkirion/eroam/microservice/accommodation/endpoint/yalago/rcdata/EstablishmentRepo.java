package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EstablishmentRepo extends PagingAndSortingRepository<EstablishmentData, Integer>
{
    Slice<EstablishmentData> findByLastUpdateLessThan(LocalDate date, Pageable pageAble);
    Slice<EstablishmentData> findByEstablishmentId(Integer establishmentId, Pageable pageAble);
}