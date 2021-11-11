package com.example


import io.micronaut.http.HttpMethod

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * POGO - To maintain API request info
 * @author vijay
 */
class ApiRequestInfo {

    @NotNull @NotBlank
    String baseURL

    @NotNull @NotBlank
    String endpoint

    @NotNull @NotBlank
    HttpMethod method

    Map headers = [:]

    Map cookies = [:]

    Object payload

    Long readTimeout = null

    Long connectTimeout = null

    boolean verifyCertificate = true

    String toString(){
        return "{'baseURL': $baseURL, 'endpoint' : $endpoint, 'httpMethod': $method, 'headers' : $headers, " +
                "'cookies' : $cookies, 'payload' : $payload, 'readTimeout' : $readTimeout, " +
                "'connectTimeout' : $connectTimeout, 'verifyCertificate' : $verifyCertificate}"
    }
}
