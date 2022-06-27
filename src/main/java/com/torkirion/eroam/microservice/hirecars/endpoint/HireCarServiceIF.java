package com.torkirion.eroam.microservice.hirecars.endpoint;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.activities.apidomain.*;
import com.torkirion.eroam.microservice.activities.dto.*;
import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.dto.*;

public interface HireCarServiceIF
{
	Collection<HireCarResult> search(HireCarSearchRQDTO availSearchRQ);

	HireCarDetailResult getDetail(DetailRQDTO detailRQDTO) throws Exception;
	
	HireCarBookRS book(String client, HireCarBookRQ bookRQ) throws Exception;

	HireCarCancelRS cancel(String client, HireCarCancelRQ cancelRQ) throws Exception;
	/*
	 * public CancelRS cancel(String site, CancelRQ cancelRQ) throws Exception;
	 * 
	 * public abstract RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) throws Exception;
	 */
	ActivityCancelRS HIRE_CANCEL_RS_DEFAULT = new ActivityCancelRS("", null);
}
