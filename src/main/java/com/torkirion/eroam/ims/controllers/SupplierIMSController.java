package com.torkirion.eroam.ims.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.ims.apidomain.APIError;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.apidomain.Supplier;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.apidomain.Supplier.SupplierContact;
import com.torkirion.eroam.ims.services.*;

@RestController
@RequestMapping("/supplier/v1")
@Api(value = "Supplier IMS API")
@Slf4j
@AllArgsConstructor
public class SupplierIMSController
{
	@Autowired
	private DataService dataService;

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

	@ApiOperation(value = "list all suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<SupplierSummary>> listSuppliers(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSuppliers::enter");
		try
		{
			List<SupplierSummary> allSummary = new ArrayList<>();
			List<com.torkirion.eroam.ims.datadomain.Supplier> allData = dataService.getSupplierRepo().findAll();
			log.debug("listSuppliers::found " + allData.size() + " from basic repo");
			for (com.torkirion.eroam.ims.datadomain.Supplier s : allData)
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
	}

	@ApiOperation(value = "read supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Supplier> readSupplier(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id)
	{
		log.debug("readSupplier::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Supplier> dataOpt = dataService.getSupplierRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readSupplier::supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "supplier item " + id + " not found"));
			}

			Supplier api = (Supplier) mapperService.mapSupplier(dataOpt.get());
			log.debug("readSupplier::returning " + api);
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Supplier> updateSupplier(@RequestHeader("X-imsclient") String imsclient, @RequestBody Supplier api)
	{
		log.debug("updateSupplier::enter for " + api.getId());
		try
		{
			if (StringUtils.isEmpty(api.getSupplierName()))
			{
				log.error("updateSupplier::empty name for input " + api);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Supplier Name must be completed"));
			}
			com.torkirion.eroam.ims.datadomain.Supplier data = null;
			if (api.getId() == null || api.getId().intValue() == 0)
			{
				data = new com.torkirion.eroam.ims.datadomain.Supplier();
			}
			else
			{
				Optional<com.torkirion.eroam.ims.datadomain.Supplier> dataOpt = dataService.getSupplierRepo().findById(api.getId());
				if (!dataOpt.isPresent())
				{
					log.error("updateSupplier::updating unknown supplier " + api.getId());
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "updating unknown supplier " + api.getId()));
				}
				data = dataOpt.get();
			}
			BeanUtils.copyProperties(api, data);
			if (api.getContacts() != null)
			{
				for (SupplierContact contact : api.getContacts())
				{
					switch ( contact.getContactType())
					{
						case RESERVATIONS: 
							data.setReservationsEmail(contact.getEmail());
							data.setReservationsName(contact.getName());
							data.setReservationsPhone(contact.getPhone());
							break;
						case CONTRACTING: 
							data.setContractingEmail(contact.getEmail());
							data.setContractingName(contact.getName());
							data.setContractingPhone(contact.getPhone());
							break;
						case CUSTOMER_SERVICE: 
							data.setCustomerserviceEmail(contact.getEmail());
							data.setCustomerserviceName(contact.getName());
							data.setCustomerservicePhone(contact.getPhone());
							break;
						case GM: 
							data.setGmEmail(contact.getEmail());
							data.setGmName(contact.getName());
							data.setGmPhone(contact.getPhone());
							break;
						case ACCOUNTS: 
							data.setAccountsEmail(contact.getEmail());
							data.setAccountsName(contact.getName());
							data.setAccountsPhone(contact.getPhone());
							break;
					}
				}
			}

			data.setLastUpdated(LocalDateTime.now());
			data = dataService.getSupplierRepo().save(data);

			Supplier apiResponse = mapperService.mapSupplier(data);
			return new ResponseData<>(apiResponse);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateTransportation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteSupplier(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id)
	{
		log.debug("deleteSupplier::enter for " + id);
		try
		{
			dataService.getSupplierRepo().deleteById(id);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
}
