package com.torkirion.eroam.microservice.transfers.endpoint;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRS;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO;

public interface TransferServiceIF
{
	public List<TransferResult> searchByCodes(SearchRQDTO searchRQ);

	//public RateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDT) throws Exception; // TODO

	public TransferBookRS book(String client, String subclient, TransferBookRQ bookRQ) throws Exception;

	public TransferCancelRS cancel(String client, String subclient, TransferCancelRQ cancelRQ) throws Exception; // TODO

	public abstract RetrieveTransferRS retrieve(String client, String subclient, RetrieveTransferRQ retrieveRQ) throws Exception; // TODO
	
	void initiateRCLoad(String code);

}
