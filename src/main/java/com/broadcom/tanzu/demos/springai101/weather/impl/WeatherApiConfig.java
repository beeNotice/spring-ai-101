/*
 * Copyright (c) 2024 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.broadcom.tanzu.demos.springai101.weather.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
class WeatherApiConfig {
    @Bean
    WeatherApi weatherApi(RestClient.Builder rcb, @Value("${openweathermap.api-key}") String owmApiKey) {
        // Create a RestClient interface bound to OpenWeatherMap API.
        final var restClient = rcb.clone()
                .baseUrl("https://api.openweathermap.org")
                .requestFactory(new SimpleClientHttpRequestFactory() {
                    @Override
                    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
                        // Set appid as a query param in every request.
                        final var newUri = UriComponentsBuilder.fromHttpUrl(URLDecoder.decode(uri.toASCIIString(), StandardCharsets.US_ASCII))
                                .queryParam("appid", owmApiKey).build().toUri();
                        return super.createRequest(newUri, httpMethod);
                    }
                })
                .build();
        // Create a client interface implementation for the OpenWeatherMap API.
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(WeatherApi.class);
    }
}
