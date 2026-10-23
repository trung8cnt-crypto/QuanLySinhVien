# Phần Mềm Quản Lý Sinh Viên (Java Swing & SQL Server)

Dự án ứng dụng Desktop quản lý thông tin sinh viên, lớp học và môn học được xây dựng bằng ngôn ngữ **Java (Java Swing)** trên **Apache NetBeans IDE**, kết nối cơ sở dữ liệu **Microsoft SQL Server** thông qua thư viện **JDBC**.

---

## Chức năng chính của hệ thống

1. **Đăng nhập & Phân quyền (`FrmDangNhap`, `FrmTrangChu`):**
   * Xác thực người dùng từ cơ sở dữ liệu.
   * Phân quyền truy cập chức năng theo vai trò (**Quản trị viên**, **Giảng viên**, **Sinh viên**).
2. **Quản lý Lớp học (`FrmLop`):**
   * Xem danh sách, Thêm, Sửa, Xóa thông tin lớp học và khoa quản lý.
3. **Quản lý Sinh viên (`FrmSinhVien`):**
   * Quản lý hồ sơ sinh viên (Mã SV, Họ tên, Ngày sinh, Giới tính, SĐT, Địa chỉ, Lớp).
   * Ràng buộc khóa ngoại chặt chẽ với danh mục Lớp học.
4. **Quản lý Môn học (`FrmMonHoc`):**
   * Quản lý danh mục môn học, số tín chỉ và số tiết học.

---

## Yêu cầu hệ thống & Công cụ cài đặt

* **IDE:** Apache NetBeans IDE (khuyến nghị bản 17 trở lên, dự án phát triển trên NetBeans 31).
* **JDK:** Java Development Kit (JDK 11 trở lên).
* **Hệ quản trị CSDL:** Microsoft SQL Server (Express / Developer Edition) & SSMS.
* **Thư viện kết nối:** `mssql-jdbc-13.6.0.jre11.jar` (đã tích hợp sẵn trong dự án).

---

## Hướng dẫn cài đặt và chạy chương trình (Dành cho Giảng viên chấm bài)

### Bước 1: Khởi tạo Cơ sở dữ liệu (SQL Server)
1. Mở phần mềm **SQL Server Management Studio (SSMS)**.
2. Mở file script cơ sở dữ liệu `.sql` nằm trong thư mục gốc của dự án (hoặc chạy script tạo CSDL `QLSINHVIEN`).
3. Nhấn **F5 (Execute)** để hệ thống tự động tạo Database **`QLSINHVIEN`**, các bảng (`Lop`, `SinhVien`, `MonHoc`, `TaiKhoan`) cùng dữ liệu mẫu.
4. Đảm bảo SQL Server đã bật chế độ **SQL Server and Windows Authentication mode (Mixed Mode)** và mở cổng TCP/IP **`1433`**.

### Bước 2: Mở dự án trên NetBeans
1. Tải mã nguồn về máy (`Download ZIP` hoặc `git clone https://github.com/trung8cnt-crypto/QuanLySinhVien.git`).
2. Mở **Apache NetBeans IDE** $\rightarrow$ Chọn **File** $\rightarrow$ **Open Project...** và trỏ đến thư mục `QuanLySinhVien`.
3. Kiểm tra mục **Libraries**: Nếu NetBeans báo thiếu đường dẫn file `.jar`, nhấp chuột phải vào **Libraries** $\rightarrow$ chọn **Add JAR/Folder...** và chọn lại file `mssql-jdbc-13.6.0.jre11.jar`.

### Bước 3: Cấu hình tài khoản kết nối CSDL
Mở file `src/quanlysinhvien/DBConnect.java` và kiểm tra thông tin tài khoản SQL Server tại dòng 13 - 15 cho khớp với máy chấm bài:

```java
String url = "jdbc:sqlserver://localhost:1433;databaseName=QLSINHVIEN;encrypt=true;trustServerCertificate=true;";
String user = "sa";       // Tài khoản SQL Server trên máy
String pass = "123456";   // Mật khẩu SQL Server trên máy
