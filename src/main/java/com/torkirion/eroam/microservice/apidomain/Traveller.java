package com.torkirion.eroam.microservice.apidomain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Traveller implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public int getAge(Date atDate) throws Exception
	{
		if (getBirthDate() == null)
			throw new Exception("Birthdate not set");
		Calendar cAtDate = Calendar.getInstance();
		Calendar cBirthDate = Calendar.getInstance();
		cAtDate.setTime(atDate);
		cBirthDate.setTime(getBirthDate());
		// getbasic year diff
		int yearDiff = cAtDate.get(Calendar.YEAR) - cBirthDate.get(Calendar.YEAR);
		// now check if the birthmonth/day is yet to come for this year...
		cBirthDate.set(Calendar.YEAR, cAtDate.get(Calendar.YEAR));
		if (cBirthDate.after(cAtDate))
			yearDiff = yearDiff - 1;
		return yearDiff;
	}

	@JsonIgnore
	public int getAge(LocalDate atDate) 
	{
		if (getBirthDate() == null)
			return 30;
		return Period.between(new java.sql.Date(getBirthDate().getTime()).toLocalDate(), atDate).getYears();
	}

	@JsonIgnore
	public void setAge(Integer age) throws Exception
	{
		Calendar cAtDate = Calendar.getInstance();
		cAtDate.add(Calendar.YEAR, age * -1);
		cAtDate.add(Calendar.DATE, -1);
		setBirthDate(cAtDate.getTime());
	}

	private String title;

	private String givenName;

	private String surname;

	@ApiModelProperty(notes = "The telephone numnber of the person.  Currently only used for transfers.", example = "0414555666", required = false)
	String telephone;

	@ApiModelProperty(notes = "The email of the traveller. Currently only used for transfers.", example = "fred@gmail.com", required = false)
	String email;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date birthDate;
}
