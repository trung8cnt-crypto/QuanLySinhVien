package quanlysinhvien;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FrmDangNhap extends JFrame {

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap, btnThoat;

    public FrmDangNhap() {
        // 1. Cấu hình khung Form chính
        setTitle("Đăng Nhập Hệ Thống Quản Lý Sinh Viên");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // 2. Tiêu đề Form
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 3. Panel chứa ô nhập Tài khoản & Mật khẩu
        JPanel pnlInput = new JPanel(new GridLayout(2, 2, 10, 20));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        pnlInput.add(new JLabel("Tên đăng nhập:"));
        txtTenDangNhap = new JTextField();
        pnlInput.add(txtTenDangNhap);

        pnlInput.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JPasswordField();
        pnlInput.add(txtMatKhau);

        add(pnlInput, BorderLayout.CENTER);

        // 4. Panel chứa Nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnDangNhap = new JButton("Đăng nhập");
        btnThoat = new JButton("Thoát");

        btnDangNhap.setPreferredSize(new Dimension(110, 32));
        btnThoat.setPreferredSize(new Dimension(110, 32));

        pnlButtons.add(btnDangNhap);
        pnlButtons.add(btnThoat);
        add(pnlButtons, BorderLayout.SOUTH);

        // Nhấn Enter trong ô mật khẩu sẽ tự kích hoạt nút Đăng nhập
        getRootPane().setDefaultButton(btnDangNhap);

        // =================================================================
        // 5. XỬ LÝ SỰ KIỆN NÚT ĐĂNG NHẬP (VIẾT THẲNG SQL TẠI ĐÂY)
        // =================================================================
        btnDangNhap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });

        // 6. Xử lý sự kiện nút Thoát
        btnThoat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        FrmDangNhap.this,
                        "Bạn có chắc chắn muốn thoát chương trình?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    // Hàm viết trực tiếp truy vấn SQL khi bấm nút
    private void xuLyDangNhap() {
        String user = txtTenDangNhap.getText().trim();
        String pass = new String(txtMatKhau.getPassword()).trim();

        // Kiểm tra rỗng
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kết nối CSDL và chạy câu lệnh SELECT trực tiếp
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, 
                        "Không thể kết nối đến SQL Server!", 
                        "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "SELECT hoTenHienThi, vaiTro FROM TaiKhoan WHERE tenDangNhap = ? AND matKhau = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            // Nếu tìm thấy dòng dữ liệu khớp
            if (rs.next()) {
                String hoTen = rs.getString("hoTenHienThi");
                String vaiTro = rs.getString("vaiTro");

                JOptionPane.showMessageDialog(this, 
                        "Đăng nhập thành công!\nXin chào: " + hoTen + " (" + vaiTro + ")", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Đóng Form đăng nhập hiện tại
                this.dispose();

                // Mở Form tiếp theo (Ví dụ: mở Form Quản lý Sinh viên)
                new FrmTrangChu(hoTen, vaiTro).setVisible(true);
                // new FrmSinhVien().setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, 
                        "Sai tên đăng nhập hoặc mật khẩu!", 
                        "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
                txtMatKhau.setText("");
                txtMatKhau.requestFocus();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Lỗi truy vấn: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Chạy chương trình
        SwingUtilities.invokeLater(() -> {
            new FrmDangNhap().setVisible(true);
        });
    }
}