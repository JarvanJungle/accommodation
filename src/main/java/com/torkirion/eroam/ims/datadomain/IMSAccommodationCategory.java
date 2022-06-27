package com.torkirion.eroam.ims.datadomain;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "imsaccommodationcategory")
@Data
public class IMSAccommodationCategory
{
	@Id
	@Column(length = 50)
	private String category;
}
