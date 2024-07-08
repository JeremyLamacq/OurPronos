package com.lamacqdev.ourpronos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;


public class Prono {

    private static HikariDataSource dataSource;

    public static void main(String[] Args) throws SQLException {

        try {
            initDatabaseConnectionPool();
            deleteData("%");
            readData();
            createData("jeremy","lamacq","dev@my","jojo");
            createData("cindy","gerard","wife@my","juju");
            readData();
            updateData("jeremy","dev@foryou");
            readData();      
            deleteData("jeremy");
            readData();      
    
        } finally {
            closeDatabaseConnectionPool();
        }
    }

    private static void initDatabaseConnectionPool() {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mariadb://localhost:3306/our_pronos");
        dataSource.setUsername("lamacqdev");
        dataSource.setPassword("mymdpdev44");
    }

    private static void closeDatabaseConnectionPool() {
        dataSource.close();
    }

    private static void createData(String firstname, String lastname, String email, String password) throws SQLException {
        System.out.println("Creating data...");
        int rowsInserted;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO user(firstname, lastname, email, password)
                    VALUES (?, ?, ?, ?)
                    """)) {
                statement.setString(1, firstname);
                statement.setString(2, lastname);
                statement.setString(3, email);
                statement.setString(4, password);
                rowsInserted = statement.executeUpdate();
                statement.close();
            }
        }
        System.out.println("Rows inserted: " + rowsInserted);
    }
    
    private static void readData() throws SQLException {
        System.out.println("Reading data...");
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                SELECT firstname, lastname, email, password
                FROM user
            """)) {
                ResultSet resultSet = statement.executeQuery();
                boolean empty = true;
                while(resultSet.next()) {
                    String firstname = resultSet.getString(1);
                    String lastname = resultSet.getString(2);
                    String email = resultSet.getString(3);
                    String password = resultSet.getString(4);
                    System.out.println("\t>" + firstname + " " + lastname + " by " + email + " " + password);
                    empty = false;
                }
                if(empty) {
                    System.out.println("\t (no data)");
                }
            }
        }
    }
    
    private static void updateData(String firstname, String newEmail) throws SQLException {
        System.out.println("Updating data...");
        int rowsUpdated;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE user
                SET email = ?
                WHERE firstname = ?
            """)) {
                statement.setString(1, newEmail);
                statement.setString(2, firstname);
                rowsUpdated = statement.executeUpdate();
                System.out.println("Rows updated: " + rowsUpdated);
            }
        }
    }
    
    private static void deleteData(String nameExpression) throws SQLException {
        System.out.println("Deleting data...");
        int rowsDeleted;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM user
                WHERE firstname LIKE ?
            """)) {
                statement.setString(1, nameExpression);
                rowsDeleted = statement.executeUpdate();
                System.out.println("Rows deleted: " + rowsDeleted);
            }
        }
    }
}
