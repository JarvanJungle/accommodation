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

import com.torkirion.eroam.ims.apidomain.APIError;
import com.torkirion.eroam.ims.apidomain.EventType;
import com.torkirion.eroam.ims.apidomain.Merchandise;
import com.torkirion.eroam.ims.apidomain.MerchandiseCategory;
import com.torkirion.eroam.ims.apidomain.MerchandiseOption;
import com.torkirion.eroam.ims.apidomain.MerchandiseSale;
import com.torkirion.eroam.ims.apidomain.MerchandiseSupplier;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.services.*;
import com.torkirion.eroam.microservice.merchandise.services.MerchandiseSearchService;

@RestController
@RequestMapping("/merchandiseims/v1")
@Api(value = "Merchandise IMS API")
@Slf4j
@AllArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class MerchandiseIMSController
{
	@Autowired
	private DataService dataService;

	@Autowired
	private MapperService mapperService;

	@Autowired
	private MerchandiseSearchService merchandiseSearchService;

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

	@ApiOperation(value = "list merchandise categories")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<MerchandiseCategory>> listCategories(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listCategories::enter");
		try
		{
			List<MerchandiseCategory> all = mapperService.mapMerchandiseCategories(dataService.getMerchandiseCategoryRepo().findAll());
			SortedSet<MerchandiseCategory> sortedTypes = new TreeSet<>(new MerchandiseCategory.MerchandiseCategorySorterByName());
			sortedTypes.addAll(all);
			return new ResponseData<>(new ArrayList<MerchandiseCategory>(sortedTypes));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listCategories::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "add merchandise category")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/category/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<MerchandiseCategory>> createMerchandise(@RequestHeader("X-imsclient") String imsclient, @RequestBody String name)
	{
		log.debug("createMerchandise::enter");
		try
		{
			List<com.torkirion.eroam.ims.datadomain.MerchandiseCategory> existings = dataService.getMerchandiseCategoryRepo().findByName(name);
			if (existings == null || existings.size() == 0)
			{
				com.torkirion.eroam.ims.datadomain.MerchandiseCategory data = new com.torkirion.eroam.ims.datadomain.MerchandiseCategory();
				data.setName(name);
				dataService.getMerchandiseCategoryRepo().save(data);
			}
			List<MerchandiseCategory> all = mapperService.mapMerchandiseCategories(dataService.getMerchandiseCategoryRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("createMerchandise::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list merchandise suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<MerchandiseSupplier>> listSuppliers(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSuppliers::enter");
		try
		{
			List<MerchandiseSupplier> all = mapperService.mapMerchandiseSuppliers(dataService.getMerchandiseSupplierRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listSuppliers::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update merchandise supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<MerchandiseSupplier> updateSupplier(@RequestHeader("X-imsclient") String imsclient, @RequestBody MerchandiseSupplier merchandiseSupplier)
	{
		log.debug("updateSupplier::enter for " + merchandiseSupplier.getId());
		try
		{
			com.torkirion.eroam.ims.datadomain.MerchandiseSupplier data = mapperService.mapMerchandiseSupplier(merchandiseSupplier);
			data.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.MerchandiseSupplier savedData = dataService.getMerchandiseSupplierRepo().save(data);

			MerchandiseSupplier newAPi = mapperService.mapMerchandiseSupplier(savedData);
			merchandiseSearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read merchandise supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<MerchandiseSupplier> readSupplier(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("readSupplier::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.MerchandiseSupplier> dataOpt = dataService.getMerchandiseSupplierRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readSupplier::merchandise supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandiseSupplier " + id + " not found"));
			}
			MerchandiseSupplier api = mapperService.mapMerchandiseSupplier(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete merchandise supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> deleteSupplier(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("deleteSupplier::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.MerchandiseSupplier> dataOpt = dataService.getMerchandiseSupplierRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteSupplier::merchandise supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Supplier " + id + " not found"));
			}
			if ( dataOpt.get().getMerchandise() != null && dataOpt.get().getMerchandise().size() > 0 )
			{
				log.error("deleteSupplier::supplier still has events");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Supplier has merchandise"));
			}
			dataService.getMerchandiseSupplierRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list merchandise")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<Merchandise>> listMerchandises(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listMerchandises::enter");
		try
		{
			List<Merchandise> all = mapperService.mapMerchandise(dataService.getMerchandiseRepo().findAll());
			SortedSet<Merchandise> sortedTypes = new TreeSet<>(new Merchandise.MerchandiseSorterByExternalMerchandiseId());
			sortedTypes.addAll(all);
			return new ResponseData<>(new ArrayList<Merchandise>(sortedTypes));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listMerchandises::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update merchandise")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Merchandise> updateMerchandise(@RequestHeader("X-imsclient") String imsclient, @RequestBody Merchandise merchandise)
	{
		log.debug("updateMerchandise::enter for " + merchandise.getId());
		try
		{
			List<com.torkirion.eroam.ims.datadomain.MerchandiseCategory> categoryList = dataService.getMerchandiseCategoryRepo().findByName(merchandise.getMerchandiseCategory());
			if (categoryList.size() == 0)
			{
				log.error("updateMerchandise::merchandise category not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise Category " + merchandise.getMerchandiseCategory() + " not found"));
			}
			Optional<com.torkirion.eroam.ims.datadomain.MerchandiseSupplier> supplierOpt = dataService.getMerchandiseSupplierRepo().findById(merchandise.getSupplierId());
			if (!supplierOpt.isPresent())
			{
				log.error("updateMerchandise::merchandise supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise Supplier " + merchandise.getSupplierId() + " not found"));
			}
			
			com.torkirion.eroam.ims.datadomain.Merchandise apiData = mapperService.mapMerchandise(merchandise, supplierOpt.get(), categoryList.get(0));
			
			if ( merchandise.getId() != null )
			{
				if ( merchandise.getExternalMerchandiseId() != null )
				{
					List<com.torkirion.eroam.ims.datadomain.Merchandise> merchandiseList = dataService.getMerchandiseRepo().findByExternalMerchandiseId(merchandise.getExternalMerchandiseId());
					if ( merchandiseList.size() > 0)
					{
						com.torkirion.eroam.ims.datadomain.Merchandise m = merchandiseList.get(0);
						if ( m.getId().intValue() != merchandise.getId().intValue())
						{
							log.error("updateMerchandise::ExternalMerchandiseId must be unique");
							return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "External Merchandise Id" + merchandise.getExternalMerchandiseId() + " must be unique"));
						}
					}
				}
				com.torkirion.eroam.ims.datadomain.Merchandise dbData = dataService.getMerchandiseRepo().getOne(merchandise.getId());
				if ( merchandise.getId() != null )
				{
					Optional<com.torkirion.eroam.ims.datadomain.Merchandise> dataDBOpt = dataService.getMerchandiseRepo().findById(merchandise.getId());
					if ( !dataDBOpt.isPresent())
					{
						log.error("updateActivity::merchandise ID " + merchandise.getId() + " not found");
						return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise ID " + merchandise.getId() + " not found"));
					}
					apiData.setOptions(dataDBOpt.get().getOptions());
					apiData.setSales(dataDBOpt.get().getSales());
				}
				apiData.setEventMerchandiseLinks(dbData.getEventMerchandiseLinks());
			}
			
			apiData.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.Merchandise savedData = dataService.getMerchandiseRepo().save(apiData);

			Merchandise newAPi = mapperService.mapMerchandise(savedData);
			merchandiseSearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateMerchandise::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read merchandise")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Merchandise> readMerchandise(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("readMerchandise::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Merchandise> dataOpt = dataService.getMerchandiseRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readMerchandise::merchandise venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise " + id + " not found"));
			}
			Merchandise api = mapperService.mapMerchandise(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readMerchandise::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete merchandise")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> deleteMerchandise(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer id)
	{
		log.debug("deleteMerchandise::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Merchandise> dataOpt = dataService.getMerchandiseRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteMerchandise::merchandise not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise " + id + " not found"));
			}
			// check sales
			List<MerchandiseSale> merchandiseSales = mapperService.mapMerchandiseSales(dataService.getMerchandiseSaleRepo().findByMerchandise(dataOpt.get()));
			if ( merchandiseSales != null && merchandiseSales.size() > 0 )
			{
				log.error("deleteMerchandise::merchandise has sales");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise has sales"));
			}
			dataService.getMerchandiseOptionRepo().deleteByMerchandise(dataOpt.get());
			dataService.getMerchandiseRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteMerchandise::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	@ApiOperation(value = "list options")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise/{merchandiseId}/option", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<MerchandiseOption>> listOptions(@RequestHeader("X-imsclient") String imsclient, Integer merchandiseId)
	{
		log.debug("listOptions::enter");
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Merchandise> dataOpt = dataService.getMerchandiseRepo().findById(merchandiseId);
			if (!dataOpt.isPresent())
			{
				log.error("listOptions::merchandise option not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise " + merchandiseId + " not found"));
			}

			List<MerchandiseOption> all = mapperService.mapMerchandiseOptions(dataService.getMerchandiseOptionRepo().findByMerchandise(dataOpt.get()));
			return new ResponseData<>(all);
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
	@RequestMapping(value = "/merchandise/{merchandiseId}/option/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<MerchandiseOption> updateOption(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer merchandiseId, @RequestBody MerchandiseOption merchandiseOption)
	{
		log.debug("updateOption::enter for " + merchandiseOption.getId());
		try
		{
			if (merchandiseOption.getAllotment() == null || merchandiseOption.getAllotment().intValue() < -1)
			{
				log.error("updateOption::merchandiseOption.getAllotment() must be greater than -1:" + merchandiseOption.getAllotment());
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise option allotment must be -1 (freesell), 0 or a positive number"));
			}
			Optional<com.torkirion.eroam.ims.datadomain.Merchandise> merchandiseOpt = dataService.getMerchandiseRepo().findById(merchandiseId);
			if (!merchandiseOpt.isPresent())
			{
				log.error("updateOption::merchandise venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise " + merchandiseId + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Merchandise merchandiseData = merchandiseOpt.get();
			com.torkirion.eroam.ims.datadomain.MerchandiseOption optionData = null;
			if (merchandiseOption.getId() != null)
			{
				Optional<com.torkirion.eroam.ims.datadomain.MerchandiseOption> optionOpt = dataService.getMerchandiseOptionRepo().findById(merchandiseOption.getId());
				if (!optionOpt.isPresent())
				{
					log.error("updateOption::merchandise option not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise option " + merchandiseOption.getId() + " not found"));
				}
				optionData = optionOpt.get();
			}
			if (optionData == null)
			{
				optionData = mapperService.mapMerchandiseOption(merchandiseOption, merchandiseData);
				optionData.setLastUpdated(LocalDateTime.now());
				optionData = dataService.getMerchandiseOptionRepo().save(optionData);
				merchandiseData.getOptions().add(optionData);
				dataService.getMerchandiseRepo().save(merchandiseData);
			}
			else
			{
				optionData = dataService.getMerchandiseOptionRepo().save(mapperService.mapMerchandiseOption(merchandiseOption, merchandiseData));
			}
			MerchandiseOption api = mapperService.mapMerchandiseOption(optionData);
			merchandiseSearchService.clearSearchCache();
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateOption::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read merchandise option")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise/{merchandiseId}/option/{optionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<MerchandiseOption> readOption(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer merchandiseId, @PathVariable Integer optionId)
	{
		log.debug("readOption::enter for " + optionId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.MerchandiseOption> dataOpt = dataService.getMerchandiseOptionRepo().findById(optionId);
			if (!dataOpt.isPresent())
			{
				log.error("readOption::merchandise option not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise option " + optionId + " not found"));
			}
			if (dataOpt.get().getMerchandise().getId().intValue() != merchandiseId.intValue())
			{
				log.error("readOption::merchandiseId " + dataOpt.get().getMerchandise().getId() + " not matched with " + merchandiseId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						new APIError(-1, "merchandise " + dataOpt.get().getMerchandise().getId() + " not matched with " + merchandiseId));
			}

			MerchandiseOption api = mapperService.mapMerchandiseOption(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readOption::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete merchandise option")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise/{merchandiseId}/option/{optionId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteOption(@RequestHeader("X-imsclient") String imsclient, @PathVariable Integer merchandiseId, @PathVariable Integer optionId)
	{
		log.debug("deleteOption::enter for " + optionId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.MerchandiseOption> dataOpt = dataService.getMerchandiseOptionRepo().findById(optionId);
			if (!dataOpt.isPresent())
			{
				log.error("deleteOption::merchandise option not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise option " + optionId + " not found"));
			}
			if (dataOpt.get().getMerchandise().getId().intValue() != merchandiseId.intValue())
			{
				log.error("deleteOption::merchandiseId " + dataOpt.get().getMerchandise().getId() + " not matched with " + merchandiseId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						new APIError(-1, "merchandise " + dataOpt.get().getMerchandise().getId() + " not matched with " + merchandiseId));
			}
			Optional<com.torkirion.eroam.ims.datadomain.Merchandise> merchandiseOpt = dataService.getMerchandiseRepo().findById(merchandiseId);
			if (!dataOpt.isPresent())
			{
				log.error("deleteOption::merchandise not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise " + merchandiseId + " not found"));
			}
			// check sales
			List<MerchandiseSale> merchandiseSales = mapperService.mapMerchandiseSales(dataService.getMerchandiseSaleRepo().findByMerchandise(merchandiseOpt.get()));
			for ( MerchandiseSale sale : merchandiseSales)
			{
				if ( sale.getOptionId().intValue() == optionId.intValue())
				{
					log.error("deleteMerchandise::event has sales");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "option has sales"));
				}
			}
			merchandiseOpt.get().getOptions().remove(dataOpt.get());
			dataService.getMerchandiseRepo().save(merchandiseOpt.get());
			dataService.getMerchandiseOptionRepo().deleteById(optionId);
			log.debug("deleteOption::deleted " + dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteMerchandise::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	@ApiOperation(value = "list sales")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/merchandise/sales", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<MerchandiseSale>> listSales(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSales::enter");
		try
		{

			List<MerchandiseSale> allSales = mapperService.mapMerchandiseSales(dataService.getMerchandiseSaleRepo().findAll());
			return new ResponseData<>(allSales);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listOptions::error " + e.toString(), e);
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
			List<com.torkirion.eroam.microservice.ims.datadomain.Supplier> allData = dataService.getSupplierRepo().findByForAccommodation(true);
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
