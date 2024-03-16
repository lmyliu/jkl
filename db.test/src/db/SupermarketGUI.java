package db;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SupermarketGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("超市管理系统");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();

            JButton employeeButton = new JButton("员工信息管理");
            JButton productButton = new JButton("商品信息管理 ");
            JButton purchaseButton = new JButton("商品采购管理");
            JButton saleButton = new JButton("商品销售管理");
            // 新增按钮
            JButton showAllTablesButton = new JButton("展示所有表格内容");

            panel.add(employeeButton);
            panel.add(productButton);
            panel.add(purchaseButton);
            panel.add(saleButton);
            // 将新增按钮添加到界面


            frame.add(panel);

            SupermarketFunctionality functionality = new SupermarketFunctionality();

            employeeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    functionality.manageEmployeeInformation();
                }
            });

            productButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    functionality.manageProductInformation();
                }
            });

            purchaseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    functionality.managePurchase();
                }
            });

            saleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    functionality.manageSale();
                }
            });

            // 设置展示所有表格内容按钮的点击事件


            frame.setVisible(true);
        });
    }
}