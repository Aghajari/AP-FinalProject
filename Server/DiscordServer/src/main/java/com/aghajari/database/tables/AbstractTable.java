package com.aghajari.database.tables;

import com.aghajari.database.DatabaseParameters;

import java.sql.*;

public abstract class AbstractTable {

    final Object lock = new Object();

    private final String table;

    protected AbstractTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    public Connection getConnection() {
        return com.aghajari.database.Connection.getInstance().getConnection();
    }

    protected void INSERT(DatabaseParameters parameters) throws SQLException {
        INSERT(parameters, null, null);
    }

    protected void INSERT(DatabaseParameters parameters, String[] clmn, ExecuteQuery run) throws SQLException {
        String sql = "INSERT INTO " + table + "(" +
                parameters.getQuery(true, false) +
                ") VALUES (" + parameters.getQuery(false, true) + ")";

        PreparedStatement stmt = run == null ?
                getConnection().prepareStatement(sql)
                : getConnection().prepareStatement(sql, clmn);
        parameters.update(stmt);
        stmt.executeUpdate();
        if (run != null)
            run.apply(stmt.getGeneratedKeys());
        stmt.close();
    }

    protected void UPDATE(DatabaseParameters setParameters,
                          DatabaseParameters whereParameters) throws SQLException {
        UPDATE(setParameters, whereParameters, null);
    }

    protected void UPDATE(DatabaseParameters setParameters,
                          DatabaseParameters whereParameters, String whereQuery) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("UPDATE " + table + " SET " +
                setParameters.getQuery(true, true) +
                (whereParameters != null ? " WHERE " + (whereQuery == null ? whereParameters.getWhereQuery() : whereQuery) : ""));
        setParameters.update(stmt);
        if (whereParameters != null)
            whereParameters.update(setParameters.size() + 1, stmt);
        stmt.executeUpdate();
        stmt.close();
    }

    protected void DELETE(DatabaseParameters whereParameters) throws SQLException {
        DELETE(whereParameters, null);
    }

    protected void DELETE(DatabaseParameters whereParameters, String whereQuery) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM " + table +
                (whereParameters != null ? " WHERE " + (whereQuery == null ? whereParameters.getWhereQuery() : whereQuery) : ""));
        if (whereParameters != null)
            whereParameters.update(stmt);
        stmt.executeUpdate();
        stmt.close();
    }

    protected void SELECT(DatabaseParameters whereParameters, ExecuteQuery executor) throws SQLException {
        SELECT("*", whereParameters, null, executor);
    }

    protected void SELECT(String selection, DatabaseParameters whereParameters, ExecuteQuery executor) throws SQLException {
        SELECT(selection, whereParameters, null, executor);
    }

    protected void SELECT(String selection,
                          DatabaseParameters whereParameters, String whereQuery,
                          ExecuteQuery executor) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("SELECT " + selection + " FROM " + table +
                (whereParameters != null ? " WHERE " + (whereQuery == null ? whereParameters.getWhereQuery() : whereQuery) : ""));
        if (whereParameters != null)
            whereParameters.update(stmt);
        executor.apply(stmt.executeQuery());
        stmt.close();
    }

    public interface ExecuteQuery {
        void apply(ResultSet set) throws SQLException;
    }
}
