package com.jay.stockapiv2.controllers;

import com.jay.stockapiv2.models.Overview;
import com.jay.stockapiv2.repositories.OverviewRepository;
import com.jay.stockapiv2.utils.ApiErrorHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

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

            return ApiErrorHandling.customApiError("Error In Test Overview. Check URL used for AV Request", 500);

        }
        catch (Exception e) {

           return ApiErrorHandling.genericApiError(e);

        }
    }

    @PostMapping("/test")
    private ResponseEntity<?> testUploadOverview (RestTemplate restTemplate) {
        try{

            String url = BASE_URL + "&symbol=IBM&apikey=demo";

            Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);

            if(alphaVantageResponse == null) {

                ApiErrorHandling.throwErr(500, "Did not receive response from AV");

            }
            else if(alphaVantageResponse.getSymbol() == null) {
                ApiErrorHandling.throwErr(400, "No Data Retrieved From AV ");
            }

            Overview savedOverview = overviewRepository.save(alphaVantageResponse);

            return ResponseEntity.ok(savedOverview);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch
        (DataIntegrityViolationException e) {

            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data", 400);

        }
        catch (IllegalArgumentException e) {

            return ApiErrorHandling.customApiError("Error In Test Overview. Check URL used for AV Request", 500);

        }
        catch (Exception e) {

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
                ApiErrorHandling.throwErr(500, "Did not receive response from AV");
            }
            else if(alphaVantageResponse.getSymbol() == null) {

                ApiErrorHandling.throwErr(404, "Invalid Stock Symbol: " + symbol);

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
                ApiErrorHandling.throwErr(500, "Did not receive response from AV");
            }
            else if(alphaVantageResponse.getSymbol() == null) {
                ApiErrorHandling.throwErr(404,"Invalid Stock Symbol: " + symbol);
            }

            Overview savedOverview = overviewRepository.save(alphaVantageResponse);

            return ResponseEntity.ok(savedOverview );


        }catch(DataIntegrityViolationException e) {

            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data", 500);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //Get All Overviews From SQL Database

    @GetMapping("/all")
    private ResponseEntity<?> getAllOverviews () {
       try {

           Iterable<Overview> allOverviews = overviewRepository.findAll();

           return ResponseEntity.ok(allOverviews);

       } catch (Exception e) {
           return ApiErrorHandling.genericApiError(e);
       }
    }

    //Get One Overview By ID From SQL Database Return Found Overview Or 404 If Not Found

    @GetMapping("/id/{id}")
    private ResponseEntity<?> getOverviewById (@PathVariable String id) {
        try {

            Optional<Overview> foundOverview = overviewRepository.findById(Long.parseLong(id));

            if (foundOverview.isEmpty()) {

                ApiErrorHandling.throwErr(404, id + " did not match any overview");

            }

            return ResponseEntity.ok(foundOverview);

        } catch (NumberFormatException e) {

            return ApiErrorHandling.customApiError("ID Must be a number: " + id, 400);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @GetMapping("/symbol/{symbol}")
    private ResponseEntity<?> getOverviewBySymbol (@PathVariable String symbol) {
        try {

            Optional<Overview> foundOverview = overviewRepository.findBySymbol(symbol);

            if (foundOverview.isEmpty()) {

                ApiErrorHandling.throwErr(404, symbol + " did not match any overview");

            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @GetMapping("/exchange/{exchange}")
    private ResponseEntity<?> getOverviewByExchange (@PathVariable String exchange) {
        try {

            List<Overview> foundOverview = overviewRepository.findByExchange(exchange);

            if (foundOverview.isEmpty()) {

                ApiErrorHandling.throwErr(404, exchange + " did not match any overview");

            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }




    //Delete All Overviews From SQL Database Return # of Delete Overviews
    @DeleteMapping("/all")
    private ResponseEntity<?> deleteAllOverviews () {
        try {

            long allOverviewsCount = overviewRepository.count();

            if(allOverviewsCount == 0){

                return ResponseEntity.ok("No Overviews to Delete");

            }

            overviewRepository.deleteAll();

            return ResponseEntity.ok("Deleted Overviews: " + allOverviewsCount);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    //Delete One Overview by ID from SQL database return deleted overview or 404 if not found
    @DeleteMapping("/id/{id}")
    private ResponseEntity<?> deleteById (@PathVariable String id) {
        try {

            long overviewId = Long.parseLong(id);

            Optional<Overview> foundOverview = overviewRepository.findById(overviewId);

            if (foundOverview.isEmpty()) {

                ApiErrorHandling.throwErr(404, id + " did not match any overview");

            }

            overviewRepository.deleteById(overviewId);

            return ResponseEntity.ok(foundOverview);

        } catch (NumberFormatException e) {

            return ApiErrorHandling.customApiError("ID Must be a number: " + id, 400);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }
}
