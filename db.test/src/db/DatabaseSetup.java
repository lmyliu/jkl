package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void main(String[] args) {
        // JDBC连接数据库的URL
        String url = "jdbc:mysql://localhost:3306/";
        // 数据库用户名和密码
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // 检查数据库是否存在
            if (!databaseExists(connection, "supermarket_database")) {
                createDatabase(connection, "supermarket_database");
            }

            // 连接到数据库
            url += "supermarket_database";
            try (Connection dbConnection = DriverManager.getConnection(url, username, password)) {
                // 检查表是否存在，如果不存在则创建
                if (!tableExists(dbConnection, "employee")) {
                    createEmployeeTable(dbConnection);
                }

                if (!tableExists(dbConnection, "product")) {
                    createProductTable(dbConnection);
                }

                if (!tableExists(dbConnection, "purchase")) {
                    createPurchaseTable(dbConnection);
                }

                if (!tableExists(dbConnection, "sale")) {
                    createSaleTable(dbConnection);
                }
            }

            System.out.println("Database and tables setup completed successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 检查数据库是否存在
    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getCatalogs();
        while (resultSet.next()) {
            if (resultSet.getString(1).equals(databaseName)) {
                return true;
            }
        }
        return false;
    }

    // 创建数据库
    private static void createDatabase(Connection connection, String databaseName) throws SQLException {
        String createDatabaseQuery = "CREATE DATABASE " + databaseName;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createDatabaseQuery);
        }
    }

    // 检查表是否存在
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null);
        return resultSet.next();
    }

    // 创建员工信息表
    private static void createEmployeeTable(Connection connection) throws SQLException {
        String createEmployeeTableQuery = "CREATE TABLE employee (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "employee_id INT NOT NULL," +
                "contact_number VARCHAR(20) NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createEmployeeTableQuery);
        }
    }

    // 创建商品信息表
    private static void createProductTable(Connection connection) throws SQLException {
        String createProductTableQuery = "CREATE TABLE product (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "price DECIMAL(10, 2) NOT NULL," +
                "stock_quantity INT NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createProductTableQuery);
        }
    }

    // 创建商品采购表
    private static void createPurchaseTable(Connection connection) throws SQLException {
        String createPurchaseTableQuery = "CREATE TABLE purchase (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "purchase_order_number VARCHAR(20) NOT NULL," +
                "product_id INT NOT NULL," +
                "quantity INT NOT NULL," +
                "purchase_date DATE NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createPurchaseTableQuery);
        }
    }

    // 创建商品销售表
    private static void createSaleTable(Connection connection) throws SQLException {
        String createSaleTableQuery = "CREATE TABLE sale (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "sale_order_number VARCHAR(20) NOT NULL," +
                "product_id INT NOT NULL," +
                "quantity INT NOT NULL," +
                "sale_date DATE NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createSaleTableQuery);
        }
    }
}