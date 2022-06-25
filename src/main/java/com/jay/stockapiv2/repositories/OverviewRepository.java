package com.jay.stockapiv2.repositories;

import com.jay.stockapiv2.models.Overview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OverviewRepository extends CrudRepository<Overview, Long> {

    Optional<Overview> findBySymbol(String symbol);

    List<Overview> findByExchange(String exchange);


}
