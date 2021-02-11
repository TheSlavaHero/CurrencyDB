package com.company.database;

import java.lang.reflect.Field;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractDAO<K, T> {
    private final Connection conn;
    private final String table;

    public AbstractDAO(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    public void add(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();

            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for (Field f : fields) {
                f.setAccessible(true);
                if (f.get(t) != null) {
                    names.append(f.getName()).append(',');
                    if (f.getType().equals(Date.class)) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = simpleDateFormat.format(f.get(t));
                        values.append('"').append(dateStr).append("\",");
                    } else {
                        values.append('"').append(f.get(t)).append("\",");
                    }
                }
            }
            names.deleteCharAt(names.length() - 1); // last ','
            values.deleteCharAt(values.length() - 1); // last ','

            String sql = "INSERT INTO " + table + "(" + names.toString() +
                    ") VALUES(" + values.toString() + ")";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void update(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = null;

            for (Field f : fields) {
                if (f.isAnnotationPresent(Id.class)) {
                    id = f;
                    id.setAccessible(true);
                    break;
                }
            }
            if (id == null)
                throw new RuntimeException("No Id field");

            StringBuilder sb = new StringBuilder();

            for (Field f : fields) {
                if (f != id) {
                    f.setAccessible(true);

                    sb.append(f.getName())
                            .append(" = ")
                            .append('"')
                            .append(f.get(t))
                            .append('"')
                            .append(',');
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            String sql = "UPDATE " + table + " SET " + sb.toString() + " WHERE " +
                    id.getName() + " = \"" + id.get(t) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void delete(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = null;

            for (Field f : fields) {
                if (f.isAnnotationPresent(Id.class)) {
                    id = f;
                    id.setAccessible(true);
                    break;
                }
            }
            if (id == null)
                throw new RuntimeException("No Id field");

            String sql = "DELETE FROM " + table + " WHERE " + id.getName() +
                    " = \"" + id.get(t) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<T> getAll(Class<T> cls) {
        List<T> res = new ArrayList<>();

        try {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + table)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T t = cls.newInstance();

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);

                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);

                            field.set(t, rs.getObject(columnName));
                        }

                        res.add(t);
                    }
                }
            }

            return res;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<T> getAllEx(Class<T> cls, String... names) {
        List<T> res = new ArrayList<>();

        try {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + table)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T t = cls.newInstance();

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);
                            for (int j = 0; j < names.length; j++) {
                                if (names[j].equals(columnName)) {
                                    Field field = cls.getDeclaredField(columnName);
                                    field.setAccessible(true);

                                    field.set(t, rs.getObject(columnName));
                                }
                            }
                        }

                        res.add(t);
                    }
                }
            }

            return res;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void createTable(Class<T> cls) {

        Field[] fields = cls.getDeclaredFields();
//        String[] fieldNames = new String[fields.length];
        for (Field f : fields) {
            f.setAccessible(true);
        }

//        for (int i = 0;i < fields.length; i++) {
//            fieldNames[i] = fields[i].getName();
//        }

        String sqlQuery = "";
        StringBuilder sb = new StringBuilder(sqlQuery);

        Field id = null;
        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                id = f;
                id.setAccessible(true);
                break;
            }
        }

        sb.append("(");
        for (Field f : fields) {
            Class<?> type = f.getType();
            if (f.isAnnotationPresent(Id.class)) {
                sb.append(id.getName() + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");
            } else if (type.equals(int.class) && !f.isAnnotationPresent(Id.class)) {
                sb.append(" " + f.getName() + " INT DEFAULT NULL,");
            } else if (type.equals(String.class)) {
                sb.append(" " + f.getName() + " VARCHAR(128) DEFAULT NULL,");
            } else if (type.equals(double.class)) {
                sb.append(" " + f.getName() + " DOUBLE DEFAULT NULL,");
            } else if (type.equals(Date.class)) {
                sb.append(" " + f.getName() + " DATE DEFAULT NULL,");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");
        sqlQuery = sb.toString();
        sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1);
        int classNameInt = cls.getName().lastIndexOf(".") + 1;
        String className = cls.getName().substring(classNameInt);
        try (Statement st = conn.createStatement()) {
            String sqlQueryFinal = "CREATE TABLE " + className + sqlQuery;
            st.execute("DROP TABLE IF EXISTS " + className + ";");
            st.execute(sqlQueryFinal);//changed from st.executeQuery

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void init(Class<T> cls) {
        createTable(cls);
    }
}
