package me.tinytank800.tinylives.utilities;

import me.tinytank800.tinylives.tinylives;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    private String host = "localhost";
    private String port = "3306";
    private String database = "test";
    private String user = "root";
    private String pass = "";

    private Connection connection;

    public void setup(){
        host = tinylives.getInstance().getConfig().getString("MySQL.host");
        port = tinylives.getInstance().getConfig().getString("MySQL.port");
        database = tinylives.getInstance().getConfig().getString("MySQL.database");
        user = tinylives.getInstance().getConfig().getString("MySQL.username");
        pass = tinylives.getInstance().getConfig().getString("MySQL.password");
    }

    public boolean isConnected() {
        return (connection == null ? false : true);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if(!isConnected()){
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            host + ":" + port + "/" + database + "?useSSL=false",
                    user, pass);
        }
    }

    public void disconnect(){
        if(isConnected()){
            try{
                connection.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection(){
        return connection;
    }

}
