/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t12311m0.shoes_store;

/**
 *
 * @author admin
 */

public class Customer {
    private int customerId;
    private String customerName;
    private String phone;
    private String email;
    private String gender; // Thêm trường gender

    public Customer(int customerId, String customerName, String phone, String email, String gender) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.gender = gender; // Khởi tạo gender
    }

    // Getters và Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; } // Getter cho gender
    public void setGender(String gender) { this.gender = gender; } // Setter cho gender
}


