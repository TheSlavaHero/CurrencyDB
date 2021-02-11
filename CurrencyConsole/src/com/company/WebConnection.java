package com.company;

import com.company.database.ConnectionFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class WebConnection {
    private static final String url = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/CurrencyDB?serverTimezone=Europe/Kiev";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public static String getJsonfromPB(Date date) throws IOException {
        String json = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = simpleDateFormat.format(date);
        Scanner sc = new Scanner(new URL(url + dateStr).openStream());//date in format dd.MM.yyyy from year 2014.
        if (sc.hasNextLine()) {
            json = json + sc.nextLine();
        }
        return json;
    }

    public static Connection dbConnection() {
        ConnectionFactory factory = new ConnectionFactory(DB_CONNECTION, DB_USER, DB_PASSWORD);
        Connection conn = factory.getConnection();
        return conn;
    }
}
