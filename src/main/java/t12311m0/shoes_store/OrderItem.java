/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t12311m0.shoes_store;

/**
 *
 * @author admin
 */
public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int productId; // Thêm productId để lưu mã sản phẩm
    private String productName;
    private int quantity;
    private double price;

    // Constructor
    public OrderItem(int orderItemId, int orderId, int productId, String productName, int quantity, double price) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId; // Gán productId
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() { // Getter cho productId
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    // Getter for total price
    public double getTotalPrice() {
        return price * quantity;
    }
}

