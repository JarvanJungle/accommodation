package com.torkirion.eroam.microservice.activities.apidomain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ActivityKey
{
	private String activityId;

	private String optionId;

	private String date; // yyyy-MM-dd

	@Override
	public int hashCode()
	{
		String str = activityId + optionId + date;
		return str.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityKey that = (ActivityKey) obj;
		return activityId.equals(that.getActivityId()) && optionId.equals(that.getOptionId()) && date.equals(that.getDate());
	}
}
