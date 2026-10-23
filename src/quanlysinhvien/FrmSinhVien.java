package quanlysinhvien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FrmSinhVien extends JFrame {

    private JTextField txtMaSV, txtHoTen, txtNgaySinh, txtSDT, txtDiaChi, txtTimKiem;
    private JRadioButton radNam, radNu;
    private JComboBox<String> cboMaLop;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable tblSinhVien;
    private DefaultTableModel model;

    public FrmSinhVien() {
        setTitle("Quản Lý Sinh Viên");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ HỒ SƠ SINH VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // 2. Khu vực Nhập liệu (Chia 2 cột)
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlInput = new JPanel(new GridLayout(4, 4, 15, 12));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));

        // Hàng 1
        pnlInput.add(new JLabel("Mã sinh viên:"));
        txtMaSV = new JTextField();
        pnlInput.add(txtMaSV);

        pnlInput.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        pnlInput.add(txtSDT);

        // Hàng 2
        pnlInput.add(new JLabel("Họ và tên:"));
        txtHoTen = new JTextField();
        pnlInput.add(txtHoTen);

        pnlInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pnlInput.add(txtDiaChi);

        // Hàng 3
        pnlInput.add(new JLabel("Ngày sinh (YYYY-MM-DD):"));
        txtNgaySinh = new JTextField("2006-01-01");
        pnlInput.add(txtNgaySinh);

        pnlInput.add(new JLabel("Lớp học:"));
        cboMaLop = new JComboBox<>();
        pnlInput.add(cboMaLop);

        // Hàng 4
        pnlInput.add(new JLabel("Giới tính:"));
        JPanel pnlGioiTinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radNam = new JRadioButton("Nam", true);
        radNu = new JRadioButton("Nữ");
        ButtonGroup bg = new ButtonGroup();
        bg.add(radNam);
        bg.add(radNu);
        pnlGioiTinh.add(radNam);
        pnlGioiTinh.add(radNu);
        pnlInput.add(pnlGioiTinh);

        pnlInput.add(new JLabel("")); // Ô trống căn lề
        pnlInput.add(new JLabel(""));

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // 3. Khu vực Nút bấm & Tìm kiếm
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
        pnlSearch.add(new JLabel("Tìm kiếm (Mã SV / Họ tên / Mã lớp):"));
        txtTimKiem = new JTextField(25);
        btnTimKiem = new JButton("Tìm kiếm");
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        pnlActions.add(pnlButtons);
        pnlActions.add(pnlSearch);
        pnlTop.add(pnlActions, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // 4. Bảng JTable hiển thị danh sách
        String[] columns = {"Mã SV", "Họ Tên", "Ngày Sinh", "Giới Tính", "SĐT", "Địa Chỉ", "Mã Lớp"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSinhVien = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblSinhVien);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách sinh viên"));
        add(scrollPane, BorderLayout.CENTER);

        // Khởi chạy dữ liệu ban đầu
        loadMaLopToComboBox();
        loadSinhVienToTable("");

        // =================================================================
        // SỰ KIỆN CLICK BẢNG -> ĐỔ DỮ LIỆU LÊN FORM
        // =================================================================
        tblSinhVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblSinhVien.getSelectedRow();
                if (row >= 0) {
                    txtMaSV.setText(model.getValueAt(row, 0).toString());
                    txtHoTen.setText(model.getValueAt(row, 1).toString());
                    txtNgaySinh.setText(model.getValueAt(row, 2).toString());
                    
                    String gt = model.getValueAt(row, 3).toString();
                    if (gt.equalsIgnoreCase("Nam")) radNam.setSelected(true);
                    else radNu.setSelected(true);

                    txtSDT.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
                    txtDiaChi.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
                    cboMaLop.setSelectedItem(model.getValueAt(row, 6).toString());

                    txtMaSV.setEditable(false); // Khóa ô Mã SV khi đang chọn
                }
            }
        });

        // =================================================================
        // SỰ KIỆN CÁC NÚT BẤM (VIẾT THẲNG SQL)
        // =================================================================
        btnThem.addActionListener(e -> themSinhVien());
        btnSua.addActionListener(e -> suaSinhVien());
        btnXoa.addActionListener(e -> xoaSinhVien());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTimKiem.addActionListener(e -> loadSinhVienToTable(txtTimKiem.getText().trim()));
    }

    // Tự động lấy danh sách Mã Lớp từ bảng Lop nạp vào JComboBox
    private void loadMaLopToComboBox() {
        cboMaLop.removeAllItems();
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT maLop FROM Lop";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cboMaLop.addItem(rs.getString("maLop"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách lớp: " + ex.getMessage());
        }
    }

    // 1. Tải danh sách Sinh Viên lên JTable (Kèm Tìm kiếm)
    private void loadSinhVienToTable(String tuKhoa) {
        model.setRowCount(0);
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM SinhVien WHERE maSV LIKE ? OR hoTen LIKE ? OR maLop LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + tuKhoa + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("maSV"),
                    rs.getString("hoTen"),
                    rs.getDate("ngaySinh"),
                    rs.getString("gioiTinh"),
                    rs.getString("soDienThoai"),
                    rs.getString("diaChi"),
                    rs.getString("maLop")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách sinh viên: " + ex.getMessage());
        }
    }

    // 2. Thêm Sinh Viên
    private void themSinhVien() {
        if (txtMaSV.getText().trim().isEmpty() || txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã SV và Họ tên không được để trống!");
            return;
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO SinhVien (maSV, hoTen, ngaySinh, gioiTinh, soDienThoai, diaChi, maLop) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtMaSV.getText().trim());
            ps.setString(2, txtHoTen.getText().trim());
            ps.setDate(3, Date.valueOf(txtNgaySinh.getText().trim())); // Định dạng chuẩn YYYY-MM-DD
            ps.setString(4, radNam.isSelected() ? "Nam" : "Nữ");
            ps.setString(5, txtSDT.getText().trim());
            ps.setString(6, txtDiaChi.getText().trim());
            ps.setString(7, cboMaLop.getSelectedItem().toString());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
            lamMoi();
        } catch (IllegalArgumentException ie) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải nhập đúng định dạng Năm-Tháng-Ngày (VD: 2006-05-14)!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm sinh viên (Kiểm tra trùng mã SV): " + ex.getMessage());
        }
    }

    // 3. Sửa Sinh Viên
    private void suaSinhVien() {
        if (txtMaSV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên trên bảng để sửa!");
            return;
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE SinhVien SET hoTen=?, ngaySinh=?, gioiTinh=?, soDienThoai=?, diaChi=?, maLop=? "
                       + "WHERE maSV=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtHoTen.getText().trim());
            ps.setDate(2, Date.valueOf(txtNgaySinh.getText().trim()));
            ps.setString(3, radNam.isSelected() ? "Nam" : "Nữ");
            ps.setString(4, txtSDT.getText().trim());
            ps.setString(5, txtDiaChi.getText().trim());
            ps.setString(6, cboMaLop.getSelectedItem().toString());
            ps.setString(7, txtMaSV.getText().trim());

            int kq = ps.executeUpdate();
            if (kq > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin sinh viên thành công!");
                lamMoi();
            }
        } catch (IllegalArgumentException ie) {
            JOptionPane.showMessageDialog(this, "Ngày sinh sai định dạng YYYY-MM-DD!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

    // 4. Xóa Sinh Viên
    private void xoaSinhVien() {
        String maSV = txtMaSV.getText().trim();
        if (maSV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận xóa sinh viên có mã: " + maSV + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "DELETE FROM SinhVien WHERE maSV = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maSV);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa sinh viên: " + ex.getMessage());
        }
    }

    // 5. Làm mới các ô nhập
    private void lamMoi() {
        txtMaSV.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("2006-01-01");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtTimKiem.setText("");
        radNam.setSelected(true);
        txtMaSV.setEditable(true);
        txtMaSV.requestFocus();
        loadMaLopToComboBox();
        loadSinhVienToTable("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmSinhVien().setVisible(true));
    }
}