package quanlysinhvien;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {
    
    // Hàm trả về đối tượng Connection để các Form gọi trực tiếp
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLSINHVIEN;encrypt=true;trustServerCertificate=true;";
            String user = "sa";
            String pass = "123456";
            
            conn = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    // Hàm main nhỏ để bạn bấm Shift + F6 kiểm tra nhanh xem kết nối thành công chưa
    public static void main(String[] args) {
        Connection c = getConnection();
        if (c != null) {
            System.out.println("Kết nối SQL Server thành công!");
        } else {
            System.out.println("Kết nối thất bại!");
        }
    }
}