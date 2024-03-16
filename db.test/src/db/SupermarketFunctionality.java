package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class SupermarketFunctionality {

    private static final String url = "jdbc:mysql://localhost:3306/supermarket_database";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public SupermarketFunctionality() {
        // 在构造方法中进行数据库和表的检查和创建
        checkAndCreateDatabase();
        checkAndCreateTables();
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
                "id INT  PRIMARY KEY," +
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
                "id INT  PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "price DECIMAL(10, 2) NOT NULL," +
                "stock_quantity INT CHECK (stock_quantity >= 0)NOT NULL) ";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createProductTableQuery);
        }
    }

    // 创建商品采购表
    private static void createPurchaseTable(Connection connection) throws SQLException {
        String createPurchaseTableQuery = "CREATE TABLE purchase (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
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
                "product_id INT NOT NULL," +
                "quantity INT NOT NULL," +
                "sale_date DATE NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createSaleTableQuery);
        }
    }

    private void checkAndCreateDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USERNAME, PASSWORD)) {
            if (!databaseExists(connection, "supermarket_database")) {
                createDatabase(connection, "supermarket_database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkAndCreateTables() {

        try (Connection dbConnection = DriverManager.getConnection(url, USERNAME, PASSWORD)) {
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


            System.out.println("Database and tables setup completed successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void manageEmployeeInformation() {
        JFrame employeeFrame = new JFrame("员工信息管理");
        employeeFrame.setSize(300, 200);
        employeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.Y_AXIS));

        JTextField idField = new JTextField(15);
        JTextField employeeIdField = new JTextField(10);
        JTextField employeeNameField = new JTextField(20);
        JTextField contactNumberField = new JTextField(15);

        JButton saveUpdateButton = new JButton("保存/更新员工信息");
        saveUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int Id = Integer.parseInt(idField.getText());
                int employeeId = Integer.parseInt(employeeIdField.getText());
                String employeeName = employeeNameField.getText();
                String contactNumber = contactNumberField.getText();

                // 调用方法保存或更新员工信息到数据库
                saveOrUpdateEmployeeInformation(Id, employeeName, employeeId, contactNumber);

                // 清空输入框
                idField.setText("");
                employeeIdField.setText("");
                employeeNameField.setText("");
                contactNumberField.setText("");
            }
        });

        JButton deleteButton = new JButton("删除员工");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText());

                // 调用方法删除员工信息
                deleteEmployeeInformation(id);

                // 清空输入框
                employeeIdField.setText("");
                employeeNameField.setText("");
                contactNumberField.setText("");
                idField.setText("");
            }
        });



        employeePanel.add(new JLabel("ID:"));
        employeePanel.add(idField);
        employeePanel.add(new JLabel("员工名称:"));
        employeePanel.add(employeeNameField);
        employeePanel.add(new JLabel("员工ID:"));
        employeePanel.add(employeeIdField);

        employeePanel.add(new JLabel("联系方式:"));
        employeePanel.add(contactNumberField);
        employeePanel.add(saveUpdateButton);
        employeePanel.add(deleteButton);
        JButton displayAllEmployeesButton = new JButton("显示员工信息");
        displayAllEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用显示所有员工信息的方法
                displayAllEmployees();
            }
        });
        employeePanel.add(displayAllEmployeesButton);


        employeeFrame.add(employeePanel);
        employeeFrame.setVisible(true);
    }



    private void saveOrUpdateEmployeeInformation(int Id, String employeeName, int employeeId, String contactNumber) {
        // 修改 'employee_id', 'name', 'contact_number' 为正确的列名
        String query = "INSERT INTO employee (id, name,employee_id,contact_number) VALUES (?, ?, ?,?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name),employee_id=VALUES(employee_id), contact_number = VALUES(contact_number)";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, Id);
            preparedStatement.setString(2, employeeName);
            preparedStatement.setInt(3, employeeId);
            preparedStatement.setString(4, contactNumber);

            // 执行更新或插入
            preparedStatement.executeUpdate();

            System.out.println("员工信息保存/更新成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteEmployeeInformation(int employeeId) {
        String query = "DELETE FROM employee WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, employeeId);

            // 执行删除
            preparedStatement.executeUpdate();

            System.out.println("员工信息删除成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void displayAllEmployees() {
        try {
            // 连接数据库
            Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            // 查询并展示员工表所有数据
            displayTableData("employee", connection);

            // 关闭数据库连接
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常
        }
    }
    public void displayAllProduct() {
        try {
            // 连接数据库
            Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            // 查询并展示员工表所有数据
            displayTableData("product", connection);

            // 关闭数据库连接
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常
        }
    }
    public void displayAllPurchase() {
        try {
            // 连接数据库
            Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            // 查询并展示员工表所有数据
            displayTableData("purchase", connection);

            // 关闭数据库连接
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常
        }
    }
    public void displayAllSale() {
        try {
            // 连接数据库
            Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            // 查询并展示员工表所有数据
            displayTableData("sale", connection);

            // 关闭数据库连接
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常
        }
    }

    public void manageProductInformation() {
        JFrame productFrame = new JFrame("商品信息管理");
        productFrame.setSize(300, 200);
        productFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));

        JTextField productIdField = new JTextField(10); // 添加输入商品ID的文本框
        JTextField productNameField = new JTextField(20);
        JTextField priceField = new JTextField(10);
        JTextField stockQuantityField = new JTextField(15);

        JButton saveUpdateButton = new JButton("保存/更新商品信息");
        saveUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = Integer.parseInt(productIdField.getText());
                String productName = productNameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stockQuantity = Integer.parseInt(stockQuantityField.getText());

                // 调用方法保存或更新商品信息到数据库
                saveOrUpdateProductInformation(productId, productName, price, stockQuantity);

                // 清空输入框
                productIdField.setText("");
                productNameField.setText("");
                priceField.setText("");
                stockQuantityField.setText("");
            }
        });

        JButton deleteButton = new JButton("删除商品");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = Integer.parseInt(productIdField.getText());

                // 调用方法删除商品信息
                deleteProductInformation(productId);

                // 清空输入框
                productIdField.setText("");
                productNameField.setText("");
                priceField.setText("");
                stockQuantityField.setText("");
            }
        });


        productPanel.add(new JLabel("商品ID:")); // 添加标签
        productPanel.add(productIdField); // 添加文本框
        productPanel.add(new JLabel("商品名称:"));
        productPanel.add(productNameField);
        productPanel.add(new JLabel("价格:"));
        productPanel.add(priceField);
        productPanel.add(new JLabel("库存量:"));
        productPanel.add(stockQuantityField);
        productPanel.add(saveUpdateButton);
        productPanel.add(deleteButton);
        JButton displayAllProductButton = new JButton("显示商品信息");
        displayAllProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用显示所有员工信息的方法
                displayAllProduct();
            }
        });
        productPanel.add(displayAllProductButton);

        productFrame.add(productPanel);
        productFrame.setVisible(true);
    }



    private void saveOrUpdateProductInformation(int productId, String productName, double price, int stockQuantity) {
        // 修改 'product_id', 'name', 'price', 'stock_quantity' 为正确的列名
        String query = "INSERT INTO product (id, name, price, stock_quantity) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), price = VALUES(price), stock_quantity = VALUES(stock_quantity)";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            preparedStatement.setString(2, productName);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, stockQuantity);

            // 执行更新或插入
            preparedStatement.executeUpdate();

            System.out.println("商品信息保存/更新成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteProductInformation(int productId) {
        String query = "DELETE FROM product WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);

            // 执行删除
            preparedStatement.executeUpdate();

            System.out.println("商品信息删除成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void managePurchase() {
        // 实现商品采购管理的相关功能
        // ...
        // 在商品采购管理部分中，添加以下代码

// 导入需要的类

        JFrame purchaseFrame = new JFrame("商品采购管理");
        purchaseFrame.setSize(300, 200);
        purchaseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel purchasePanel = new JPanel();
        purchasePanel.setLayout(new BoxLayout(purchasePanel, BoxLayout.Y_AXIS));

        JTextField productIdField = new JTextField(10);
        JTextField purchaseQuantityField = new JTextField(10);

        JButton purchaseButton = new JButton("采购商品");
        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = Integer.parseInt(productIdField.getText());
                int purchaseQuantity = Integer.parseInt(purchaseQuantityField.getText());

                // 获取当前时间
                Date purchaseTime = new Date();

                // 调用方法进行商品采购
                purchaseProduct(productId, purchaseQuantity, purchaseTime);

                // 清空输入框
                productIdField.setText("");
                purchaseQuantityField.setText("");
            }
        });

        purchasePanel.add(new JLabel("商品ID:"));
        purchasePanel.add(productIdField);
        purchasePanel.add(new JLabel("采购数量:"));
        purchasePanel.add(purchaseQuantityField);
        purchasePanel.add(purchaseButton);
        JButton displayAllEmployeesButton = new JButton("显示采购信息");
        displayAllEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用显示所有员工信息的方法
                displayAllPurchase();
            }
        });
        purchasePanel.add(displayAllEmployeesButton);
        JButton displayAllProductButton = new JButton("显示商品信息");
        displayAllProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用显示所有员工信息的方法
                displayAllProduct();
            }
        });
        purchasePanel.add(displayAllProductButton);

        purchaseFrame.add(purchasePanel);
        purchaseFrame.setVisible(true);
    }

    // 商品采购方法
    private void purchaseProduct(int productId, int purchaseQuantity, Date purchaseTime) {
        // 修改 'product_id', 'quantity', 'purchase_time' 为正确的列名
        String query = "INSERT INTO purchase (product_id, quantity, purchase_date) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, purchaseQuantity);
            preparedStatement.setTimestamp(3, new Timestamp(purchaseTime.getTime()));

            // 执行商品采购
            preparedStatement.executeUpdate();

            // 自动更新库存信息
            updateProductStock(productId, purchaseQuantity);

            System.out.println("商品采购成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 更新商品库存方法
    private void updateProductStock(int productId, int purchaseQuantity) {
        // 修改 'product_id', 'stock' 为正确的列名
        String query = "UPDATE product SET stock_quantity = stock_quantity + ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, purchaseQuantity);
            preparedStatement.setInt(2, productId);

            // 执行更新库存
            preparedStatement.executeUpdate();

            System.out.println("库存信息更新成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ... 其他代码


    public void manageSale() {
        JFrame saleFrame = new JFrame("商品销售管理");
        saleFrame.setSize(300, 200);
        saleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel salePanel = new JPanel();
        salePanel.setLayout(new BoxLayout(salePanel, BoxLayout.Y_AXIS));

        JTextField productIdField = new JTextField(10);
        JTextField saleQuantityField = new JTextField(10);

        JButton saleButton = new JButton("销售商品");
        saleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = Integer.parseInt(productIdField.getText());
                int saleQuantity = Integer.parseInt(saleQuantityField.getText());

                // 获取当前时间
                Date saleTime = new Date();

                // 调用方法进行商品销售
                sellProduct(productId, saleQuantity, saleTime);

                // 清空输入框
                productIdField.setText("");
                saleQuantityField.setText("");
            }
        });

        salePanel.add(new JLabel("商品ID:"));
        salePanel.add(productIdField);
        salePanel.add(new JLabel("销售数量:"));
        salePanel.add(saleQuantityField);
        salePanel.add(saleButton);
        JButton displayAllEmployeesButton = new JButton("显示销售信息");
        displayAllEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用显示所有员工信息的方法
                displayAllSale();
            }
        });
        salePanel.add(displayAllEmployeesButton);
        JButton displayAllProductButton = new JButton("显示商品信息");
        displayAllProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用显示所有员工信息的方法
                displayAllProduct();
            }
        });
        salePanel.add(displayAllProductButton);

        saleFrame.add(salePanel);
        saleFrame.setVisible(true);
    }

    // 商品销售方法
    private void sellProduct(int productId, int saleQuantity, Date saleTime) {
        // 修改 'product_id', 'quantity', 'sale_time' 为正确的列名
        String query = "INSERT INTO sale (product_id, quantity, sale_date) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, saleQuantity);
            preparedStatement.setTimestamp(3, new Timestamp(saleTime.getTime()));

            // 执行商品销售
            preparedStatement.executeUpdate();

            // 自动更新库存信息
            updateProductStock2(productId, -saleQuantity); // 销售时库存减少


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 更新商品库存方法
    private void updateProductStock2(int productId, int saleQuantity) {
        // 修改 'product_id', 'stock' 为正确的列名
        String query = "UPDATE product SET stock_quantity = stock_quantity + ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, saleQuantity);
            preparedStatement.setInt(2, productId);

            // 执行更新库存
            preparedStatement.executeUpdate();

            System.out.println("库存信息更新成功！");
            System.out.println("商品销售成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("库存不足");

        }
        System.out.println("商品销售管理功能执行成功！");
    }

    public void displayTableData(String tableName, Connection connection) {
        String query;

        // 根据表名选择相应的查询语句
        switch (tableName) {
            case "employee":
                query = "SELECT * FROM employee";
                break;
            case "product":
                query = "SELECT * FROM product";
                break;
            case "purchase":
                query = "SELECT * FROM purchase";
                break;
            case "sale":
                query = "SELECT * FROM sale";
                break;
            default:
                System.out.println("Invalid table name");
                return;
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // 获取结果集的元数据，用于获取列名和数据类型
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();

            // 创建一个表格模型
            DefaultTableModel tableModel = new DefaultTableModel();

            // 添加列名到表格模型
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // 处理查询结果，添加行数据到表格模型
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                // 使用元数据获取列名和数据类型
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                // 添加行数据到表格模型
                tableModel.addRow(rowData);
            }

            // 创建一个表格并设置数据模型
            JTable table = new JTable(tableModel);

            // 创建一个滚动面板，如果表格行数较多，可以滚动查看
            JScrollPane scrollPane = new JScrollPane(table);

            // 创建一个新的窗口来展示表格
            JFrame tableFrame = new JFrame(tableName + " 表格数据");
            tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            tableFrame.getContentPane().add(scrollPane);
            tableFrame.pack();
            tableFrame.setLocationRelativeTo(null); // 居中显示
            tableFrame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常
        }
    }
}


