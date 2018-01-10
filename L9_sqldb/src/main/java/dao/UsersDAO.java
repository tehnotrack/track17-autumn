package dao;

import dataSets.UsersDataSet;
import executor.TExecutor;

import java.sql.Connection;
import java.sql.SQLException;

public class UsersDAO {

    private Connection con;

    public UsersDAO(Connection con) {
        this.con = con;
    }

    public UsersDataSet get(long id) throws SQLException {
        TExecutor exec = new TExecutor();
        return exec.execQuery(con, "select * from users where id=" + id, result -> {
            result.next();
            return new UsersDataSet(result.getLong(1), result.getString(2));
        });
    }
}
