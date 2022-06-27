package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface YoutravelLoadRepo extends JpaRepository<YoutravelLoadData, Integer>
{
}