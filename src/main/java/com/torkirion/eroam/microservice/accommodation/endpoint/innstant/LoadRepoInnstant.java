package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadRepoInnstant extends JpaRepository<LoadDataInnstant, Integer>
{
}