package t12311m0.shoes_store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Employee {

    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String status;

    public Employee(int id, String name, String email, String password, String phone, String gender,String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        setGender(gender);
        setStatus(status);
        // Use the setter to enforce validation
    }

    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     */
 

    // Getter and setter with validation for gender
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female")) {
            this.gender = gender;
        } else {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'");
        }
    }
    
      public void setStatus(String status) {
    if (status.equalsIgnoreCase("able") || status.equalsIgnoreCase("disable")) {
        this.status = status;
    } else {
        throw new IllegalArgumentException("Status must be 'ABLE' or 'DISABLE'");
    }
}


    // Other getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", phone=" + phone + ", gender=" + gender + '}';
    }

    public boolean saveToDatabase() {
        try (Connection conn = ConnectDB.connectDB()) {
            String sql = "INSERT INTO employees (employee_name, email, password, phone, gender) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            stmt.setString(5, gender); // Insert gender

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
