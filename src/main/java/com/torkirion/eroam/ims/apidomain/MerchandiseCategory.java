package com.torkirion.eroam.ims.apidomain;

import java.util.Comparator;
import java.util.List;

import lombok.Data;

@Data
public class MerchandiseCategory
{
	public static class MerchandiseCategorySorterByName implements Comparator<MerchandiseCategory>
	{
		@Override
		public int compare(MerchandiseCategory o1, MerchandiseCategory o2)
		{
			return o1.name.compareTo(o2.name);
		}
	}
	private String name;
}
