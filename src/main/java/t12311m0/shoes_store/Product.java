    package t12311m0.shoes_store;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;

    public class Product {

        private int id;
        private String name;
        private int brandId; // Brand ID
        private String brandName; // Brand Name
        private double price;
        private int quantity;
        private String description;
        private String size;
        private String imageUrl;
        private Status status;

        public enum Status {
            AVAILABLE, UNAVAILABLE
        }

        // Constructor with all parameters
        public Product(int id, String name, String brandName, double price, int quantity, String description, String size, String imageUrl) {
            this.id = id;
            this.name = name;
            this.brandId = getBrandIdByName(brandName); // Retrieve brandId from brandName
            this.brandName = brandName; // Set brandName
            this.price = price;
            this.quantity = quantity;
            this.description = description;
            this.size = size;
            this.imageUrl = imageUrl;
              this.status = Status.AVAILABLE; // Default status

            // Check if brandId is valid
            if (this.brandId == -1) {
                throw new IllegalArgumentException("Brand not found: " + brandName);
            }
        }

        // Constructor without quantity and description
        public Product(int id, String name, String brandName, double price, String size, String imageUrl) {
            this.id = id;
            this.name = name;
            this.brandName = brandName;
            this.brandId = getBrandIdByName(brandName); // Retrieve brandId from brandName
            this.price = price;
            this.size = size;
            this.imageUrl = imageUrl;
        }

        // Getters
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getBrandName() {
            return brandName; // Getter for brandName
        }

        public int getBrandId() {
            return brandId; // Getter for brandId
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getDescription() {
            return description;
        }

        public String getSize() {
            return size;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Status getStatus() {
            return status;
        }

        // Method to get brandId by name
        public static int getBrandIdByName(String brandName) {
            String sql = "SELECT brand_id FROM brands WHERE brand_name = ?";
            try (Connection conn = ConnectDB.connectDB();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, brandName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("brand_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1; // Return -1 if not found
        }

        // Method to get brandName by id
        public static String getBrandNameById(int brandId) {
            String brandName = null;
            String sql = "SELECT brand_name FROM brands WHERE brand_id = ?";

            try (Connection conn = ConnectDB.connectDB();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, brandId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    brandName = rs.getString("brand_name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return brandName;
        }

        // Save product to database
        public boolean saveToDatabase() {
            String sql = "INSERT INTO products (product_name, brand_id, price, quantity, description, product_size, image_url, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = ConnectDB.connectDB();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setInt(2, brandId);
                stmt.setDouble(3, price);
                stmt.setInt(4, quantity);
                stmt.setString(5, description);
                stmt.setString(6, size);
                stmt.setString(7, imageUrl);
                stmt.setString(8, status.name());

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

    }