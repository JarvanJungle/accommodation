package com.torkirion.eroam.ims.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.http.HttpMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.Functions;
import com.torkirion.eroam.ims.apidomain.APIError;
import com.torkirion.eroam.ims.apidomain.Activity;
import com.torkirion.eroam.ims.apidomain.ActivityAgeBand;
import com.torkirion.eroam.ims.apidomain.ActivityAllotment;
import com.torkirion.eroam.ims.apidomain.ActivityDepartureTime;
import com.torkirion.eroam.ims.apidomain.ActivityOption;
import com.torkirion.eroam.ims.apidomain.ActivitySale;
import com.torkirion.eroam.ims.apidomain.ActivitySupplier;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.apidomain.ActivityAllotment.AllotmentSummary;
import com.torkirion.eroam.ims.apidomain.ActivityOption.ActivityOptionBlock;
import com.torkirion.eroam.ims.apidomain.ActivityOption.ActivityOptionPriceBand;
import com.torkirion.eroam.ims.datadomain.ActivitySupplierAgeBand;
import com.torkirion.eroam.ims.services.*;
import com.torkirion.eroam.microservice.activities.services.ActivitySearchService;
import com.torkirion.eroam.microservice.apidomain.RequestData;

@RestController
@RequestMapping("/activityims/v1")
@Api(value = "Activity IMS API")
@Slf4j
@AllArgsConstructor
public class ActivitiesIMSController
{
	@Autowired
	private DataService dataService;

	@Autowired
	private ActivitySearchService activitySearchService;

	@Autowired
	private MapperService mapperService;

