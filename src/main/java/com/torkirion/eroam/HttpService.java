package com.torkirion.eroam;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.annotations.ApiModelProperty;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
public abstract class HttpService
{
	protected abstract void addHeaders(org.apache.http.HttpMessage httpMessage);
	
	protected abstract String getUrl();
	
	protected int getConnectionTimeout()
	{
		return 60;
	}
	
	public String doCallPut(String callType, Object requestData)
	{
		return doCallSend(callType, requestData, HttpPut.class);
	}

	public String doCallPatch(String callType, Object requestData)
	{
		return doCallSend(callType, requestData, HttpPatch.class);
	}

	public String doCallPost(String callType, Object requestData)
	{
		return doCallSend(callType, requestData, HttpPost.class);
	}

	private String doCallSend(String callType, Object requestData, Class<?> classtType)
	{
		log.debug("doCallSend::entering for " + callType);

		CloseableHttpClient httpClient = null;
		long timer1 = System.currentTimeMillis();
		try
		{
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(getConnectionTimeout() * 1000).setConnectTimeout(getConnectionTimeout() * 1000)
					.setConnectionRequestTimeout(getConnectionTimeout() * 1000).build();

			httpClient = HttpClients.createDefault();

			String requestURL = getUrl() + "/" + callType;

			HttpEntityEnclosingRequestBase httpPost = (HttpEntityEnclosingRequestBase) classtType.getDeclaredConstructor().newInstance();
			httpPost.setURI(URI.create(requestURL));
			httpPost.setConfig(requestConfig);
			log.debug("doCallSend::httpPost/Put/Patch to " + requestURL + " is " + httpPost);

			addHeaders(httpPost);

			Writer writer = null;
			if (requestData instanceof String)
			{
				StringEntity body = new StringEntity((String)requestData);
				httpPost.setEntity(body);
			}
			else if (requestData != null )
			{
				writer = new StringWriter();
				getObjectMapper().writeValue(writer, requestData);
				log.debug("doCallSend::submitting body " + writer.toString());
				StringEntity body = new StringEntity(writer.toString());
				httpPost.setEntity(body);
			}

			HttpClientContext context = HttpClientContext.create();
			context.setAttribute("http.protocol.version", HttpVersion.HTTP_1_1);

			//log.debug("doCallSend::executing request method " + httpPost.getMethod());
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			//log.debug("doCallSend::response " + response.getStatusLine());
			String responseString = "";
			if (entity != null)
			{
				//log.debug("doCallSend::Response content length: " + entity.getContentLength());
				responseString = EntityUtils.toString(entity, "UTF-8");
				//log.debug("doCallSend::Response string length = " + responseString.length());
				//log.debug("doCallSend::Response string length = " + responseString.length());
			}

			//log.debug("doCallSend::finishing with JSON " + responseString);
			//log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
			return responseString;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (httpClient != null)
				try
				{
					httpClient.close();
				}
				catch (Exception e)
				{
					log.debug("doCallSend::caught exception on close " + e.toString());
				}
		}
		log.debug("doCallSend::exiting with empty error");
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
		return "";
	}

	public String doCallGet(String requestURL, Map<String, String> params)
	{
		log.debug("doCallGet::entering");

		int connectionTimeout = 60;
		CloseableHttpClient httpClient = null;
		long timer1 = System.currentTimeMillis();
		try
		{
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectionTimeout * 1000).setConnectTimeout(connectionTimeout * 1000)
					.setConnectionRequestTimeout(connectionTimeout * 1000).build();

			httpClient = HttpClients.createDefault();

			URIBuilder builder = new URIBuilder(getUrl() + "/" + requestURL);
			if (params != null)
			{
				for (Map.Entry<String, String> param : params.entrySet())
				{
					if (param.getValue() != null)
						builder.setParameter(param.getKey(), param.getValue());
				}
			}

			HttpGet httpGet = new HttpGet(builder.build());
			httpGet.setConfig(requestConfig);
			log.debug("doCallGet::httpGet to " + getUrl() + "/" + requestURL + " is " + httpGet);

			addHeaders(httpGet);

			HttpClientContext context = HttpClientContext.create();
			context.setAttribute("http.protocol.version", HttpVersion.HTTP_1_1);

			log.debug("doCallGet::executing request method " + httpGet.getMethod());
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			log.debug("doCallGet::response " + response.getStatusLine());
			String responseString = "";
			if (entity != null)
			{
				log.debug("doCallGet::Response content length: " + entity.getContentLength());
				responseString = EntityUtils.toString(entity, "UTF-8");
				log.debug("doCallGet::Response string length = " + responseString.length());
			}

			//log.debug("doCallGet::finishing with JSON " + responseString);
			log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
			return responseString;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (httpClient != null)
				try
				{
					httpClient.close();
				}
				catch (Exception e)
				{
					log.debug("doCallGet::caught exception on close " + e.toString());
				}
		}
		log.debug("doCallGet::exiting with empty error");
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
		return "";
	}

	public String doCallDelete(String requestURL, Map<String, String> params)
	{
		log.debug("doCallDelete::entering");

		int connectionTimeout = 60;
		CloseableHttpClient httpClient = null;
		long timer1 = System.currentTimeMillis();
		try
		{
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectionTimeout * 1000).setConnectTimeout(connectionTimeout * 1000)
					.setConnectionRequestTimeout(connectionTimeout * 1000).build();

			httpClient = HttpClients.createDefault();

			URIBuilder builder = new URIBuilder(getUrl() + "/" + requestURL);
			if (params != null)
			{
				for (Map.Entry<String, String> param : params.entrySet())
				{
					if (param.getValue() != null)
						builder.setParameter(param.getKey(), param.getValue());
				}
			}

			HttpDelete httpDelete = new HttpDelete(builder.build());
			httpDelete.setConfig(requestConfig);
			log.debug("doCallDelete::httpDelete to " + getUrl() + "/" + requestURL + " is " + httpDelete);

			addHeaders(httpDelete);

			HttpClientContext context = HttpClientContext.create();
			context.setAttribute("http.protocol.version", HttpVersion.HTTP_1_1);

			log.debug("doCallDelete::executing request method " + httpDelete.getMethod());
			HttpResponse response = httpClient.execute(httpDelete);
			HttpEntity entity = response.getEntity();
			log.debug("doCallDelete::response " + response.getStatusLine());
			String responseString = "";
			if (entity != null)
			{
				log.debug("doCallDelete::Response content length: " + entity.getContentLength());
				responseString = EntityUtils.toString(entity, "UTF-8");
				log.debug("doCallDelete::Response string length = " + responseString.length());
				log.debug("doCallDelete::Response string length = " + responseString.length());
			}

			log.debug("doCdoCallDeleteallGet::finishing with JSON " + responseString);
			log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
			return responseString;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (httpClient != null)
				try
				{
					httpClient.close();
				}
				catch (Exception e)
				{
					log.debug("doCallDelete::caught exception on close " + e.toString());
				}
		}
		log.debug("doCallDelete::exiting with empty error");
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
		return "";
	}

	private ObjectMapper getObjectMapper()
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		return objectMapper;
	}
}
