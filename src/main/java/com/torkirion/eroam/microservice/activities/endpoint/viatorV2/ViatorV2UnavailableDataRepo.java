package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;

import lombok.Data;
import lombok.ToString;

public interface ViatorV2UnavailableDataRepo extends JpaRepository<ViatorV2UnavailableData, Long>
{
	List<ViatorV2UnavailableData> findByProductCode(String productCode);
	void deleteByProductCode(String productCode);
}