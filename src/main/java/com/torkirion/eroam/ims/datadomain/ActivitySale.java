package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "imsactivitysale")
@Data
public class ActivitySale
{
	public static enum ItemStatus
	{
		BOOKED, CANCELLED, FAILED;
	}

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
    @JoinColumn(name="activity_id", nullable=false)
	private Activity activity;

	@Column(length = 200)
	private LocalDateTime bookingDateTime;

	// save event details
	@Column(length = 200)
	private String name;

	@Column
	private LocalDate activityDate;

	@Column(length = 3)
	private String currency;

	@Column
	private BigDecimal nettPrice;

	@Column(length = 3)
	private String rrpCurrency;

	@Column
	private BigDecimal rrpPrice;

	@Enumerated(EnumType.STRING)
	private ItemStatus itemStatus;
	
	@Column
	private Integer optionId;

	@Column(length = 200)
	private String optionName;
	
	@Column
	private Integer departureTimeId;

	@Column(length = 200)
	private String departureTimeName;
	
	@Column
	private Integer count;

	@Column
	private String ageList;

	// booker information
	@Column(length = 2)
	private String countryCodeOfOrigin;
	
	@Column(length = 20)
	private String title;

	@Column(length = 100)
	private String givenName;

	@Column(length = 100)
	private String surname;

	@Column(length = 100)
	private String telephone;
	
	@Column(length = 100)
	private String internalBookingReference;

	@Column(length = 100)
	private String internalItemReference;

	@Column(columnDefinition = "TEXT")
	private String bookingQuestionAnswers;

	@Override
	public String toString()
	{
		return "ActivitySale [id=" + id + ", bookingDateTime=" + bookingDateTime + ", name=" + name + ", activityDate=" + activityDate + ", currency=" + currency + ", nettPrice=" + nettPrice
				+ ", rrpCurrency=" + rrpCurrency + ", rrpPrice=" + rrpPrice + ", itemStatus=" + itemStatus + ", optionId=" + optionId + ", optionName=" + optionName + ", departureTimeId="
				+ departureTimeId + ", departureTimeName=" + departureTimeName + ", count=" + count + ", ageList=" + ageList + ", countryCodeOfOrigin=" + countryCodeOfOrigin + ", title=" + title
				+ ", givenName=" + givenName + ", surname=" + surname + ", telephone=" + telephone + ", internalBookingReference=" + internalBookingReference + ", internalItemReference="
				+ internalItemReference + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivitySale other = (ActivitySale) obj;
		if (activityDate == null)
		{
			if (other.activityDate != null)
				return false;
		}
		else if (!activityDate.equals(other.activityDate))
			return false;
		if (ageList == null)
		{
			if (other.ageList != null)
				return false;
		}
		else if (!ageList.equals(other.ageList))
			return false;
		if (bookingDateTime == null)
		{
			if (other.bookingDateTime != null)
				return false;
		}
		else if (!bookingDateTime.equals(other.bookingDateTime))
			return false;
		if (count == null)
		{
			if (other.count != null)
				return false;
		}
		else if (!count.equals(other.count))
			return false;
		if (countryCodeOfOrigin == null)
		{
			if (other.countryCodeOfOrigin != null)
				return false;
		}
		else if (!countryCodeOfOrigin.equals(other.countryCodeOfOrigin))
			return false;
		if (departureTimeId == null)
		{
			if (other.departureTimeId != null)
				return false;
		}
		else if (!departureTimeId.equals(other.departureTimeId))
			return false;
		if (departureTimeName == null)
		{
			if (other.departureTimeName != null)
				return false;
		}
		else if (!departureTimeName.equals(other.departureTimeName))
			return false;
		if (givenName == null)
		{
			if (other.givenName != null)
				return false;
		}
		else if (!givenName.equals(other.givenName))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (internalBookingReference == null)
		{
			if (other.internalBookingReference != null)
				return false;
		}
		else if (!internalBookingReference.equals(other.internalBookingReference))
			return false;
		if (internalItemReference == null)
		{
			if (other.internalItemReference != null)
				return false;
		}
		else if (!internalItemReference.equals(other.internalItemReference))
			return false;
		if (itemStatus != other.itemStatus)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (currency == null)
		{
			if (other.currency != null)
				return false;
		}
		else if (!currency.equals(other.currency))
			return false;
		if (nettPrice == null)
		{
			if (other.nettPrice != null)
				return false;
		}
		else if (!nettPrice.equals(other.nettPrice))
			return false;
		if (optionId == null)
		{
			if (other.optionId != null)
				return false;
		}
		else if (!optionId.equals(other.optionId))
			return false;
		if (optionName == null)
		{
			if (other.optionName != null)
				return false;
		}
		else if (!optionName.equals(other.optionName))
			return false;
		if (rrpCurrency == null)
		{
			if (other.rrpCurrency != null)
				return false;
		}
		else if (!rrpCurrency.equals(other.rrpCurrency))
			return false;
		if (rrpPrice == null)
		{
			if (other.rrpPrice != null)
				return false;
		}
		else if (!rrpPrice.equals(other.rrpPrice))
			return false;
		if (surname == null)
		{
			if (other.surname != null)
				return false;
		}
		else if (!surname.equals(other.surname))
			return false;
		if (telephone == null)
		{
			if (other.telephone != null)
				return false;
		}
		else if (!telephone.equals(other.telephone))
			return false;
		if (title == null)
		{
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityDate == null) ? 0 : activityDate.hashCode());
		result = prime * result + ((ageList == null) ? 0 : ageList.hashCode());
		result = prime * result + ((bookingDateTime == null) ? 0 : bookingDateTime.hashCode());
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((countryCodeOfOrigin == null) ? 0 : countryCodeOfOrigin.hashCode());
		result = prime * result + ((departureTimeId == null) ? 0 : departureTimeId.hashCode());
		result = prime * result + ((departureTimeName == null) ? 0 : departureTimeName.hashCode());
		result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((internalBookingReference == null) ? 0 : internalBookingReference.hashCode());
		result = prime * result + ((internalItemReference == null) ? 0 : internalItemReference.hashCode());
		result = prime * result + ((itemStatus == null) ? 0 : itemStatus.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((nettPrice == null) ? 0 : nettPrice.hashCode());
		result = prime * result + ((optionId == null) ? 0 : optionId.hashCode());
		result = prime * result + ((optionName == null) ? 0 : optionName.hashCode());
		result = prime * result + ((rrpCurrency == null) ? 0 : rrpCurrency.hashCode());
		result = prime * result + ((rrpPrice == null) ? 0 : rrpPrice.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		result = prime * result + ((telephone == null) ? 0 : telephone.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
}
