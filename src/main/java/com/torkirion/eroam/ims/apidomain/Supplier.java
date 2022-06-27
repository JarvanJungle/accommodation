package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



import lombok.Data;

@Data
public class Supplier extends SupplierSummary
{
	public static enum SupplierContactType
	{
		RESERVATIONS, CONTRACTING, CUSTOMER_SERVICE, GM, ACCOUNTS;
	}

	@Data
	public static class SupplierContact
	{
		private SupplierContactType contactType;

		private String name;

		private String email;

		private String phone;
	}

	private Boolean forAccommodation;

	private Boolean forActivities;

	private Boolean forEvents;

	private Boolean forMerchandise;

	private Boolean forTransportation;

	private BigDecimal defaultMargin;

	private Boolean showSupplierName;

	private List<SupplierContact> contacts = new ArrayList<>();
}
