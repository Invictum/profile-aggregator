package com.example.aggregator;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public RestTemplate getDefaultClient() {
        return new RestTemplateBuilder()
                .errorHandler(new NoOpResponseHandler())
                .build();
    }

    /**
     * Error handler implementation that just ignores errors. So client code is responsible for managing them.
     */
    private static class NoOpResponseHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {

        }
    }
}
