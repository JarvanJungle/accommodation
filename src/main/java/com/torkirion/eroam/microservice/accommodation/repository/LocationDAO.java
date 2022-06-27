package com.torkirion.eroam.microservice.accommodation.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.datadomain.Location;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.datadomain.Country;
import com.torkirion.eroam.microservice.repository.CountryDAO;

import lombok.extern.slf4j.Slf4j;

//@Repository
@Service
@Slf4j
public class LocationDAO
{
	private DataSource dataSource;

	@Autowired
	private CountryDAO countryDAO;

	@Autowired
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public Location read(String findLocationCode)
	{
		log.debug("read::entered for " + findLocationCode);

		String sql = "select * from location where locationid = ?";
		java.sql.Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		try
		{
			conn = dataSource.getConnection();
			stmnt = conn.prepareStatement(sql);
			stmnt.setString(1, findLocationCode);
			rs = stmnt.executeQuery();
			while ( rs.next() )
			{
				String locationid = rs.getString("locationid");
				String locationName = rs.getString("locationName");
				String searchName = rs.getString("searchName");
				String countryID = rs.getString("country");
				Country country = countryDAO.getCountry(countryID);
				LatitudeLongitude northWest = new LatitudeLongitude();
				//northWest.setLatitude(rs.getDouble("northwestLatitude"));
				//northWest.setLongitude(rs.getDouble("northwestLongitude"));
				LatitudeLongitude southEast = new LatitudeLongitude();
				//southEast.setLatitude(rs.getDouble("southEastLatitude"));
				//southEast.setLongitude(rs.getDouble("southEastLongitude"));
				
				Location location = new Location();
				location.setLocationid(locationid);
				location.setLocationName(locationName);
				location.setSearchName(searchName);
				location.setCountry(country);
				location.setNorthWest(northWest);
				location.setSouthEast(southEast);
				
				return location;
			}
		}
		catch (Exception e)
		{
			log.error("read::exception " + e.toString(), e);
		}
		finally
		{
			try
			{
				rs.close();
				stmnt.close();
				conn.close();
			}
			catch (Exception e)
			{
				log.error("read::exception " + e.toString(), e);
			}
		}
		return null;
	}
	
	public Set<String> getHotelIdsForInternalDestinationCode(String internalDestinationCode)
	{
		log.debug("getHotelIdsForInternalDestinationCode::entered for " + internalDestinationCode);

		String sql = "select code from rcaccommodation where INTERNAL_DESTINATION_CODE = ?";
		java.sql.Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		Set<String> hotelIDs = new HashSet<>();
		try
		{
			conn = dataSource.getConnection();
			stmnt = conn.prepareStatement(sql);
			stmnt.setString(1, internalDestinationCode);
			rs = stmnt.executeQuery();
			while ( rs.next() )
			{
				String code = rs.getString("code");
				
				hotelIDs.add(code);
			}
		}
		catch (Exception e)
		{
			log.error("getHotelIdsForInternalDestinationCode::exception " + e.toString(), e);
		}
		finally
		{
			try
			{
				rs.close();
				stmnt.close();
				conn.close();
			}
			catch (Exception e)
			{
				log.error("getHotelIdsForInternalDestinationCode::exception " + e.toString(), e);
			}
		}
		return hotelIDs;
	}
}
