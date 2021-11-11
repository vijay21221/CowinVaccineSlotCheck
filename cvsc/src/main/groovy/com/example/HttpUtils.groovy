package com.example

import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.uri.UriBuilder
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.time.Duration

/**
 * Utility class to make API calls
 *
 * @author vijay
 */
class HttpUtils {
    private static final Log log = LogFactory.getLog(HttpUtils.class)


    static Map apiCaller(ApiRequestInfo requestInfo, boolean parseJSONResponse = true){
        DefaultHttpClientConfiguration config = new DefaultHttpClientConfiguration()
        if(requestInfo.readTimeout)
            config.setReadTimeout(Duration.ofSeconds(requestInfo.readTimeout))
        if(requestInfo.connectTimeout)
            config.setConnectTimeout(Duration.ofSeconds(requestInfo.connectTimeout))

        HttpClient httpClient = BeanFactory.getBean(HttpClient.class, requestInfo.baseURL.toURL(), config) as HttpClient
        MutableHttpRequest httpRequest

        switch (requestInfo.method){
            case HttpMethod.GET :
                UriBuilder uriBuilder = UriBuilder.of(requestInfo.endpoint)
                requestInfo.payload.each {String key, Object val ->
                    uriBuilder.queryParam(key, val)
                }
                httpRequest = HttpRequest.GET(uriBuilder.build())
                break
            case HttpMethod.POST :
                httpRequest = HttpRequest.POST(requestInfo.endpoint, requestInfo.payload)
                break
            case HttpMethod.PUT :
                httpRequest = HttpRequest.PUT(requestInfo.endpoint, requestInfo.payload)
                break
            case HttpMethod.DELETE :
                httpRequest = HttpRequest.DELETE(requestInfo.endpoint)
                break
        }

        httpRequest.headers(requestInfo.headers)


        HttpResponse httpResponse
        Map response = [:]

        try {
            httpResponse = httpClient.toBlocking().exchange(httpRequest, String)
        }catch(HttpClientResponseException htcre){
            // This exception occurs when a response returns an error code equal to or greater than 400
            httpResponse = htcre.getResponse()
            log.error("API Failed. {requestInfo : ${requestInfo.toString()}, " +
                    "response : {status : ${httpResponse.status()}, body : ${httpResponse.getBody()}")
        }catch(Exception e){
            log.error("Error while calling API. {requestInfo : ${requestInfo.toString()}, error : ${e.getMessage()}}")
            response.put("statusCode", HttpStatus.SERVICE_UNAVAILABLE.code)
            response.put("errorMessage", e.getMessage())
        }finally{

            if(httpResponse){
                response.put("statusCode", httpResponse.getStatus().getCode())
                Object body = httpResponse.body()
                if(body && parseJSONResponse){
                    body =  JSONParser.stringToMapORList(body.toString())
                }
                response.put("body", body)
            }

            if(httpClient != null) {
                httpClient.close()
            }
        }

        return response
    }
}
