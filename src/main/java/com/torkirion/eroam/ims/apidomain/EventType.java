package com.torkirion.eroam.ims.apidomain;

import java.util.Comparator;
import java.util.List;

import lombok.Data;

@Data
public class EventType
{
	public static class EventTypeSorterByName implements Comparator<EventType>
	{
		@Override
		public int compare(EventType o1, EventType o2)
		{
			return o1.name.compareTo(o2.name);
		}
	}
	private String name;
}
