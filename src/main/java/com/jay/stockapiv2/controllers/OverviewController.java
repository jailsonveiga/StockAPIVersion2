package com.jay.stockapiv2.controllers;

import com.jay.stockapiv2.models.Overview;
import com.jay.stockapiv2.repositories.OverviewRepository;
import com.jay.stockapiv2.utils.ApiErrorHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    @Autowired
    private Environment env;

    @Autowired
    private OverviewRepository overviewRepository;

    private final String BASE_URL = "https://www.alphavantage.co/query?function=OVERVIEW";

    //http://localhost:8080/api/overview/test
    @GetMapping("/test")
    private ResponseEntity<?> testOverview (RestTemplate restTemplate) {
        try{

            String url = BASE_URL + "&symbol=IBM&apikey=demo";

            Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);

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

    @PostMapping("/test")
    private ResponseEntity<?> testUploadOverview (RestTemplate restTemplate) {
        try{

            String url = BASE_URL + "&symbol=IBM&apikey=demo";

            Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);

            if(alphaVantageResponse == null) {
                return ApiErrorHandling.customApiError("Did not receive response from AV", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            else if(alphaVantageResponse.getSymbol() == null) {
                return ApiErrorHandling.customApiError("No Data Retrieved From AV ", HttpStatus.NOT_FOUND);
            }

            Overview savedOverview = overviewRepository.save(alphaVantageResponse);

            return ResponseEntity.ok(savedOverview);

        } catch(DataIntegrityViolationException e) {

            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        catch (IllegalArgumentException e) {

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

            Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);

            if(alphaVantageResponse == null) {
                return ApiErrorHandling.customApiError("Did not receive response from AV", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            else if(alphaVantageResponse.getSymbol() == null) {
                return ApiErrorHandling.customApiError("Invalid Stock Symbol: " + symbol, HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(alphaVantageResponse);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @PostMapping("/{symbol}")
    public ResponseEntity<?> uploadOverviewBySymbol (RestTemplate restTemplate, @PathVariable String symbol) {
        try{

            String apiKey = env.getProperty("AV_API_KEY");

            String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apiKey;

            Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);

            if(alphaVantageResponse == null) {
                return ApiErrorHandling.customApiError("Did not receive response from AV", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            else if(alphaVantageResponse.getSymbol() == null) {
                return ApiErrorHandling.customApiError("Invalid Stock Symbol: " + symbol, HttpStatus.NOT_FOUND);
            }

            Overview savedOverview = overviewRepository.save(alphaVantageResponse);

            return ResponseEntity.ok(savedOverview );


        }catch(DataIntegrityViolationException e) {

            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }


}
