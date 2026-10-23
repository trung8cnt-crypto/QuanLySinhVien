package quanlysinhvien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FrmTaiKhoan extends JFrame {

    private JTextField txtTenDangNhap, txtMatKhau, txtHoTenHienThi, txtTimKiem;
    private JComboBox<String> cboVaiTro;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable tblTaiKhoan;
    private DefaultTableModel model;

    public FrmTaiKhoan() {
        setTitle("Quản Lý Tài Khoản Hệ Thống");
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // 2. Khu vực Nhập liệu
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 15, 15));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));

        pnlInput.add(new JLabel("Tên đăng nhập:"));
        txtTenDangNhap = new JTextField();
        pnlInput.add(txtTenDangNhap);

        pnlInput.add(new JLabel("Họ tên hiển thị:"));
        txtHoTenHienThi = new JTextField();
        pnlInput.add(txtHoTenHienThi);

        pnlInput.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JTextField();
        pnlInput.add(txtMatKhau);

        pnlInput.add(new JLabel("Vai trò:"));
        cboVaiTro = new JComboBox<>(new String[]{"Quản trị viên", "Giảng viên", "Sinh viên"});
        pnlInput.add(cboVaiTro);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // 3. Khu vực Nút chức năng & Tìm kiếm
        JPanel pnlActions = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");
        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnlSearch.add(new JLabel("Tìm kiếm (Tên ĐN / Họ tên / Vai trò):"));
        txtTimKiem = new JTextField(22);
        btnTimKiem = new JButton("Tìm kiếm");
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        pnlActions.add(pnlButtons);
        pnlActions.add(pnlSearch);
        pnlTop.add(pnlActions, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // 4. Bảng JTable hiển thị danh sách
        String[] columns = {"Tên Đăng Nhập", "Mật Khẩu", "Họ Tên Hiển Thị", "Vai Trò"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTaiKhoan = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblTaiKhoan);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách tài khoản"));
        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDataToTable("");

        // =================================================================
        // SỰ KIỆN CLICK TRÊN BẢNG -> ĐỔ DỮ LIỆU LÊN Ô NHẬP
        // =================================================================
        tblTaiKhoan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblTaiKhoan.getSelectedRow();
                if (row >= 0) {
                    txtTenDangNhap.setText(model.getValueAt(row, 0).toString());
                    txtMatKhau.setText(model.getValueAt(row, 1).toString());
                    txtHoTenHienThi.setText(model.getValueAt(row, 2).toString());
                    cboVaiTro.setSelectedItem(model.getValueAt(row, 3).toString());
                    txtTenDangNhap.setEditable(false); // Không cho sửa khóa chính
                }
            }
        });

        // =================================================================
        // SỰ KIỆN CÁC NÚT BẤM (VIẾT THẲNG SQL)
        // =================================================================
        btnThem.addActionListener(e -> themTaiKhoan());
        btnSua.addActionListener(e -> suaTaiKhoan());
        btnXoa.addActionListener(e -> xoaTaiKhoan());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTimKiem.addActionListener(e -> loadDataToTable(txtTimKiem.getText().trim()));
    }

    // 1. Tải dữ liệu và tìm kiếm
    private void loadDataToTable(String tuKhoa) {
        model.setRowCount(0);
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT tenDangNhap, matKhau, hoTenHienThi, vaiTro FROM TaiKhoan "
                       + "WHERE tenDangNhap LIKE ? OR hoTenHienThi LIKE ? OR vaiTro LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            String pattern = "%" + tuKhoa + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("tenDangNhap"),
                    rs.getString("matKhau"),
                    rs.getString("hoTenHienThi"),
                    rs.getString("vaiTro")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách tài khoản: " + ex.getMessage());
        }
    }

    // 2. Thêm Tài Khoản
    private void themTaiKhoan() {
        String user = txtTenDangNhap.getText().trim();
        String pass = txtMatKhau.getText().trim();
        String hoTen = txtHoTenHienThi.getText().trim();
        String vaiTro = cboVaiTro.getSelectedItem().toString();

        if (user.isEmpty() || pass.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên đăng nhập, Mật khẩu và Họ tên!");
            return;
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, hoTenHienThi, vaiTro) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, hoTen);
            ps.setString(4, vaiTro);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm tài khoản (Có thể trùng Tên đăng nhập): " + ex.getMessage());
        }
    }

    // 3. Sửa Tài Khoản
    private void suaTaiKhoan() {
        String user = txtTenDangNhap.getText().trim();
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trên bảng để sửa!");
            return;
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE TaiKhoan SET matKhau = ?, hoTenHienThi = ?, vaiTro = ? WHERE tenDangNhap = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtMatKhau.getText().trim());
            ps.setString(2, txtHoTenHienThi.getText().trim());
            ps.setString(3, cboVaiTro.getSelectedItem().toString());
            ps.setString(4, user);

            int kq = ps.executeUpdate();
            if (kq > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
                lamMoi();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

    // 4. Xóa Tài Khoản
    private void xoaTaiKhoan() {
        String user = txtTenDangNhap.getText().trim();
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!");
            return;
        }

        if (user.equalsIgnoreCase("admin")) {
            JOptionPane.showMessageDialog(this, "Không được phép xóa tài khoản Quản trị viên gốc (admin)!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa tài khoản: " + user + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "DELETE FROM TaiKhoan WHERE tenDangNhap = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa tài khoản: " + ex.getMessage());
        }
    }

    // 5. Làm mới
    private void lamMoi() {
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtHoTenHienThi.setText("");
        txtTimKiem.setText("");
        cboVaiTro.setSelectedIndex(0);
        txtTenDangNhap.setEditable(true);
        txtTenDangNhap.requestFocus();
        loadDataToTable("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmTaiKhoan().setVisible(true));
    }
}