	@ApiOperation(value = "Ping Test Call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> ping(HttpServletRequest httpRequest)
	{
		log.debug("ping::enter");
		try
		{
			return new ResponseData<>(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("ping::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list activity suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<ActivitySupplier>> listSuppliers(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSuppliers::enter");
		try
		{
			List<ActivitySupplier> all = mapperService.mapActivitySuppliers(dataService.getActivitySupplierRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listSuppliers::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update activity supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<ActivitySupplier> updateSupplier(@RequestHeader("X-imsclient") String imsclient, @RequestBody ActivitySupplier activitySupplier)
	{
		log.debug("updateSupplier::enter for " + activitySupplier.getId() + " with " + activitySupplier);
		try
		{
			com.torkirion.eroam.ims.datadomain.ActivitySupplier oldData = null;
			com.torkirion.eroam.ims.datadomain.ActivitySupplier newData = mapperService.mapActivitySupplier(activitySupplier);
			log.debug("updateSupplier::after mapping, newData = " + newData);

			Set<ActivitySupplierAgeBand> toDelete = new HashSet<>();
			Set<ActivitySupplierAgeBand> toUpdate = new HashSet<>();
			Set<ActivitySupplierAgeBand> toAdd = new HashSet<>();

			List<ActivityOption> existingOptions = new ArrayList<>();
			if (activitySupplier.getId() == null)
			{
				log.debug("updateSupplier::new supplier");
				List<com.torkirion.eroam.ims.datadomain.ActivitySupplier> existingName = dataService.getActivitySupplierRepo().findByName(activitySupplier.getName());
				if (existingName != null && existingName.size() > 0)
				{
					log.error("updateSupplier::activitySupplierId " + activitySupplier.getId() + " uses duplicate name");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Duplicate Supplier Name"));
				}
				for (ActivityAgeBand apiAgeBand : activitySupplier.getAgeBands())
				{
					ActivitySupplierAgeBand b = new ActivitySupplierAgeBand();
					b.setBandName(apiAgeBand.getBandName());
					b.setMinAge(apiAgeBand.getMinAge());
					b.setMaxAge(apiAgeBand.getMaxAge());
					toAdd.add(b);
				}
			}
			else
			{
				log.debug("updateSupplier::update existing supplier");
				Optional<com.torkirion.eroam.ims.datadomain.ActivitySupplier> dOpt = dataService.getActivitySupplierRepo().findById(activitySupplier.getId());
				if (dOpt.isPresent())
				{
					oldData = dOpt.get();
					List<com.torkirion.eroam.ims.datadomain.Activity> activities = dataService.getActivityRepo().findByActivitySupplier(oldData);
					for (com.torkirion.eroam.ims.datadomain.Activity a : activities)
					{
						List<ActivityOption> options = mapperService.mapActivityOptions(dataService.getActivityOptionRepo().findByActivity(a));
						existingOptions.addAll(options);
					}
				}
				else
				{
					log.error("updateSupplier::activitySupplierId " + activitySupplier.getId() + " not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activitySupplierId " + activitySupplier.getId() + " not found"));
				}
			}

			if (oldData != null)
			{
				log.debug("updateSupplier::comapring to oldData");
				newData.setAgebands(oldData.getAgebands());
				List<ActivitySupplierAgeBand> existingAgeBandData = dataService.getActivitySupplierAgeBandRepo().findByActivitySupplier(oldData);
				for (ActivitySupplierAgeBand activitySupplierAgeBandData : existingAgeBandData)
				{
					// see if it exists in the new list
					ActivityAgeBand apiActivityAgeBand = null;
					for (ActivityAgeBand activityAgeBand : activitySupplier.getAgeBands())
					{
						if (activityAgeBand.getId() != null && activitySupplierAgeBandData.getId().intValue() == activityAgeBand.getId().intValue())
						{
							apiActivityAgeBand = activityAgeBand;
							break;
						}
					}
					if (apiActivityAgeBand != null)
					{
						// see if it has changed?
						if (!activitySupplierAgeBandData.getBandName().equals(apiActivityAgeBand.getBandName())
								|| activitySupplierAgeBandData.getMinAge().intValue() != apiActivityAgeBand.getMinAge().intValue()
								|| activitySupplierAgeBandData.getMaxAge().intValue() != apiActivityAgeBand.getMaxAge().intValue())
						{
							activitySupplierAgeBandData.setBandName(apiActivityAgeBand.getBandName());
							activitySupplierAgeBandData.setMinAge(apiActivityAgeBand.getMinAge());
							activitySupplierAgeBandData.setMaxAge(apiActivityAgeBand.getMaxAge());
							toUpdate.add(activitySupplierAgeBandData);
						}
					}
					else
					{
						// ageband is being deleted ... check if it used in any option
						for (ActivityOption activityOption : existingOptions)
						{
							for (ActivityOptionBlock optionBlock : activityOption.getPriceBlocks())
							{
								for (Integer ageBand : optionBlock.getPriceBands().keySet())
								{
									if (ageBand.intValue() == activitySupplierAgeBandData.getId().intValue())
									{
										log.error("updateSupplier::ageBand " + activitySupplierAgeBandData.toString() + " is in use and cannot be deleted");
										return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
												new APIError(-1, "Ageband " + activitySupplierAgeBandData.getId() + " is in use and cannot be deleted"));
									}
								}
							}
						}
						toDelete.add(activitySupplierAgeBandData);
						newData.getAgebands().remove(activitySupplierAgeBandData);
					}
				}
				for (ActivityAgeBand activityAgeBand : activitySupplier.getAgeBands())
				{
					if (activityAgeBand.getId() == null)
					{
						ActivitySupplierAgeBand b = new ActivitySupplierAgeBand();
						b.setBandName(activityAgeBand.getBandName());
						b.setMinAge(activityAgeBand.getMinAge());
						b.setMaxAge(activityAgeBand.getMaxAge());
						toAdd.add(b);
					}
				}
			}
			log.debug("updateSupplier::saving supplier: " + newData);
			// DEBUG
			if (newData.getAgebands() != null)
			{
				for (ActivitySupplierAgeBand ageband : newData.getAgebands())
				{
					log.debug("updateSupplier::saving ageband: " + ageband);
				}
			}
			newData.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.ActivitySupplier savedData = dataService.getActivitySupplierRepo().save(newData);

			log.debug("updateSupplier::ageBands toDelete=" + toDelete);
			for (ActivitySupplierAgeBand d : toDelete)
			{
				d.setActivitySupplier(savedData);
				dataService.getActivitySupplierAgeBandRepo().delete(d);
			}
			log.debug("updateSupplier::ageBands toUpdate=" + toUpdate);
			for (ActivitySupplierAgeBand u : toUpdate)
			{
				u.setActivitySupplier(savedData);
				dataService.getActivitySupplierAgeBandRepo().save(u);
			}
			log.debug("updateSupplier::ageBands toAdd=" + toAdd);
			for (ActivitySupplierAgeBand a : toAdd)
			{
				a.setActivitySupplier(savedData);
				dataService.getActivitySupplierAgeBandRepo().save(a);
			}

			ActivitySupplier newApi = mapperService.mapActivitySupplier(dataService.getActivitySupplierRepo().getOne(savedData.getId()));
			activitySearchService.clearSearchCache();
			return new ResponseData<>(newApi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read activity supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<ActivitySupplier> readSupplier(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("readSupplier::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.ActivitySupplier> dataOpt = dataService.getActivitySupplierRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readSupplier::activity supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "eventSupplier " + id + " not found"));
			}
			ActivitySupplier api = mapperService.mapActivitySupplier(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete activity supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteSupplier(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("deleteSupplier::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.ActivitySupplier> dataOpt = dataService.getActivitySupplierRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteSupplier::activity supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Supplier " + id + " not found"));
			}
			if (dataOpt.get().getActivities() != null && dataOpt.get().getActivities().size() > 0)
			{
				log.error("deleteSupplier::suplier still has activities");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Supplier has activities"));
			}
			dataService.getActivitySupplierRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list categories")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Collection<String>> listCategories(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listCategories::enter");
		try
		{
			SortedSet<String> categories = new TreeSet<>();
			for (Activity activity : mapperService.mapActivities(dataService.getActivityRepo().findAll()))
			{
				categories.addAll(activity.getCategories());
			}
			return new ResponseData<>(categories);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listCategories::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list operators")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/operators", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Collection<String>> listOperators(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listOperators::enter");
		try
		{
			SortedSet<String> operators = new TreeSet<>();
			for (Activity activity : mapperService.mapActivities(dataService.getActivityRepo().findAll()))
			{
				operators.add(activity.getOperator());
			}
			return new ResponseData<>(operators);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listCategories::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list all activities")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<Activity>> listActivities(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listActivities::enter");
		try
		{
			List<Activity> all = mapperService.mapActivities(dataService.getActivityRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listActivities::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update activity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Activity> updateActivity(@RequestHeader("X-imsclient") String imsclient, @RequestBody Activity activity)
	{
		log.debug("updateActivity::enter for " + activity.getId());
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.ActivitySupplier> supplierOpt = dataService.getActivitySupplierRepo().findById(activity.getSupplierId());
			if (!supplierOpt.isPresent())
			{
				log.error("updateActivity::activity supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity Supplier " + activity.getSupplierId() + " not found"));
			}

			List<com.torkirion.eroam.ims.datadomain.Activity> byName = dataService.getActivityRepo().findByName(activity.getName());
			if (byName != null && byName.size() > 0)
			{
				com.torkirion.eroam.ims.datadomain.Activity first = byName.get(0);
				if (  activity.getId() == null || activity.getId().intValue() != first.getId().intValue())
				{
					log.error("updateActivity::duplicate activity name");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Duplicate activity name"));
				}
			}
			List<com.torkirion.eroam.ims.datadomain.Activity> byExternalActivityId = dataService.getActivityRepo().findByExternalActivityId(activity.getExternalActivityId());
			if (byExternalActivityId != null && byExternalActivityId.size() > 0)
			{
				com.torkirion.eroam.ims.datadomain.Activity first = byExternalActivityId.get(0);
				if (  activity.getId() == null || activity.getId().intValue() != first.getId().intValue())
				{
					log.error("updateActivity::duplicate external activity id");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Duplicate External Activity Id name"));
				}
			}

			com.torkirion.eroam.ims.datadomain.Activity data = mapperService.mapActivity(activity, supplierOpt.get());
			if ( activity.getId() != null )
			{
				Optional<com.torkirion.eroam.ims.datadomain.Activity> dataDBOpt = dataService.getActivityRepo().findById(activity.getId());
				if ( !dataDBOpt.isPresent())
				{
					log.error("updateActivity::activity ID " + activity.getId() + " not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity ID " + activity.getId() + " not found"));
				}
				data.setAllotments(dataDBOpt.get().getAllotments());
				data.setDepartureTimes(dataDBOpt.get().getDepartureTimes());
				data.setOptions(dataDBOpt.get().getOptions());
				data.setSales(dataDBOpt.get().getSales());
			}
			data.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.Activity savedData = dataService.getActivityRepo().save(data);

			Activity newAPi = mapperService.mapActivity(savedData);
			activitySearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateActivity::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read activity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Activity> readActivity(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("readActivity::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> dataOpt = dataService.getActivityRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readActivity::activity not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + id + " not found"));
			}
			Activity api = mapperService.mapActivity(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readActivity::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete activity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteActivity(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("deleteActivity::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> dataOpt = dataService.getActivityRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteActivity::activity not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + id + " not found"));
			}
			// check sales
			List<ActivitySale> activitySales = mapperService.mapActivitySales(dataService.getActivitySaleRepo().findByActivity(dataOpt.get()));
			if (activitySales != null && activitySales.size() > 0)
			{
				log.error("deleteActivity::activity has sales");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity has sales"));
			}
			dataService.getActivityAllotmentRepo().deleteByActivityId(dataOpt.get().getId());
			dataService.getActivityDepartureTimeRepo().deleteByActivity(dataOpt.get());
			dataService.getActivityOptionRepo().deleteByActivity(dataOpt.get());
			dataService.getActivityRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteActivity::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list options")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/option", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<ActivityOption>> listOptions(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId)
	{
		log.debug("listOptions::enter");
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> dataOpt = dataService.getActivityRepo().findById(activityId);
			if (!dataOpt.isPresent())
			{
				log.error("listOptions::activity not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + activityId + " not found"));
			}

			List<ActivityOption> options = mapperService.mapActivityOptions(dataService.getActivityOptionRepo().findByActivity(dataOpt.get()));
			return new ResponseData<>(options);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listOptions::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update option")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/option/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<ActivityOption> updateOption(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @RequestBody ActivityOption activityOption)
	{
		log.debug("updateOption::enter for " + activityOption.getId() + " with " + activityOption);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> activityOpt = dataService.getActivityRepo().findById(activityId);
			if (!activityOpt.isPresent())
			{
				log.error("updateOption::activity venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + activityId + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Activity activity = activityOpt.get();
			for (ActivityOptionBlock priceBlock : activityOption.getPriceBlocks())
			{
				for (Integer ageBandId : priceBlock.getPriceBands().keySet())
				{
					log.debug("updateOption::checking ageBandId " + ageBandId);
					boolean ageBandExists = false;
					for (ActivitySupplierAgeBand activityAgeBand : activity.getActivitySupplier().getAgebands())
					{
						log.debug("updateOption::checking ageBandId " + ageBandId + " against " + activityAgeBand.getId());
						if (ageBandId.intValue() == activityAgeBand.getId().intValue())
						{
							ageBandExists = true;
							break;
						}
					}
					if (!ageBandExists)
					{
						log.error("updateOption::ageband " + ageBandId + " is not defined");
						return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "ageband " + ageBandId + " is not defined"));
					}
				}
			}
			List<com.torkirion.eroam.ims.datadomain.ActivityOption> allOptions = dataService.getActivityOptionRepo().findByActivity(activity);
			for (com.torkirion.eroam.ims.datadomain.ActivityOption option : allOptions)
			{
				if (activityOption.getName().equals(option.getName()))
				{
					if (activityOption.getId() == null || activityOption.getId().intValue() != option.getId().intValue())
					{
						log.error("updateOption::duplicate named option, already exists as " + option.getId());
						return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Rate name already exists"));
					}
				}
			}
			com.torkirion.eroam.ims.datadomain.ActivityOption data = mapperService.mapActivityOption(activityOption, activity);
			data.setLastUpdated(LocalDateTime.now());
			log.debug("updateOption::valid, ready to save:" + data.toString());
			data = dataService.getActivityOptionRepo().save(data);
			ActivityOption api = mapperService.mapActivityOption(data);
			activitySearchService.clearSearchCache();
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateOption::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read activity option")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/option/{optionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<ActivityOption> readOption(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @PathVariable Integer optionId)
	{
		log.debug("readOption::enter for " + optionId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.ActivityOption> dataOpt = dataService.getActivityOptionRepo().findById(optionId);
			if (!dataOpt.isPresent())
			{
				log.error("readOption::activity option not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Activity rate " + optionId + " not found"));
			}
			if (dataOpt.get().getActivity().getId().intValue() != activityId.intValue())
			{
				log.error("readOption::activityId " + dataOpt.get().getActivity().getId() + " not matched with " + activityId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + dataOpt.get().getActivity().getId() + " not matched with " + activityId));
			}

			ActivityOption api = mapperService.mapActivityOption(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readOption::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete activity option")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/option/{optionId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteOption(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @PathVariable Integer optionId)
	{
		log.debug("deleteOption::enter for " + optionId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> activityOpt = dataService.getActivityRepo().findById(activityId);
			if (!activityOpt.isPresent())
			{
				log.error("deleteOption::activity not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Activity " + activityId + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Activity activity = activityOpt.get();

			Optional<com.torkirion.eroam.ims.datadomain.ActivityOption> dataOpt = dataService.getActivityOptionRepo().findById(optionId);
			if (!dataOpt.isPresent())
			{
				log.error("deleteOption::activity option not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Activity rate " + optionId + " not found"));
			}
			if (dataOpt.get().getActivity().getId().intValue() != activityId.intValue())
			{
				log.error("deleteOption::activityId " + dataOpt.get().getActivity().getId() + " not matched with " + activityId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + dataOpt.get().getActivity().getId() + " not matched with " + activityId));
			}
			activity.getOptions().remove(dataOpt.get());
			dataService.getActivityRepo().save(activity);
			dataService.getActivityOptionRepo().deleteById(dataOpt.get().getId());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteOption::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list departureTimes")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/departureTime", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<ActivityDepartureTime>> listDepartureTimes(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId)
	{
		log.debug("listDepartureTimes::enter");
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> dataOpt = dataService.getActivityRepo().findById(activityId);
			if (!dataOpt.isPresent())
			{
				log.error("listDepartureTimes::activity not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + activityId + " not found"));
			}

			List<ActivityDepartureTime> options = mapperService.mapActivityDepartureTimes(dataService.getActivityDepartureTimeRepo().findByActivity(dataOpt.get()));
			return new ResponseData<>(options);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listDepartureTimes::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update departureTime")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/departureTime/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<ActivityDepartureTime> updateDepartureTime(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @RequestBody ActivityDepartureTime activityDepartureTime)
	{
		log.debug("updateDepartureTime::enter for " + activityDepartureTime.getId() + " with " + activityDepartureTime);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> activityOpt = dataService.getActivityRepo().findById(activityId);
			if (!activityOpt.isPresent())
			{
				log.error("updateDepartureTime::activity venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + activityId + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Activity activity = activityOpt.get();

			DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
			LocalTime updateDepartureTime = LocalTime.parse(activityDepartureTime.getDepartureTime(), tf);
			List<com.torkirion.eroam.ims.datadomain.ActivityDepartureTime> allDepartureTimes = dataService.getActivityDepartureTimeRepo().findByActivity(activity);
			for (com.torkirion.eroam.ims.datadomain.ActivityDepartureTime departureTime : allDepartureTimes)
			{
				if (updateDepartureTime.equals(departureTime.getDepartureTime()))
				{
					if (activityDepartureTime.getId() == null || activityDepartureTime.getId().intValue() != departureTime.getId().intValue())
					{
						log.error("updateOption::duplicate departureTime, already exists as " + departureTime.getId());
						return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Departure Time already exists"));
					}
				}
				if (activityDepartureTime.getName().equals(departureTime.getName()))
				{
					if (activityDepartureTime.getId() == null || activityDepartureTime.getId().intValue() != departureTime.getId().intValue())
					{
						log.error("updateOption::duplicate departureTime, already exists as " + departureTime.getId());
						return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Departure Name already exists"));
					}
				}
			}

			com.torkirion.eroam.ims.datadomain.ActivityDepartureTime data = mapperService.mapActivityDepartureTime(activityDepartureTime, activity);
			data.setLastUpdated(LocalDateTime.now());
			data = dataService.getActivityDepartureTimeRepo().save(data);
			ActivityDepartureTime api = mapperService.mapActivityDepartureTime(data);
			activitySearchService.clearSearchCache();
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateDepartureTime::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read activity departureTime")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/departureTime/{departureTimeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<ActivityDepartureTime> readDepartureTime(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @PathVariable Integer departureTimeId)
	{
		log.debug("readDepartureTime::enter for " + departureTimeId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.ActivityDepartureTime> dataOpt = dataService.getActivityDepartureTimeRepo().findById(departureTimeId);
			if (!dataOpt.isPresent())
			{
				log.error("readDepartureTime::activity departureTime not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity departureTime " + departureTimeId + " not found"));
			}
			if (dataOpt.get().getActivity().getId().intValue() != activityId.intValue())
			{
				log.error("readDepartureTime::activityId " + dataOpt.get().getActivity().getId() + " not matched with " + activityId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + dataOpt.get().getActivity().getId() + " not matched with " + activityId));
			}

			ActivityDepartureTime api = mapperService.mapActivityDepartureTime(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readDepartureTime::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete activity departureTime")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/departureTime/{departureTimeId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteDepartureTime(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @PathVariable Integer departureTimeId)
	{
		log.debug("deleteDepartureTime::enter for " + departureTimeId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> activityOpt = dataService.getActivityRepo().findById(activityId);
			if (!activityOpt.isPresent())
			{
				log.error("deleteDepartureTime::activity venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + activityId + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Activity activity = activityOpt.get();

			Optional<com.torkirion.eroam.ims.datadomain.ActivityDepartureTime> dataOpt = dataService.getActivityDepartureTimeRepo().findById(departureTimeId);
			if (!dataOpt.isPresent())
			{
				log.error("deleteDepartureTime::activity departureTime not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity departureTime " + departureTimeId + " not found"));
			}
			if (dataOpt.get().getActivity().getId().intValue() != activityId.intValue())
			{
				log.error("deleteDepartureTime::activityId " + dataOpt.get().getActivity().getId() + " not matched with " + activityId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + dataOpt.get().getActivity().getId() + " not matched with " + activityId));
			}
			activity.getDepartureTimes().remove(dataOpt.get());
			dataService.getActivityDepartureTimeRepo().delete(dataOpt.get());
			dataService.getActivityRepo().save(activity);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteDepartureTime::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list allotments")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/allotments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<ActivityAllotment.AllotmentSummary>> listAllotments(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId)
	{
		log.debug("listAllotments::enter");
		try
		{
			List<com.torkirion.eroam.ims.datadomain.ActivityAllotment> dataList = dataService.getActivityAllotmentRepo().findByActivityId(activityId);
			List<com.torkirion.eroam.ims.apidomain.ActivityAllotment.AllotmentSummary> summaries = new ArrayList<>();
			for (com.torkirion.eroam.ims.datadomain.ActivityAllotment a : dataList)
			{
				com.torkirion.eroam.ims.apidomain.ActivityAllotment.AllotmentSummary summary = new com.torkirion.eroam.ims.apidomain.ActivityAllotment.AllotmentSummary(
						activityId, a.getOptionId(), a.getDepartureTimeId());
				if (!summaries.contains(summary))
					summaries.add(summary);
			}

			return new ResponseData<>(summaries);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listAllotments::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list allotment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/allotment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<ActivityAllotment> listAllotment(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, Integer optionId, Integer departureTimeId)
	{
		log.debug("listAllotment::enter");
		try
		{
			List<com.torkirion.eroam.ims.datadomain.ActivityAllotment> dataList = dataService.getActivityAllotmentRepo().findByActivityIdAndOptionIdAndDepartureTimeIdOrderByAllotmentDate(activityId,
					optionId, departureTimeId);

			ActivityAllotment all = mapperService.mapActivityAllotment(dataList);
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listAllotments::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update allotment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/allotment/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<ActivityAllotment> updateAllotment(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @RequestBody ActivityAllotment activityAllotment)
	{
		log.debug("updateAllotment::enter for " + activityAllotment.getAllotmentSummary());
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> activityOpt = dataService.getActivityRepo().findById(activityId);
			if (!activityOpt.isPresent())
			{
				log.error("updateAllotment::activity not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + activityId + " not found"));
			}
			if (activityOpt.get().getAllotmentByDepartureAndOption() && activityAllotment.getAllotmentSummary().getOptionId() == null)
			{
				log.error("updateAllotment::must specify optionId");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "must specify OptionId"));
			}
			if (!activityOpt.get().getAllotmentByDepartureAndOption() && activityAllotment.getAllotmentSummary().getOptionId() != null)
			{
				log.error("updateAllotment::cannot specify optionId");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "cannot specify OptionId"));
			}
			if (activityAllotment.getAllotmentSummary().getOptionId() != null)
			{
				Optional<com.torkirion.eroam.ims.datadomain.ActivityOption> activityOptionOpt = dataService.getActivityOptionRepo()
						.findById(activityAllotment.getAllotmentSummary().getOptionId());
				if (!activityOptionOpt.isPresent())
				{
					log.error("updateAllotment::activity option not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							new APIError(-1, "activity option " + activityAllotment.getAllotmentSummary().getOptionId() + " not found"));
				}
			}
			Optional<com.torkirion.eroam.ims.datadomain.ActivityDepartureTime> activityDepartureTimeOpt = dataService.getActivityDepartureTimeRepo()
					.findById(activityAllotment.getAllotmentSummary().getDepartureTimeId());
			if (!activityDepartureTimeOpt.isPresent())
			{
				log.error("updateAllotment::activity departure time not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						new APIError(-1, "activity departure time " + activityAllotment.getAllotmentSummary().getDepartureTimeId() + " not found"));
			}
			List<com.torkirion.eroam.ims.datadomain.ActivityAllotment> dataList = mapperService.mapActivityAllotment(activityAllotment);
			for (com.torkirion.eroam.ims.datadomain.ActivityAllotment a : dataList)
			{
				dataService.getActivityAllotmentRepo().save(a);
			}
			activitySearchService.clearSearchCache();
			return listAllotment(imsclient, activityAllotment.getAllotmentSummary().getActivityId(), activityAllotment.getAllotmentSummary().getOptionId(),
					activityAllotment.getAllotmentSummary().getDepartureTimeId());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateAllotment::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete activity allotment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/{activityId}/allotment", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteAllotment(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer activityId, @RequestBody ActivityAllotment.AllotmentSummary allotmentSummary)
	{
		log.debug("deleteAllotment::enter for " + allotmentSummary);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Activity> activityOpt = dataService.getActivityRepo().findById(activityId);
			if (!activityOpt.isPresent())
			{
				log.error("deleteDepartureTime::activity venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "activity " + activityId + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Activity activity = activityOpt.get();

			if (!activity.getAllotmentByDepartureAndOption() )
			{
				allotmentSummary.setOptionId(null);
			}

			dataService.getActivityAllotmentRepo().deleteByActivityIdAndOptionIdAndDepartureTimeId(allotmentSummary.getActivityId(), allotmentSummary.getOptionId(), allotmentSummary.getDepartureTimeId());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteDepartureTime::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list sales")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/activity/sales", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<ActivitySale>> listSales(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSales::enter");
		try
		{

			List<ActivitySale> allSales = mapperService.mapActivitySales(dataService.getActivitySaleRepo().findAll());
			return new ResponseData<>(allSales);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listClassifications::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	/*
	@ApiOperation(value = "list suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/supplier", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<SupplierSummary>> listSuppliers()
	{
		log.debug("listSuppliers::enter");
		try
		{
			List<SupplierSummary> allSummary = new ArrayList<>();
			List<com.torkirion.eroam.microservice.ims.datadomain.Supplier> allData = dataService.getSupplierRepo().findByForActivities(true);
			log.debug("listSuppliers::found " + allData.size() + " from basic repo");
			for (com.torkirion.eroam.microservice.ims.datadomain.Supplier s : allData)
			{
				SupplierSummary supplierSummary = mapperService.mapSupplierSummary(s);
				allSummary.add(supplierSummary);
			}
			return new ResponseData<>(allSummary);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listSuppliers::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	} */
}
