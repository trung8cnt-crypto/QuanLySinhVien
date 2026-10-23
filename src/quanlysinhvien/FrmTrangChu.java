package quanlysinhvien;
import javax.swing.*;
import java.awt.*;

public class FrmTrangChu extends JFrame {

    private String hoTenNguoiDung;
    private String vaiTroNguoiDung;

    private JButton btnQLLop, btnQLSinhVien, btnQLMonHoc, btnQLTaiKhoan, btnDangXuat, btnThoat;

    // Constructor nhận Họ tên và Vai trò truyền sang từ FrmDangNhap
    public FrmTrangChu(String hoTen, String vaiTro) {
        this.hoTenNguoiDung = hoTen;
        this.vaiTroNguoiDung = vaiTro;

        setTitle("Hệ Thống Quản Lý Sinh Viên - Trang Chủ");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        // =================================================================
        // 1. PHẦN HEADER (Tiêu đề + Thông tin người đăng nhập)
        // =================================================================
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlHeader.setBackground(new Color(0, 102, 204));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("PHẦN MỀM QUẢN LÝ SINH VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUserInfo = new JLabel("Xin chào: " + hoTenNguoiDung + "  |  Vai trò: " + vaiTroNguoiDung, SwingConstants.CENTER);
        lblUserInfo.setFont(new Font("Arial", Font.ITALIC, 15));
        lblUserInfo.setForeground(new Color(255, 255, 153));

        pnlHeader.add(lblTitle);
        pnlHeader.add(lblUserInfo);
        add(pnlHeader, BorderLayout.NORTH);

        // =================================================================
        // 2. PHẦN CENTER (4 Nút lớn mở 4 Form Quản lý)
        // =================================================================
        JPanel pnlMenu = new JPanel(new GridLayout(2, 2, 25, 25));
        pnlMenu.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(15, 40, 15, 40), 
                "DANH MỤC CHỨC NĂNG HỆ THỐNG"));

        Font fontButton = new Font("Arial", Font.BOLD, 16);

        btnQLLop = new JButton("1. QUẢN LÝ LỚP HỌC");
        btnQLSinhVien = new JButton("2. QUẢN LÝ SINH VIÊN");
        btnQLMonHoc = new JButton("3. QUẢN LÝ MÔN HỌC");
        btnQLTaiKhoan = new JButton("4. QUẢN LÝ TÀI KHOẢN");

        JButton[] mainButtons = {btnQLLop, btnQLSinhVien, btnQLMonHoc, btnQLTaiKhoan};
        for (JButton btn : mainButtons) {
            btn.setFont(fontButton);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            pnlMenu.add(btn);
        }

        // Phân quyền nhanh: Nếu không phải "Quản trị viên" thì khóa nút Quản lý Tài Khoản
        if (!vaiTroNguoiDung.equalsIgnoreCase("Quản trị viên")) {
            btnQLTaiKhoan.setEnabled(false);
            btnQLTaiKhoan.setToolTipText("Chỉ Quản trị viên mới có quyền truy cập mục này!");
        }

        add(pnlMenu, BorderLayout.CENTER);

        // =================================================================
        // 3. PHẦN FOOTER (Nút Đăng xuất & Thoát)
        // =================================================================
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        btnDangXuat = new JButton("Đăng xuất");
        btnThoat = new JButton("Thoát chương trình");

        btnDangXuat.setPreferredSize(new Dimension(120, 35));
        btnThoat.setPreferredSize(new Dimension(150, 35));

        pnlFooter.add(btnDangXuat);
        pnlFooter.add(btnThoat);
        add(pnlFooter, BorderLayout.SOUTH);

        // =================================================================
        // 4. SỰ KIỆN MỞ CÁC FORM QUẢN LÝ
        // =================================================================
        btnQLLop.addActionListener(e -> new FrmLop().setVisible(true));
        
        btnQLSinhVien.addActionListener(e -> new FrmSinhVien().setVisible(true));
        
        btnQLMonHoc.addActionListener(e -> new FrmMonHoc().setVisible(true));
        
        btnQLTaiKhoan.addActionListener(e -> new FrmTaiKhoan().setVisible(true));

        // Sự kiện Đăng xuất: Đóng Trang Chủ và mở lại Form Đăng Nhập
        btnDangXuat.addActionListener(e -> {
            int chon = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn đăng xuất khỏi tài khoản hiện tại?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION
            );
            if (chon == JOptionPane.YES_OPTION) {
                this.dispose(); // Đóng Trang Chủ
                new FrmDangNhap().setVisible(true); // Bật lại Đăng Nhập
            }
        });

        // Sự kiện Thoát hoàn toàn
        btnThoat.addActionListener(e -> {
            int chon = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đóng toàn bộ ứng dụng?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION
            );
            if (chon == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    // Hàm main chạy thử riêng FrmTrangChu (giả lập quyền Quản trị viên)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmTrangChu("Quản Trị Hệ Thống", "Quản trị viên").setVisible(true);
        });
    }
}