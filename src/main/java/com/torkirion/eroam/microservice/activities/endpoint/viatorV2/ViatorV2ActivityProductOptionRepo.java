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
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;

import com.torkirion.eroam.ims.datadomain.Activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface ViatorV2ActivityProductOptionRepo extends JpaRepository<ViatorV2ActivityProductOption, Long>
{
	List<ViatorV2ActivityProductOption> findAllByProductCodeAndProductOptionCode(String productCode, String productOptionCode);
}