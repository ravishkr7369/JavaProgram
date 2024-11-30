import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class EmployeeJdbc extends JFrame {
    JTextField empIdField, empNameField, mobileField, searchField;
    JTable table;
    DefaultTableModel model;

    public EmployeeJdbc() {
        setTitle("Employee Registration Form by 22EARCS055");
        setSize(800, 500);
        getContentPane().setLayout(null);

        JLabel titleLabel = new JLabel("Employee Registration By Govind Yadav ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setBounds(200, 10, 500, 30);
        getContentPane().add(titleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setLayout(null);
        formPanel.setBounds(20, 50, 350, 250);
        formPanel.setBorder(new TitledBorder(new EtchedBorder(), "Please Enter Emp Details", TitledBorder.CENTER, TitledBorder.TOP, null, Color.BLUE));
        getContentPane().add(formPanel);

        JLabel empIdLabel = new JLabel("Employee ID:");
        empIdLabel.setBounds(10, 40, 100, 25);
        formPanel.add(empIdLabel);

        empIdField = new JTextField();
        empIdField.setBounds(120, 40, 200, 25);
        formPanel.add(empIdField);

        JLabel empNameLabel = new JLabel("Employee Name:");
        empNameLabel.setBounds(10, 80, 100, 25);
        formPanel.add(empNameLabel);

        empNameField = new JTextField();
        empNameField.setBounds(120, 80, 200, 25);
        formPanel.add(empNameField);

        JLabel mobileLabel = new JLabel("Mobile no.:");
        mobileLabel.setBounds(10, 120, 100, 25);
        formPanel.add(mobileLabel);

        mobileField = new JTextField();
        mobileField.setBounds(120, 120, 200, 25);
        formPanel.add(mobileField);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(120, 170, 80, 30);
        formPanel.add(saveButton);
        saveButton.addActionListener(e -> saveData());

        JLabel searchLabel = new JLabel("Enter ID:");
        searchLabel.setBounds(20, 330, 60, 25);
        getContentPane().add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(80, 330, 100, 25);
        getContentPane().add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(290, 330, 80, 25);
        getContentPane().add(searchButton);
        searchButton.addActionListener(e -> searchData());

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(400, 330, 80, 25);
        getContentPane().add(updateButton);
        updateButton.addActionListener(e -> updateData());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(541, 330, 80, 25);
        getContentPane().add(deleteButton);
        deleteButton.addActionListener(e -> deleteData());

        JButton loadButton = new JButton("Load");
        loadButton.setBounds(670, 330, 80, 25);
        getContentPane().add(loadButton);
        loadButton.addActionListener(e -> loadData());

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBounds(400, 50, 350, 250);
        tablePanel.setBorder(new TitledBorder(new EtchedBorder(), "Employee Database", TitledBorder.CENTER, TitledBorder.TOP, null, Color.BLUE));
        getContentPane().add(tablePanel);

        model = new DefaultTableModel(new String[]{"Serial No", "Emp ID", "Emp Name", "Mobile"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private Connection connectDatabase() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/employee", "root", "7369");
    }

    private void saveData() {
        try (Connection conn = connectDatabase()) {
            PreparedStatement stmt =
            		conn.prepareStatement("INSERT INTO users (id, name, mobile_no) VALUES (?, ?, ?)");
            stmt.setInt(1, Integer.parseInt(empIdField.getText()));
            stmt.setString(2, empNameField.getText());
            stmt.setString(3, mobileField.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            clearFields();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void searchData() {
        try (Connection conn = connectDatabase()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            stmt.setInt(1, Integer.parseInt(searchField.getText()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                empIdField.setText(String.valueOf(rs.getInt("id")));
                empNameField.setText(rs.getString("name"));
                mobileField.setText(rs.getString("mobile_no"));
            } else {
                JOptionPane.showMessageDialog(this, "Employee Not Found");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateData() {
        try (Connection conn = connectDatabase()) {
            String mobileNumber = mobileField.getText();
            if (!mobileNumber.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Mobile number must be exactly 10 digits.");
                return;
            }
            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET name = ?, mobile_no = ? WHERE id = ?");
            stmt.setString(1, empNameField.getText());
            stmt.setString(2, mobileNumber);
            stmt.setInt(3, Integer.parseInt(empIdField.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Updated Successfully");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection conn = connectDatabase()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
            stmt.setInt(1, Integer.parseInt(searchField.getText()));
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                Statement checkStmt = conn.createStatement();
                ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM users");
                rs.next();
                if (rs.getInt(1) == 0) {
                    Statement resetStmt = conn.createStatement();
                    resetStmt.executeUpdate("ALTER TABLE users AUTO_INCREMENT = 1");
                }
                JOptionPane.showMessageDialog(this, "Data Deleted Successfully");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Employee ID not found. Deletion failed.");
            }
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadData() {
        try (Connection conn = connectDatabase()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("serial_no"), rs.getInt("id"), rs.getString("name"), rs.getString("mobile_no")});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        empIdField.setText("");
        empNameField.setText("");
        mobileField.setText("");
        searchField.setText("");
    }

    public static void main(String[] args) {
        new EmployeeJdbc();
    }
}