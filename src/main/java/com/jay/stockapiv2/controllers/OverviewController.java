package com.jay.stockapiv2.controllers;

import com.jay.stockapiv2.utils.ApiErrorHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    @Autowired
    private Environment env;

    private final String BASE_URL = "https://www.alphavantage.co/query?function=OVERVIEW";

    //http://localhost:8080/api/overview/test
    @GetMapping("/test")
    private ResponseEntity<?> testOverview (RestTemplate restTemplate) {
        try{

            String url = BASE_URL + "&symbol=IBM&apikey=demo";

            String alphaVantageResponse = restTemplate.getForObject(url, String.class);

            return ResponseEntity.ok(alphaVantageResponse);

        } catch (IllegalArgumentException e) {

            return ApiErrorHandling.customApiError("Error In Test Overview. Check URL used for AV Request", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        catch (Exception e) {
//            System.out.println(e.getClass());
//            return ResponseEntity.internalServerError().body(e.getMessage());
           return ApiErrorHandling.genericApiError(e);
        }
    }

    //http://localhost:8080/api/overview/{symbol}
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getOverviewBySymbol (RestTemplate restTemplate, @PathVariable String symbol) {
        try{

            String apiKey = env.getProperty("AV_API_KEY");

            String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apiKey;

            System.out.println(url);

            String alphaVantageResponse = restTemplate.getForObject(url, String.class);

            if(alphaVantageResponse == null) {
                return ApiErrorHandling.customApiError("Did not receive response from AV", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            else if(alphaVantageResponse.equals("{}")) {
                return ApiErrorHandling.customApiError("Invalid Stock Symbol: " + symbol, HttpStatus.BAD_REQUEST);
            }

            return ResponseEntity.ok(alphaVantageResponse);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }


}
