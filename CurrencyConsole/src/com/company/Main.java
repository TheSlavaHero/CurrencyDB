package com.company;

import com.company.database.CurrencyDAO;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static final long day = 86400000;
    public static final long currentTime = System.currentTimeMillis();
    public static void main(String[] args) {
        CurrencyDAO currencyDAO = new CurrencyDAO(WebConnection.dbConnection(), "currency");
        try {
            updateDB(currencyDAO);
        } catch (IOException | IllegalAccessException | ParseException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        
        System.out.println("Done!");


    }

    public static void updateDB(CurrencyDAO currencyDAO) throws IOException, IllegalAccessException, ParseException, NoSuchFieldException {
        currencyDAO.createTable(Currency.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        for (long i = 0; i < day * 30; i+=day) {
            Date date = new Date(currentTime - i);
            String json = WebConnection.getJsonfromPB(date);
            CurrencyData currencyData = CurrencyData.fromJson(json);
            Currency[] curArr = currencyData.getExchangeRate();
            for (Currency currency : curArr) {
                Date date2 = simpleDateFormat.parse(currencyData.getDate());
                currency.setDate(date2);
                System.out.println(currency);
                    try {
//                        if (currency.getCurrency().equals("USD")) {
                            currencyDAO.add(currency);
//                            System.out.println("Added");
//                        }
                    } catch (NullPointerException e) {
                        //Catch 'em all!
                    }

            }


        }

    }
}
