package com.jay.stockapiv2.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Overview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false, unique = true)
    private long Id;

    @JsonProperty("Symbol")
    @Column(name="symbol", nullable = false, unique = true)
    private String symbol;

    @JsonProperty("AssetType")
    @Column(name="asset_type", nullable = false)
    private String assetType;

    @JsonProperty("Name")
    @Column(name="name", nullable = false, unique = true)
    private String name;

    @JsonProperty("Exchange")
    @Column(name="exchange", nullable = false)
    private String exchange;

    @JsonProperty("Currency")
    @Column(name="currency", nullable = false)
    private String currency;

    @JsonProperty("Country")
    @Column(name="country", nullable = false)
    private String country;

    @JsonProperty("Sector")
    @Column(name="sector", nullable = false)
    private String sector;

    @JsonProperty("Industry")
    @Column(name="industry", nullable = false)
    private long industry;

    @JsonProperty("MarketCapitalization")
    @Column(name="market_cap", nullable = false)
    private long marketCap;

    @JsonProperty("52WeekHigh")
    @Column(name="year_high", nullable = false)
    private float yearHigh;

    @JsonProperty("52WeekLow")
    @Column(name="year_low", nullable = false)
    private float yearLow;

    @JsonProperty("DividendDate")
    @Column(name="dividend_date", nullable = false)
    private String dividendDate;

}
