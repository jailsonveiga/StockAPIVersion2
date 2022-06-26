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

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/{field}/{value}")
    private ResponseEntity<?> getOverviewByField (@PathVariable String field, @PathVariable String value) {
        try {

            List<Overview> foundOverview = null;

            field = field.toLowerCase();

            switch (field) {
                case "id" -> foundOverview = overviewRepository.findById(Long.parseLong(value));
                case "symbol" -> foundOverview = overviewRepository.findBySymbol(value);
                case "sector" -> foundOverview = overviewRepository.findBySector(value);
                case "name" -> foundOverview = overviewRepository.findByName(value);
                case "currency" -> foundOverview = overviewRepository.findByCurrency(value);
                case "country" -> foundOverview = overviewRepository.findByCountry(value);
                case "exchange" -> foundOverview = overviewRepository.findByExchange(value);
                case "marketcapgte" -> foundOverview = overviewRepository.findByMarketCapGreaterThanEqual(Long.parseLong(value));
                case "marketcaplte" -> foundOverview = overviewRepository.findByMarketCapLessThanEqual(Long.parseLong(value));
            };

            if (foundOverview == null || foundOverview.isEmpty()) {

                ApiErrorHandling.throwErr(404, field + " did not match any overview");

            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (NumberFormatException e){

            return ApiErrorHandling.customApiError("ID must be a number: " + field, 400);

        }catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @DeleteMapping("/{field}/{value}")
    private ResponseEntity<?> deleteOverviewByField (@PathVariable String field, @PathVariable String value) {
        try {

            List<Overview> foundOverview = null;

            field = field.toLowerCase();

            switch (field) {
                case "id" -> foundOverview = overviewRepository.deleteById(Long.parseLong(value));
                case "symbol" -> foundOverview = overviewRepository.deleteBySymbol(value);
                case "sector" -> foundOverview = overviewRepository.deleteBySector(value);
                case "name" -> foundOverview = overviewRepository.deleteByName(value);
                case "currency" -> foundOverview = overviewRepository.deleteByCurrency(value);
                case "country" -> foundOverview = overviewRepository.deleteByCountry(value);
                case "exchange" -> foundOverview = overviewRepository.deleteByExchange(value);
                case "marketcapgte" -> foundOverview = overviewRepository.deleteByMarketCapGreaterThanEqual(Long.parseLong(value));
                case "marketcaplte" -> foundOverview = overviewRepository.deleteByMarketCapLessThanEqual(Long.parseLong(value));
            };

            if (foundOverview == null || foundOverview.isEmpty()) {

                ApiErrorHandling.throwErr(404, field + " did not match any overview");

            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (NumberFormatException e){

            return ApiErrorHandling.customApiError("ID must be a number: " + field, 400);

        }catch (Exception e) {
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

            List<Overview> foundOverview = overviewRepository.findById(overviewId);

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

    @PostMapping("/all")
    private ResponseEntity<?> uploadTestOverviews (RestTemplate restTemplate) {
        try{

            String[] testSymbols = {"AAPL", "IBM", "TM", "ALL", "GS"};

            ArrayList<Overview> overviews = new ArrayList<>();

            for (int i = 0; i < testSymbols.length; i++) {

                String symbol = testSymbols[i];

                String apiKey = env.getProperty("AV_API_KEY");

                String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apiKey;

                Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);

                if (alphaVantageResponse == null) {
                    ApiErrorHandling.throwErr(500, "Did not receive response from AV");
                } else if (alphaVantageResponse.getSymbol() == null) {
                    ApiErrorHandling.throwErr(404, "Invalid Stock Symbol: " + symbol);
                }

                overviews.add(alphaVantageResponse);
            }

            Iterable<Overview> savedOverview = overviewRepository.saveAll(overviews);

            return ResponseEntity.ok(savedOverview );


        }catch(DataIntegrityViolationException e) {

            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data", 500);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

}
