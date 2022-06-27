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
@Table(name = "imsmerchandisecategory")
@Data
public class MerchandiseCategory
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(length = 20)
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="merchandiseCategory")
	private Set<Merchandise> merchandise;
}
