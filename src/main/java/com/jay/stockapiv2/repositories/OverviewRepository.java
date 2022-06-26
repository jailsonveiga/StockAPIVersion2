package com.jay.stockapiv2.repositories;

import com.jay.stockapiv2.models.Overview;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface OverviewRepository extends JpaRepository<Overview, Long> {

    List<Overview> findById(long id);
    List<Overview> findBySymbol(String symbol);
    List<Overview> findBySector(String sector);
    List<Overview> findByName(String name);
    List<Overview> findByCurrency(String currency);
    List<Overview> findByCountry(String country);
    List<Overview> findByExchange(String exchange);

    List<Overview> findByMarketCapGreaterThanEqual(long marketCap);
    List<Overview> findByMarketCapLessThanEqual(long marketCap);

    List<Overview> deleteById(long id);
    List<Overview> deleteBySymbol(String symbol);
    List<Overview> deleteBySector(String sector);
    List<Overview> deleteByName(String name);
    List<Overview> deleteByCurrency(String currency);
    List<Overview> deleteByCountry(String country);
    List<Overview> deleteByExchange(String exchange);

    List<Overview> deleteByMarketCapGreaterThanEqual(long marketCap);
    List<Overview> deleteByMarketCapLessThanEqual(long marketCap);

}
