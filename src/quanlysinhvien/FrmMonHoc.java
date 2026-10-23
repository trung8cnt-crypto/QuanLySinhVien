package quanlysinhvien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FrmMonHoc extends JFrame {

    private JTextField txtMaMon, txtTenMon, txtSoTinChi, txtSoTiet, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable tblMonHoc;
    private DefaultTableModel model;

    public FrmMonHoc() {
        setTitle("Quản Lý Môn Học");
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ MÔN HỌC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // 2. Khu vực Nhập liệu
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 15, 15));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin môn học"));

        pnlInput.add(new JLabel("Mã môn học:"));
        txtMaMon = new JTextField();
        pnlInput.add(txtMaMon);

        pnlInput.add(new JLabel("Số tín chỉ:"));
        txtSoTinChi = new JTextField();
        pnlInput.add(txtSoTinChi);

        pnlInput.add(new JLabel("Tên môn học:"));
        txtTenMon = new JTextField();
        pnlInput.add(txtTenMon);

        pnlInput.add(new JLabel("Số tiết học:"));
        txtSoTiet = new JTextField("45");
        pnlInput.add(txtSoTiet);

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
        pnlSearch.add(new JLabel("Tìm kiếm (Mã / Tên môn):"));
        txtTimKiem = new JTextField(22);
        btnTimKiem = new JButton("Tìm kiếm");
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        pnlActions.add(pnlButtons);
        pnlActions.add(pnlSearch);
        pnlTop.add(pnlActions, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // 4. Bảng JTable hiển thị danh sách
        String[] columns = {"Mã Môn", "Tên Môn Học", "Số Tín Chỉ", "Số Tiết"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMonHoc = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblMonHoc);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách môn học"));
        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDataToTable("");

        // =================================================================
        // SỰ KIỆN CLICK TRÊN BẢNG -> ĐỔ DỮ LIỆU LÊN Ô NHẬP
        // =================================================================
        tblMonHoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblMonHoc.getSelectedRow();
                if (row >= 0) {
                    txtMaMon.setText(model.getValueAt(row, 0).toString());
                    txtTenMon.setText(model.getValueAt(row, 1).toString());
                    txtSoTinChi.setText(model.getValueAt(row, 2).toString());
                    txtSoTiet.setText(model.getValueAt(row, 3).toString());
                    txtMaMon.setEditable(false); // Khóa mã môn khi chọn
                }
            }
        });

        // =================================================================
        // SỰ KIỆN CÁC NÚT BẤM (VIẾT THẲNG SQL)
        // =================================================================
        btnThem.addActionListener(e -> themMonHoc());
        btnSua.addActionListener(e -> suaMonHoc());
        btnXoa.addActionListener(e -> xoaMonHoc());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTimKiem.addActionListener(e -> loadDataToTable(txtTimKiem.getText().trim()));
    }

    // 1. Tải dữ liệu và tìm kiếm
    private void loadDataToTable(String tuKhoa) {
        model.setRowCount(0);
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT maMon, tenMon, soTinChi, soTiet FROM MonHoc WHERE maMon LIKE ? OR tenMon LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("maMon"),
                    rs.getString("tenMon"),
                    rs.getInt("soTinChi"),
                    rs.getInt("soTiet")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    // 2. Thêm Môn Học
    private void themMonHoc() {
        String ma = txtMaMon.getText().trim();
        String ten = txtTenMon.getText().trim();
        String strTinChi = txtSoTinChi.getText().trim();
        String strSoTiet = txtSoTiet.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || strTinChi.isEmpty() || strSoTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin môn học!");
            return;
        }

        try {
            int tinChi = Integer.parseInt(strTinChi);
            int soTiet = Integer.parseInt(strSoTiet);

            if (tinChi <= 0 || soTiet <= 0) {
                JOptionPane.showMessageDialog(this, "Số tín chỉ và số tiết phải lớn hơn 0!");
                return;
            }

            try (Connection conn = DBConnect.getConnection()) {
                String sql = "INSERT INTO MonHoc (maMon, tenMon, soTinChi, soTiet) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, ma);
                ps.setString(2, ten);
                ps.setInt(3, tinChi);
                ps.setInt(4, soTiet);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Thêm môn học thành công!");
                lamMoi();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ và Số tiết phải là số nguyên!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm môn học (Kiểm tra trùng mã môn): " + ex.getMessage());
        }
    }

    // 3. Sửa Môn Học
    private void suaMonHoc() {
        String ma = txtMaMon.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học trên bảng để sửa!");
            return;
        }

        try {
            int tinChi = Integer.parseInt(txtSoTinChi.getText().trim());
            int soTiet = Integer.parseInt(txtSoTiet.getText().trim());

            if (tinChi <= 0 || soTiet <= 0) {
                JOptionPane.showMessageDialog(this, "Số tín chỉ và số tiết phải lớn hơn 0!");
                return;
            }

            try (Connection conn = DBConnect.getConnection()) {
                String sql = "UPDATE MonHoc SET tenMon = ?, soTinChi = ?, soTiet = ? WHERE maMon = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtTenMon.getText().trim());
                ps.setInt(2, tinChi);
                ps.setInt(3, soTiet);
                ps.setString(4, ma);

                int kq = ps.executeUpdate();
                if (kq > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật môn học thành công!");
                    lamMoi();
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ và Số tiết phải là số nguyên!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

    // 4. Xóa Môn Học
    private void xoaMonHoc() {
        String ma = txtMaMon.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận xóa môn học: " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "DELETE FROM MonHoc WHERE maMon = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa môn học thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa môn học: " + ex.getMessage());
        }
    }

    // 5. Làm mới
    private void lamMoi() {
        txtMaMon.setText("");
        txtTenMon.setText("");
        txtSoTinChi.setText("");
        txtSoTiet.setText("45");
        txtTimKiem.setText("");
        txtMaMon.setEditable(true);
        txtMaMon.requestFocus();
        loadDataToTable("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmMonHoc().setVisible(true));
    }
}