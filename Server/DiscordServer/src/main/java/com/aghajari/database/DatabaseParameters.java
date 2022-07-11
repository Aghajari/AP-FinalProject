package com.aghajari.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

public class DatabaseParameters {

    private final LinkedHashMap<String, Parameter> parameters = new LinkedHashMap<>();

    public void putString(String name, String value) {
        parameters.put(name, new Parameter(String.class, value));
    }

    public void putTimestamp(String name, long value) {
        parameters.put(name, new Parameter(Timestamp.class, new Timestamp(value)));
    }

    public void putBoolean(String name, boolean value) {
        parameters.put(name, new Parameter(boolean.class, value));
    }

    public void putInt(String name, int value) {
        parameters.put(name, new Parameter(int.class, value));
    }

    public String getQuery(boolean includeNames, boolean includeMarker) {
        StringBuilder query = new StringBuilder();
        for (String name : parameters.keySet()) {
            if (includeNames)
                query.append(name);
            if (includeNames && includeMarker)
                query.append('=');
            if (includeMarker)
                query.append('?');
            query.append(',');
        }

        return query.substring(0, query.length() - 1);
    }

    public String getWhereQuery() {
        StringBuilder query = new StringBuilder();
        for (String name : parameters.keySet()) {
            query.append(name);
            query.append("=? and ");
        }
        return query.substring(0, query.length() - 4);
    }

    public int size() {
        return parameters.size();
    }

    public void update(PreparedStatement statement) throws SQLException {
        update(1, statement);
    }

    public void update(int start, PreparedStatement statement) throws SQLException {
        int index = start;
        for (Parameter parameter : parameters.values()) {
            if (parameter.type == String.class) {
                statement.setString(index, (String) parameter.value);
            } else if (parameter.type == boolean.class) {
                statement.setBoolean(index, (boolean) parameter.value);
            } else if (parameter.type == int.class) {
                statement.setInt(index, (int) parameter.value);
            } else if (parameter.type == Timestamp.class) {
                statement.setTimestamp(index, (Timestamp) parameter.value);
            }
            index++;
        }
    }

    private static class Parameter {
        private final Class<?> type;
        private final Object value;

        private Parameter(Class<?> type, Object value) {
            this.type = type;
            this.value = value;
        }
    }

}
