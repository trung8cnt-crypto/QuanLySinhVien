package quanlysinhvien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FrmLop extends JFrame {

    private JTextField txtMaLop, txtTenLop, txtKhoa, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable tblLop;
    private DefaultTableModel model;

    public FrmLop() {
        setTitle("Quản Lý Lớp Học");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Đóng form này không tắt cả chương trình
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ LỚP HỌC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Khu vực Nhập liệu + Nút bấm (Ở giữa phía trên)
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JPanel pnlInput = new JPanel(new GridLayout(3, 2, 10, 12));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin lớp học"));

        pnlInput.add(new JLabel("Mã lớp:"));
        txtMaLop = new JTextField();
        pnlInput.add(txtMaLop);

        pnlInput.add(new JLabel("Tên lớp:"));
        txtTenLop = new JTextField();
        pnlInput.add(txtTenLop);

        pnlInput.add(new JLabel("Khoa:"));
        txtKhoa = new JTextField();
        pnlInput.add(txtKhoa);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // Khu vực Nút chức năng & Tìm kiếm
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
        pnlSearch.add(new JLabel("Tìm kiếm (Mã/Tên lớp):"));
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm kiếm");
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        pnlActions.add(pnlButtons);
        pnlActions.add(pnlSearch);
        pnlTop.add(pnlActions, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // 3. Bảng dữ liệu JTable
        model = new DefaultTableModel(new String[]{"Mã Lớp", "Tên Lớp", "Khoa"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        tblLop = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblLop);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách lớp học"));
        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu ngay khi mở Form
        loadDataToTable("");

        // =================================================================
        // SỰ KIỆN CLICK TRÊN BẢNG -> ĐỔ DỮ LIỆU LÊN Ô NHẬP
        // =================================================================
        tblLop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblLop.getSelectedRow();
                if (row >= 0) {
                    txtMaLop.setText(model.getValueAt(row, 0).toString());
                    txtTenLop.setText(model.getValueAt(row, 1).toString());
                    txtKhoa.setText(model.getValueAt(row, 2).toString());
                    txtMaLop.setEditable(false); // Không cho sửa khóa chính
                }
            }
        });

        // =================================================================
        // SỰ KIỆN CÁC NÚT BẤM (VIẾT THẲNG SQL)
        // =================================================================
        btnThem.addActionListener(e -> themLop());
        btnSua.addActionListener(e -> suaLop());
        btnXoa.addActionListener(e -> xoaLop());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTimKiem.addActionListener(e -> loadDataToTable(txtTimKiem.getText().trim()));
    }

    // 1. Hàm tải dữ liệu và tìm kiếm
    private void loadDataToTable(String tuKhoa) {
        model.setRowCount(0); // Xóa dữ liệu cũ trên bảng
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT maLop, tenLop, khoa FROM Lop WHERE maLop LIKE ? OR tenLop LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("maLop"),
                    rs.getString("tenLop"),
                    rs.getString("khoa")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    // 2. Hàm Thêm Lớp
    private void themLop() {
        String ma = txtMaLop.getText().trim();
        String ten = txtTenLop.getText().trim();
        String khoa = txtKhoa.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || khoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO Lop (maLop, tenLop, khoa) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, khoa);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm lớp học thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm (Có thể trùng Mã lớp): " + ex.getMessage());
        }
    }

    // 3. Hàm Sửa Lớp
    private void suaLop() {
        String ma = txtMaLop.getText().trim();
        String ten = txtTenLop.getText().trim();
        String khoa = txtKhoa.getText().trim();

        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lớp trên bảng để sửa!");
            return;
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE Lop SET tenLop = ?, khoa = ? WHERE maLop = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ten);
            ps.setString(2, khoa);
            ps.setString(3, ma);

            int kq = ps.executeUpdate();
            if (kq > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

    // 4. Hàm Xóa Lớp
    private void xoaLop() {
        String ma = txtMaLop.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa lớp " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "DELETE FROM Lop WHERE maLop = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa lớp thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Không thể xóa lớp này vì đang có Sinh Viên theo học!\nChi tiết: " + ex.getMessage(),
                    "Lỗi ràng buộc khóa ngoại", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 5. Làm mới Form
    private void lamMoi() {
        txtMaLop.setText("");
        txtTenLop.setText("");
        txtKhoa.setText("");
        txtTimKiem.setText("");
        txtMaLop.setEditable(true);
        txtMaLop.requestFocus();
        loadDataToTable("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmLop().setVisible(true));
    }
}