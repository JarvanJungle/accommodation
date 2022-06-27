package com.torkirion.eroam.microservice.transfers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.config.ApplicationConfig;
import com.torkirion.eroam.microservice.transfers.services.TransferNameSearcher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TransfersScheduler
{
	@Autowired
	private TransferNameSearcher transferNameSearcher;
	
	@Autowired
	ApplicationConfig applicationConfig;
	
	@Scheduled(fixedDelay = (1000 * 60 * 60 * 24 * 7), initialDelay = 1) // once per week
	public void scheduleTransferNameSearcher_prime() throws Exception
	{
		if ( applicationConfig.getProduct().isAll() || applicationConfig.getProduct().isTransfers())
			transferNameSearcher.prime();
	}
}
