package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

public class CurrencyData {
    private String date;
    private String bank;
    private int baseCurrency;
    private String baseCurrencyLit;
    private Currency[] exchangeRate;

    public CurrencyData(String date, String bank, int baseCurrency, String baseCurrencyLit, Currency[] exchangeRate) {
        this.date = date;
        this.bank = bank;
        this.baseCurrency = baseCurrency;
        this.baseCurrencyLit = baseCurrencyLit;
        this.exchangeRate = exchangeRate;
    }

    public CurrencyData() {
        super();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(int baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getBaseCurrencyLit() {
        return baseCurrencyLit;
    }

    public void setBaseCurrencyLit(String baseCurrencyLit) {
        this.baseCurrencyLit = baseCurrencyLit;
    }

    public Currency[] getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Currency[] exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return "CurrencyData{" +
                "date=" + date +
                ", bank='" + bank + '\'' +
                ", baseCurrency=" + baseCurrency +
                ", baseCurrencyLit='" + baseCurrencyLit + '\'' +
                ", exchangeRate=" + Arrays.toString(exchangeRate) +
                '}';
    }

    public static CurrencyData fromJson (String json) {
        Gson gson = new GsonBuilder().create();
        CurrencyData currencyData = gson.fromJson(json, CurrencyData.class);
        return currencyData;
    }


}
