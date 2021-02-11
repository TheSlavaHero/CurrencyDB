package com.company.database;

import com.company.Currency;

import java.sql.Connection;

public class CurrencyDAO extends AbstractDAO<Integer, Currency> {

    public CurrencyDAO(Connection conn, String table) {
        super(conn, table);
    }
}
