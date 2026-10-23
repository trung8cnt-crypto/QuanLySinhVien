CREATE DATABASE QLSINHVIEN;
GO
USE QLSINHVIEN;
GO

CREATE TABLE Lop (
    maLop VARCHAR(10) PRIMARY KEY,
    tenLop NVARCHAR(50) NOT NULL,
    khoa NVARCHAR(50) NOT NULL
);

CREATE TABLE SinhVien (
    maSV VARCHAR(10) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    ngaySinh DATE NOT NULL,
    gioiTinh NVARCHAR(10) CHECK (gioiTinh IN (N'Nam', N'Nữ')),
    soDienThoai VARCHAR(15),
    diaChi NVARCHAR(200),
    maLop VARCHAR(10) NOT NULL,
    CONSTRAINT FK_SinhVien_Lop FOREIGN KEY (maLop) 
        REFERENCES Lop(maLop) ON UPDATE CASCADE
);

CREATE TABLE MonHoc (
    maMon VARCHAR(10) PRIMARY KEY,
    tenMon NVARCHAR(100) NOT NULL,
    soTinChi INT NOT NULL CHECK (soTinChi > 0),
    soTiet INT NOT NULL DEFAULT 45 CHECK (soTiet > 0)
);


CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(100) NOT NULL,
    hoTenHienThi NVARCHAR(100) NOT NULL,
    vaiTro NVARCHAR(20) NOT NULL DEFAULT N'Quản trị viên' 
        CHECK (vaiTro IN (N'Quản trị viên', N'Giảng viên', N'Sinh viên'))
);
GO


INSERT INTO Lop (maLop, tenLop, khoa) VALUES
('IT01', N'Công nghệ thông tin 01', N'Công nghệ thông tin'),
('IT02', N'Công nghệ thông tin 02', N'Công nghệ thông tin'),
('KT01', N'Kế toán doanh nghiệp 01', N'Kinh tế'),
('QT01', N'Quản trị kinh doanh 01', N'Quản trị kinh doanh');


INSERT INTO SinhVien (maSV, hoTen, ngaySinh, gioiTinh, soDienThoai, diaChi, maLop) VALUES
('SV001', N'Nguyễn Văn An', '2006-05-14', N'Nam', '0912345678', N'Hà Nội', 'IT01'),
('SV002', N'Trần Thị Bình', '2006-08-20', N'Nữ', '0987654321', N'Hải Phòng', 'IT01'),
('SV003', N'Lê Hoàng Cường', '2006-01-10', N'Nam', '0905112233', N'Nam Định', 'IT02'),
('SV004', N'Phạm Minh Dung', '2006-11-25', N'Nữ', '0933445566', N'Thanh Hóa', 'KT01'),
('SV005', N'Hoàng Quốc Đạt', '2006-03-30', N'Nam', '0977889900', N'Bắc Ninh', 'QT01');


INSERT INTO MonHoc (maMon, tenMon, soTinChi, soTiet) VALUES
('MH01', N'Lập trình Java cơ bản', 3, 45),
('MH02', N'Hệ quản trị CSDL SQL Server', 3, 45),
('MH03', N'Cấu trúc dữ liệu và giải thuật', 4, 60),
('MH04', N'Nguyên lý kế toán', 3, 45);


INSERT INTO TaiKhoan (tenDangNhap, matKhau, hoTenHienThi, vaiTro) VALUES
('admin', '123456', N'Quản Trị Hệ Thống', N'Quản trị viên'),
('gv01', '123456', N'Thầy Nguyễn Văn A', N'Giảng viên'),
('sv001', '123456', N'Nguyễn Văn An', N'Sinh viên');
GO