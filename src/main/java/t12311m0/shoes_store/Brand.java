package t12311m0.shoes_store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Brand {
    private int brandId;
    private String brandName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int productCount;

    // Constructor với đầy đủ thông tin
    public Brand(int brandId, String brandName, Timestamp createdAt, Timestamp updatedAt, int productCount) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.productCount = productCount;
    }

    // Constructor ngắn gọn
    public Brand(int brandId, String brandName) {
        this(brandId, brandName, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), 0);
    }

    // Phương thức lấy thương hiệu theo tên
    public static Brand getBrandByName(String brandName) {
        String query = "SELECT * FROM brands WHERE brand_name = ?"; // Câu truy vấn để lấy thông tin thương hiệu

        try (Connection conn = ConnectDB.connectDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, brandName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Tạo đối tượng Brand từ kết quả truy vấn
                return new Brand(
                    rs.getInt("brand_id"),
                    rs.getString("brand_name"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getInt("product_count")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving brand: " + e.getMessage());
        }
        return null; // Trả về null nếu không tìm thấy thương hiệu
    }

    @Override
    public String toString() {
        return "Brand{" +
                "brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", productCount=" + productCount +
                '}';
    }

    // Getters và Setters
    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }
    
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
}
