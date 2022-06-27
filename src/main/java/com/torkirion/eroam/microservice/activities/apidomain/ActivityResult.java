package com.torkirion.eroam.microservice.activities.apidomain;


import java.util.*;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ActivityResult
{
	private ActivityRC activityRC;
	private SortedSet<ActivityDeparture> departures = new TreeSet<>();
}
