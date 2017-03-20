/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payrollfinalproject;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import util.DbConn;

/**
 *
 * @author Phantom
 */
public class FrmMainPayroll extends javax.swing.JFrame {

    Connection myConn = null;
    PreparedStatement myStmt = null;
    ResultSet myRs = null;
    ArrayList<Employee> employeeList = null;

    public FrmMainPayroll() {
        initComponents();
        setResizable(false);
        setSize(1600, 900);
        banklistener();
        employeeListener();
        departmentListener();
    }

    private void tableBankInitData() {
        DefaultTableModel tableModel = (DefaultTableModel) tblBank.getModel();
        for (int i = tblBank.getRowCount() - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from bank");

            // Execute SQL query
            myRs = myStmt.executeQuery();
            // Process result set
            while (myRs.next()) {
                Object data[] = {myRs.getInt("id_bank"), myRs.getString("bank"), myRs.getString("cabang_bank"), myRs.getString("alamat")};
                tableModel.addRow(data);
            }

        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tableDepartmentInitData() {

        DefaultTableModel tableModel = (DefaultTableModel) tblDepartment.getModel();
        for (int i = tblDepartment.getRowCount() - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from departments");

            // Execute statement
            myRs = myStmt.executeQuery();
            // Process result set
            while (myRs.next()) {
                Object data[] = {myRs.getInt("id_department"), myRs.getString("department")};
                tableModel.addRow(data);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tableEmployeeInitData() {
        int row = 0;
        DefaultTableModel tableModel = (DefaultTableModel) tblEmployee.getModel();
        for (int i = tblEmployee.getRowCount() - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from karyawan");
            // Execute SQL query
            myRs = myStmt.executeQuery();

            employeeList = new ArrayList<>();

            // Process result set
            while (myRs.next()) {
                row++;
                Integer id = myRs.getInt("id_karyawan");
                String name = myRs.getString("nama");
                String nik = myRs.getString("nik");
                String jenisKelamin = myRs.getString("jenis_kelamin");
                String tempatLahir = myRs.getString("tempat_lahir");
                String tanggalLahir = myRs.getString("tanggal_lahir");
                String alamat = myRs.getString("alamat");
                String noHp = myRs.getString("no_hp");
                String email = myRs.getString("email");
                String agama = myRs.getString("agama");
                String status_perkawinan = myRs.getString("status_perkawinan");
                String status_pajak = myRs.getString("status_pajak");
                String id_department = myRs.getString("id_department");
                String tipe_Karyawan = myRs.getString("tipe_Karyawan");
                String tanggal_masuk = myRs.getString("tgl_masuk");
                Double gaji_kotor = myRs.getDouble("gaji_kotor");
                Double tunjangan = myRs.getDouble("tunjangan");
                String idBank = myRs.getString("id_bank");
                String noRekening = myRs.getString("no_rekening");

                employeeList.add(new Employee(id, name, nik, jenisKelamin, tempatLahir, tanggalLahir, alamat, noHp, email,
                        agama, status_perkawinan, status_pajak, id_department, tipe_Karyawan, tanggal_masuk, gaji_kotor, tunjangan, idBank, noRekening));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < row; i++) {
            Object data[] = {employeeList.get(i).getId(), employeeList.get(i).getName(), employeeList.get(i).getNik()};
            tableModel.addRow(data);
        }
        //generate
        generateDepartmenCboItem();
        generateBankCboItem();
    }

    private void generateDepartmenCboItem() {
        cboEmployeeIdDepartment.removeAllItems();
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from departments");
            // Execute SQL query
            myRs = myStmt.executeQuery();
            ArrayList<String> department = new ArrayList<>();
            // Process result set
            while (myRs.next()) {
                department.add(myRs.getString("department"));
            }
            for (String a : department) {
                cboEmployeeIdDepartment.addItem(a);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generateBankCboItem() {
        cboEmployeeIdBank.removeAllItems();
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from bank");
            // Execute SQL query
            myRs = myStmt.executeQuery();
            ArrayList<String> bank = new ArrayList<>();
            // Process result set
            while (myRs.next()) {
                bank.add(myRs.getString("bank"));
            }
            for (String a : bank) {
                cboEmployeeIdBank.addItem(a);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void employeeListener() {
        ListSelectionListener listener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = tblEmployee.getSelectedRow();
                if (row >= 0) {
                    Employee employee = employeeList.get(row);
                    txtEmployeeID.setText(tblEmployee.getValueAt(row, 0).toString());
                    txtEmployeeName.setText(tblEmployee.getValueAt(row, 1).toString());
                    txtEmployeeNik.setText(employee.getNik());
                    cboEmployeeJenisKelamin.setSelectedItem(employee.getJenisKelamin());
                    txtEmployeeTempatLahir.setText(employee.getTempatLahir());
                    txtEmployeeTangalLahir.setText(employee.getTanggalLahir());
                    txtEmployeeAlamat.setText(employee.getAlamat());
                    txtEmployeeNoHP.setText(employee.getNoHp());
                    txtEmployeeEmail.setText(employee.getEmail());
                    cboEmployeeAgama.setSelectedItem(employee.getAgama());
                    cboEmployeeStatusPerkawinan.setSelectedItem(employee.getStatus_perkawinan());
                    cboEmployeeStatusPajak.setSelectedItem(employee.getStatus_pajak());
                    cboEmployeeTipeKaryawan.setSelectedItem(employee.getTipe_Karyawan());
                    cboEmployeeIdDepartment.setSelectedIndex(Integer.valueOf(employee.getId_department()) - 1);
                    txtEmployeeTanggalMasuk.setText(employee.getTanggal_masuk());
                    cboEmployeeIdBank.setSelectedIndex(Integer.valueOf(employee.getIdBank()) - 1);
                    txtEmployeeGajiKotor.setText(String.valueOf(employee.getGaji_kotor()));
                    txtEmployeeTunjangan.setText(employee.getTunjangan().toString());
                    txtEmployeeNoRekening.setText(employee.getNoRekening());
                }
            }
        };
        tblEmployee.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEmployee.getSelectionModel().addListSelectionListener(listener);
    }

    private void banklistener() {
        ListSelectionListener listener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = tblBank.getSelectedRow();
                if (row >= 0) {
                    txtIdBank.setText(tblBank.getValueAt(row, 0).toString());
                    txtNamaBank.setText(tblBank.getValueAt(row, 1).toString());
                    txtCabangBank.setText(tblBank.getValueAt(row, 2).toString());
                    txtAlamatBank.setText(tblBank.getValueAt(row, 3).toString());
                }
            }
        };
        tblBank.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBank.getSelectionModel().addListSelectionListener(listener);
    }

    private void departmentListener() {
        ListSelectionListener listener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = tblDepartment.getSelectedRow();
                if (row >= 0) {
                    txtDepartmentID.setText(tblDepartment.getValueAt(row, 0).toString());
                    txtDepartmentName.setText(tblDepartment.getValueAt(row, 1).toString());
                }
            }
        };
        tblDepartment.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDepartment.getSelectionModel().addListSelectionListener(listener);
    }

    private void changeTransactionImage(boolean b) {
        if (b == true) {
            lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll2.jpg")));
        } else {
            lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll1.jpg")));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        main = new javax.swing.JPanel();
        txtID = new javax.swing.JTextField();
        btnVerify = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        admin = new javax.swing.JPanel();
        btnEmployee = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        transaction = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        createTransaction1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        createTransaction2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblNameTransaction = new javax.swing.JLabel();
        lblDepartmentTransaction = new javax.swing.JLabel();
        lblPositionTransaction = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        viewTransaction = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        bank = new javax.swing.JPanel();
        btnNewBank = new javax.swing.JButton();
        btnUpdateBank1 = new javax.swing.JButton();
        btnDeleteBank = new javax.swing.JButton();
        btnSaveToDatabaseBank = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBank = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtIdBank = new javax.swing.JTextField();
        txtNamaBank = new javax.swing.JTextField();
        txtCabangBank = new javax.swing.JTextField();
        txtAlamatBank = new javax.swing.JTextField();
        employee = new javax.swing.JPanel();
        btnEmployeeNew = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnEmployeeUpdate = new javax.swing.JButton();
        btnEmployeeDelete = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblEmployee = new javax.swing.JTable();
        txtEmployeeID = new javax.swing.JTextField();
        txtEmployeeNik = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtEmployeeName = new javax.swing.JTextField();
        cboEmployeeJenisKelamin = new javax.swing.JComboBox<>();
        txtEmployeeTempatLahir = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEmployeeAlamat = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        txtEmployeeNoHP = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtEmployeeEmail = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        cboEmployeeAgama = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        cboEmployeeStatusPerkawinan = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        cboEmployeeStatusPajak = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        cboEmployeeIdDepartment = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        cboEmployeeTipeKaryawan = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtEmployeeGajiKotor = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtEmployeeTunjangan = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        cboEmployeeIdBank = new javax.swing.JComboBox<>();
        txtEmployeeNoRekening = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txtEmployeeTangalLahir = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        txtEmployeeTanggalMasuk = new javax.swing.JTextField();
        btnEmployeeSaveToDatabase = new javax.swing.JButton();
        empty = new javax.swing.JPanel();
        department = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDepartment = new javax.swing.JTable();
        lblDepartmentName = new javax.swing.JLabel();
        btnDepartmentAdd = new javax.swing.JButton();
        btnDepartmentDelete = new javax.swing.JButton();
        txtDepartmentName = new javax.swing.JTextField();
        btnDepartmentUpdate = new javax.swing.JButton();
        lblDepartmentID = new javax.swing.JLabel();
        txtDepartmentID = new javax.swing.JTextField();
        btnDepartmentSaveToDatabase = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        btnDepartment = new javax.swing.JButton();
        btnBank = new javax.swing.JButton();
        btnTransaction = new javax.swing.JButton();
        lblBackground = new javax.swing.JLabel();
        user = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1600, 900));
        getContentPane().setLayout(null);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.CardLayout());

        main.setOpaque(false);
        main.setLayout(null);

        txtID.setFont(new java.awt.Font("Calibri", 1, 48)); // NOI18N
        txtID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtID.setBorder(null);
        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });
        main.add(txtID);
        txtID.setBounds(990, 410, 390, 50);

        btnVerify.setBorder(null);
        btnVerify.setContentAreaFilled(false);
        btnVerify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyActionPerformed(evt);
            }
        });
        main.add(btnVerify);
        btnVerify.setBounds(1053, 560, 285, 60);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll3.jpg"))); // NOI18N
        main.add(jLabel15);
        jLabel15.setBounds(0, 0, 1600, 900);

        jPanel1.add(main, "card4");

        admin.setOpaque(false);
        admin.setLayout(null);

        btnEmployee.setContentAreaFilled(false);
        btnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeActionPerformed(evt);
            }
        });
        admin.add(btnEmployee);
        btnEmployee.setBounds(110, 400, 120, 100);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.CardLayout());

        transaction.setOpaque(false);
        transaction.setLayout(null);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.CardLayout());

        createTransaction1.setOpaque(false);

        jLabel5.setText("Employee ID");

        javax.swing.GroupLayout createTransaction1Layout = new javax.swing.GroupLayout(createTransaction1);
        createTransaction1.setLayout(createTransaction1Layout);
        createTransaction1Layout.setHorizontalGroup(
            createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction1Layout.createSequentialGroup()
                .addGap(228, 228, 228)
                .addComponent(jLabel5)
                .addGap(60, 60, 60)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(763, Short.MAX_VALUE))
        );
        createTransaction1Layout.setVerticalGroup(
            createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction1Layout.createSequentialGroup()
                .addGap(333, 333, 333)
                .addGroup(createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(387, Short.MAX_VALUE))
        );

        jPanel4.add(createTransaction1, "card2");

        createTransaction2.setOpaque(false);

        jLabel6.setText("Name:");

        jLabel7.setText("Department:");

        jLabel8.setText("Position:");

        lblNameTransaction.setText("jLabel9");

        lblDepartmentTransaction.setText("jLabel10");

        lblPositionTransaction.setText("jLabel11");

        jLabel9.setText("Total Absent");

        jLabel10.setText("Gross salary:");

        jLabel11.setText("Allowance:");

        jLabel12.setText("jLabel12");

        jLabel13.setText("jLabel13");

        jLabel14.setText("Deduction");

        javax.swing.GroupLayout createTransaction2Layout = new javax.swing.GroupLayout(createTransaction2);
        createTransaction2.setLayout(createTransaction2Layout);
        createTransaction2Layout.setHorizontalGroup(
            createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction2Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(42, 42, 42)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(createTransaction2Layout.createSequentialGroup()
                        .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNameTransaction, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDepartmentTransaction, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPositionTransaction, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(244, 244, 244)
                        .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addGap(53, 53, 53)
                        .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(724, Short.MAX_VALUE))
        );
        createTransaction2Layout.setVerticalGroup(
            createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction2Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblNameTransaction)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblDepartmentTransaction)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblPositionTransaction))
                .addGap(124, 124, 124)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(407, Short.MAX_VALUE))
        );

        jPanel4.add(createTransaction2, "card4");

        viewTransaction.setOpaque(false);

        javax.swing.GroupLayout viewTransactionLayout = new javax.swing.GroupLayout(viewTransaction);
        viewTransaction.setLayout(viewTransactionLayout);
        viewTransactionLayout.setHorizontalGroup(
            viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1350, Short.MAX_VALUE)
        );
        viewTransactionLayout.setVerticalGroup(
            viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );

        jPanel4.add(viewTransaction, "card3");

        transaction.add(jPanel4);
        jPanel4.setBounds(0, 150, 1350, 750);

        jButton9.setText("new Transaction");
        transaction.add(jButton9);
        jButton9.setBounds(130, 23, 80, 90);

        jButton1.setText("view transaction");
        transaction.add(jButton1);
        jButton1.setBounds(440, 23, 90, 90);

        jPanel2.add(transaction, "card3");

        bank.setOpaque(false);

        btnNewBank.setContentAreaFilled(false);
        btnNewBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewBankActionPerformed(evt);
            }
        });

        btnUpdateBank1.setContentAreaFilled(false);
        btnUpdateBank1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateBank1ActionPerformed(evt);
            }
        });

        btnDeleteBank.setContentAreaFilled(false);
        btnDeleteBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteBankActionPerformed(evt);
            }
        });

        btnSaveToDatabaseBank.setContentAreaFilled(false);
        btnSaveToDatabaseBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveToDatabaseBankActionPerformed(evt);
            }
        });

        tblBank.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Bank", "Bank", "Cabang Bank", "Alamat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblBank);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Nama Bank :");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("ID Bank :");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Cabang Bank :");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Alamat :");

        javax.swing.GroupLayout bankLayout = new javax.swing.GroupLayout(bank);
        bank.setLayout(bankLayout);
        bankLayout.setHorizontalGroup(
            bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bankLayout.createSequentialGroup()
                .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bankLayout.createSequentialGroup()
                        .addGap(174, 174, 174)
                        .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(bankLayout.createSequentialGroup()
                                .addComponent(txtNamaBank, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel21))
                            .addGroup(bankLayout.createSequentialGroup()
                                .addComponent(txtIdBank, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAlamatBank, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(txtCabangBank)))
                    .addGroup(bankLayout.createSequentialGroup()
                        .addGap(155, 155, 155)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(bankLayout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(btnNewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(244, 244, 244)
                .addComponent(btnUpdateBank1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 233, Short.MAX_VALUE)
                .addComponent(btnDeleteBank, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(260, 260, 260)
                .addComponent(btnSaveToDatabaseBank, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101))
        );
        bankLayout.setVerticalGroup(
            bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bankLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bankLayout.createSequentialGroup()
                        .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnUpdateBank1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(148, 148, 148)
                        .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(txtIdBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCabangBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel21)
                            .addComponent(txtNamaBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAlamatBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 217, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(126, 126, 126))
                    .addGroup(bankLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDeleteBank, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                            .addComponent(btnSaveToDatabaseBank, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel2.add(bank, "card5");

        employee.setOpaque(false);

        btnEmployeeNew.setContentAreaFilled(false);
        btnEmployeeNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeNewActionPerformed(evt);
            }
        });

        jLabel2.setText("ID karyawan");

        btnEmployeeUpdate.setContentAreaFilled(false);
        btnEmployeeUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeUpdateActionPerformed(evt);
            }
        });

        btnEmployeeDelete.setContentAreaFilled(false);
        btnEmployeeDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeDeleteActionPerformed(evt);
            }
        });

        tblEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Karyawan", "Nama", "NIK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblEmployee);
        if (tblEmployee.getColumnModel().getColumnCount() > 0) {
            tblEmployee.getColumnModel().getColumn(0).setResizable(false);
            tblEmployee.getColumnModel().getColumn(0).setPreferredWidth(1);
            tblEmployee.getColumnModel().getColumn(1).setResizable(false);
            tblEmployee.getColumnModel().getColumn(1).setPreferredWidth(60);
            tblEmployee.getColumnModel().getColumn(2).setResizable(false);
        }

        jLabel3.setText("NIK");

        jLabel4.setText("Jenis kelamin");

        jLabel16.setText("Nama");

        txtEmployeeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmployeeNameActionPerformed(evt);
            }
        });

        cboEmployeeJenisKelamin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PRIA", "WANITA" }));

        jLabel17.setText("Tempat Lahir");

        txtEmployeeAlamat.setColumns(5);
        txtEmployeeAlamat.setRows(3);
        jScrollPane1.setViewportView(txtEmployeeAlamat);

        jLabel22.setText("Alamat");

        jLabel23.setText("No HP");

        jLabel24.setText("Email");

        cboEmployeeAgama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Buddha", "Kristen", "Islam", "Hindu" }));

        jLabel25.setText("Agama");

        cboEmployeeStatusPerkawinan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lajang", "Menikah" }));

        jLabel26.setText("Status Perkawinan");

        cboEmployeeStatusPajak.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TK0", "TK1", "TK2", "TK3", "K0", "K1", "K2", "K3" }));

        jLabel27.setText("Status Pajak");

        cboEmployeeIdDepartment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel28.setText("Department");

        cboEmployeeTipeKaryawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Harian", "Bulanan" }));

        jLabel29.setText("Tipe Karyawan");

        jLabel30.setText("Tanggal Masuk");

        jLabel31.setText("Gaji Kotor");

        jLabel32.setText("Tunjangan");

        jLabel33.setText("Bank");

        cboEmployeeIdBank.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel34.setText("No Rekening");

        jLabel35.setText("Tanggal Lahir");

        btnEmployeeSaveToDatabase.setContentAreaFilled(false);
        btnEmployeeSaveToDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeSaveToDatabaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout employeeLayout = new javax.swing.GroupLayout(employee);
        employee.setLayout(employeeLayout);
        employeeLayout.setHorizontalGroup(
            employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(employeeLayout.createSequentialGroup()
                .addGap(129, 129, 129)
                .addComponent(btnEmployeeNew, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(249, 249, 249)
                .addComponent(btnEmployeeUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(230, 230, 230)
                .addComponent(btnEmployeeDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEmployeeSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
            .addGroup(employeeLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(employeeLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29))
                        .addGap(18, 18, 18)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtEmployeeNoHP)
                                .addComponent(txtEmployeeEmail)
                                .addComponent(cboEmployeeStatusPerkawinan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboEmployeeAgama, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboEmployeeStatusPajak, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboEmployeeTipeKaryawan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboEmployeeIdDepartment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(employeeLayout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel16)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel17)
                            .addComponent(jLabel35))
                        .addGap(18, 18, 18)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmployeeTangalLahir)
                            .addComponent(txtEmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmployeeName)
                            .addComponent(txtEmployeeNik)
                            .addComponent(txtEmployeeTempatLahir)
                            .addComponent(cboEmployeeJenisKelamin, 0, 171, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, employeeLayout.createSequentialGroup()
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtEmployeeTunjangan)
                                    .addComponent(txtEmployeeGajiKotor)
                                    .addComponent(txtEmployeeNoRekening, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                                    .addComponent(cboEmployeeIdBank, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, employeeLayout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(18, 18, 18)
                                .addComponent(txtEmployeeTanggalMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(116, 116, 116))))
        );
        employeeLayout.setVerticalGroup(
            employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(employeeLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEmployeeSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEmployeeNew, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnEmployeeDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(employeeLayout.createSequentialGroup()
                            .addComponent(btnEmployeeUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(8, 8, 8))))
                .addGap(92, 92, 92)
                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, employeeLayout.createSequentialGroup()
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtEmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30)
                            .addComponent(txtEmployeeTanggalMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(employeeLayout.createSequentialGroup()
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmployeeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmployeeNik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(cboEmployeeJenisKelamin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmployeeTempatLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17)))
                            .addGroup(employeeLayout.createSequentialGroup()
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmployeeGajiKotor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel31))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmployeeTunjangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel33)
                                    .addComponent(cboEmployeeIdBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmployeeNoRekening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel34))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmployeeTangalLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmployeeNoHP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmployeeEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboEmployeeAgama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboEmployeeStatusPerkawinan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboEmployeeStatusPajak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboEmployeeIdDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboEmployeeTipeKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29))))
                .addContainerGap(204, Short.MAX_VALUE))
        );

        jPanel2.add(employee, "card2");

        empty.setOpaque(false);
        empty.setLayout(null);
        jPanel2.add(empty, "card4");

        department.setOpaque(false);

        tblDepartment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Department ID", "Department Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblDepartment);

        lblDepartmentName.setText("Department Name");

        btnDepartmentAdd.setContentAreaFilled(false);
        btnDepartmentAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepartmentAddActionPerformed(evt);
            }
        });

        btnDepartmentDelete.setContentAreaFilled(false);
        btnDepartmentDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepartmentDeleteActionPerformed(evt);
            }
        });

        btnDepartmentUpdate.setContentAreaFilled(false);
        btnDepartmentUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepartmentUpdateActionPerformed(evt);
            }
        });

        lblDepartmentID.setText("Department ID");

        btnDepartmentSaveToDatabase.setContentAreaFilled(false);
        btnDepartmentSaveToDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepartmentSaveToDatabaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout departmentLayout = new javax.swing.GroupLayout(department);
        department.setLayout(departmentLayout);
        departmentLayout.setHorizontalGroup(
            departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(departmentLayout.createSequentialGroup()
                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(departmentLayout.createSequentialGroup()
                        .addGap(262, 262, 262)
                        .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDepartmentName)
                            .addComponent(lblDepartmentID))
                        .addGap(51, 51, 51)
                        .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDepartmentName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDepartmentID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(departmentLayout.createSequentialGroup()
                        .addGap(239, 239, 239)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(680, Short.MAX_VALUE))
            .addGroup(departmentLayout.createSequentialGroup()
                .addGap(128, 128, 128)
                .addComponent(btnDepartmentAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(245, 245, 245)
                .addComponent(btnDepartmentUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(235, 235, 235)
                .addComponent(btnDepartmentDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDepartmentSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102))
        );
        departmentLayout.setVerticalGroup(
            departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(departmentLayout.createSequentialGroup()
                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(departmentLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(departmentLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(btnDepartmentAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnDepartmentUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, departmentLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(btnDepartmentDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepartmentID)
                    .addComponent(txtDepartmentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepartmentName)
                    .addComponent(txtDepartmentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(105, 105, 105)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(258, 258, 258))
            .addGroup(departmentLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(btnDepartmentSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(department, "card6");

        admin.add(jPanel2);
        jPanel2.setBounds(320, 0, 1280, 900);

        jButton7.setContentAreaFilled(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        admin.add(jButton7);
        jButton7.setBounds(134, 33, 80, 80);

        btnDepartment.setContentAreaFilled(false);
        btnDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepartmentActionPerformed(evt);
            }
        });
        admin.add(btnDepartment);
        btnDepartment.setBounds(109, 543, 110, 110);

        btnBank.setContentAreaFilled(false);
        btnBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBankActionPerformed(evt);
            }
        });
        admin.add(btnBank);
        btnBank.setBounds(110, 690, 110, 100);

        btnTransaction.setContentAreaFilled(false);
        btnTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionActionPerformed(evt);
            }
        });
        admin.add(btnTransaction);
        btnTransaction.setBounds(110, 250, 130, 120);

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll1.jpg"))); // NOI18N
        admin.add(lblBackground);
        lblBackground.setBounds(0, 0, 1600, 900);

        jPanel1.add(admin, "card3");

        user.setOpaque(false);

        jButton5.setText("date");

        jLabel1.setText("user page");

        jButton6.setText("back");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout userLayout = new javax.swing.GroupLayout(user);
        user.setLayout(userLayout);
        userLayout.setHorizontalGroup(
            userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userLayout.createSequentialGroup()
                .addGroup(userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(userLayout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(jButton5))
                    .addGroup(userLayout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(jLabel1))
                    .addGroup(userLayout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jButton6)))
                .addContainerGap(1387, Short.MAX_VALUE))
        );
        userLayout.setVerticalGroup(
            userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGap(38, 38, 38)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 726, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addGap(48, 48, 48))
        );

        jPanel1.add(user, "card2");

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 1600, 900);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed
        if (txtID.getText().equals("exit")) {
            System.exit(0);
        }
        if (txtID.getText().equals("admin")) {
            changeMainLayout(admin);
        }
    }//GEN-LAST:event_btnVerifyActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        changeMainLayout(main);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        changeMainLayout(main);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnEmployeeNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeNewActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) tblEmployee.getModel();
        if (!txtEmployeeID.getText().trim().isEmpty()
                && !txtEmployeeName.getText().trim().isEmpty()
                && !txtEmployeeNik.getText().trim().isEmpty()
                && !txtEmployeeAlamat.getText().trim().isEmpty()
                && !txtEmployeeEmail.getText().trim().isEmpty()
                && !txtEmployeeGajiKotor.getText().trim().isEmpty()
                && !txtEmployeeNoHP.getText().trim().isEmpty()
                && !txtEmployeeNoRekening.getText().trim().isEmpty()
                && !txtEmployeeTangalLahir.getText().trim().isEmpty()
                && !txtEmployeeTanggalMasuk.getText().trim().isEmpty()
                && !txtEmployeeTempatLahir.getText().trim().isEmpty()
                && !txtEmployeeTunjangan.getText().trim().isEmpty()) {

            Integer id = Integer.valueOf(txtEmployeeID.getText());
            String name = txtEmployeeName.getText().toUpperCase();
            String nik = txtEmployeeNik.getText();
            String jenisKelamin = cboEmployeeJenisKelamin.getSelectedItem().toString();
            String tempatLahir = txtEmployeeTempatLahir.getText().toUpperCase();
            String tanggalLahir = txtEmployeeTangalLahir.getText();
            String alamat = txtEmployeeAlamat.getText();
            String noHp = txtEmployeeNoHP.getText();
            String email = txtEmployeeEmail.getText();
            String agama = cboEmployeeAgama.getSelectedItem().toString();
            String status_perkawinan = cboEmployeeStatusPerkawinan.getSelectedItem().toString();
            String status_pajak = cboEmployeeStatusPajak.getSelectedItem().toString();
            String id_department = String.valueOf(cboEmployeeIdDepartment.getSelectedIndex() + 1);
            String tipe_Karyawan = cboEmployeeTipeKaryawan.getSelectedItem().toString();
            String tanggal_masuk = txtEmployeeTanggalMasuk.getText();
            Double gaji_kotor = Double.valueOf(txtEmployeeGajiKotor.getText());
            Double tunjangan = Double.valueOf(txtEmployeeTunjangan.getText());
            String idBank = String.valueOf(cboEmployeeIdBank.getSelectedIndex() + 1);
            String noRekening = txtEmployeeNoRekening.getText();
            employeeList.add(new Employee(id, name, nik, jenisKelamin, tempatLahir, tanggalLahir, alamat, noHp, email, agama,
                    status_perkawinan, status_pajak, id_department, tipe_Karyawan,
                    tanggal_masuk, gaji_kotor, tunjangan, idBank, noRekening));

            Object data[] = {id, name, nik};
            tableModel.addRow(data);

            txtEmployeeID.setText("");
            txtEmployeeName.setText("");
            txtEmployeeNik.setText("");
            txtEmployeeTempatLahir.setText("");
            txtEmployeeTangalLahir.setText("");
            txtEmployeeAlamat.setText("");
            txtEmployeeNoHP.setText("");
            txtEmployeeEmail.setText("");
            txtEmployeeTanggalMasuk.setText("");
            txtEmployeeGajiKotor.setText("");
            txtEmployeeTunjangan.setText("");
            txtEmployeeNoRekening.setText("");
        }
    }//GEN-LAST:event_btnEmployeeNewActionPerformed

    private void btnEmployeeUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeUpdateActionPerformed
        int row = tblEmployee.getSelectedRow();
        if (tblEmployee.getSelectedRow() >= 0) {
            if (!txtEmployeeID.getText().trim().isEmpty()
                    && !txtEmployeeName.getText().trim().isEmpty()
                    && !txtEmployeeNik.getText().trim().isEmpty()
                    && !txtEmployeeAlamat.getText().trim().isEmpty()
                    && !txtEmployeeEmail.getText().trim().isEmpty()
                    && !txtEmployeeGajiKotor.getText().trim().isEmpty()
                    && !txtEmployeeNoHP.getText().trim().isEmpty()
                    && !txtEmployeeNoRekening.getText().trim().isEmpty()
                    && !txtEmployeeTangalLahir.getText().trim().isEmpty()
                    && !txtEmployeeTanggalMasuk.getText().trim().isEmpty()
                    && !txtEmployeeTempatLahir.getText().trim().isEmpty()
                    && !txtEmployeeTunjangan.getText().trim().isEmpty()) {

                DefaultTableModel tableModel = (DefaultTableModel) tblEmployee.getModel();
                Employee employee = employeeList.get(tblEmployee.getSelectedRow());

                Integer id = Integer.valueOf(txtEmployeeID.getText());
                String name = txtEmployeeName.getText().toUpperCase();
                String nik = txtEmployeeNik.getText();
                String jenisKelamin = cboEmployeeJenisKelamin.getSelectedItem().toString();
                String tempatLahir = txtEmployeeTempatLahir.getText().toUpperCase();
                String tanggalLahir = txtEmployeeTangalLahir.getText();
                String alamat = txtEmployeeAlamat.getText();
                String noHp = txtEmployeeNoHP.getText();
                String email = txtEmployeeEmail.getText();
                String agama = cboEmployeeAgama.getSelectedItem().toString();
                String status_perkawinan = cboEmployeeStatusPerkawinan.getSelectedItem().toString();
                String status_pajak = cboEmployeeStatusPajak.getSelectedItem().toString();
                String id_department = String.valueOf(cboEmployeeIdDepartment.getSelectedIndex() + 1);
                String tipe_Karyawan = cboEmployeeTipeKaryawan.getSelectedItem().toString();
                String tanggal_masuk = txtEmployeeTanggalMasuk.getText();
                Double gaji_kotor = Double.valueOf(txtEmployeeGajiKotor.getText());
                Double tunjangan = Double.valueOf(txtEmployeeTunjangan.getText());
                String idBank = String.valueOf(cboEmployeeIdBank.getSelectedIndex() + 1);
                String noRekening = txtEmployeeNoRekening.getText();

                employee.setId(id);
                employee.setName(name);
                employee.setNik(nik);
                employee.setJenisKelamin(jenisKelamin);
                employee.setTempatLahir(tempatLahir);
                employee.setTanggalLahir(tanggalLahir);
                employee.setAlamat(alamat);
                employee.setNoHp(noHp);
                employee.setEmail(email);
                employee.setAgama(agama);
                employee.setStatus_perkawinan(status_perkawinan);
                employee.setStatus_pajak(status_pajak);
                employee.setId_department(id_department);
                employee.setTipe_Karyawan(tipe_Karyawan);
                employee.setTanggal_masuk(tanggal_masuk);
                employee.setGaji_kotor(gaji_kotor);
                employee.setTunjangan(tunjangan);
                employee.setIdBank(idBank);
                employee.setNoRekening(noRekening);

                tableModel.setValueAt(employee.getId(), row, 0);
                tableModel.setValueAt(employee.getName(), row, 1);

                txtEmployeeID.setText("");
                txtEmployeeName.setText("");
                txtEmployeeNik.setText("");
                txtEmployeeTempatLahir.setText("");
                txtEmployeeTangalLahir.setText("");
                txtEmployeeAlamat.setText("");
                txtEmployeeNoHP.setText("");
                txtEmployeeEmail.setText("");
                txtEmployeeTanggalMasuk.setText("");
                txtEmployeeGajiKotor.setText("");
                txtEmployeeTunjangan.setText("");
                txtEmployeeNoRekening.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select the data", "App Information", 1);
        }
    }//GEN-LAST:event_btnEmployeeUpdateActionPerformed

    private void btnEmployeeDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeDeleteActionPerformed
        int row = tblEmployee.getSelectedRow();
        DefaultTableModel tableModel = (DefaultTableModel) tblEmployee.getModel();
        if (tblEmployee.getSelectedRow() >= 0) {
            tableModel.removeRow(row);
            employeeList.remove(row);
        } else {
            JOptionPane.showMessageDialog(this, "Please select the data", "App Information", 1);
        }
    }//GEN-LAST:event_btnEmployeeDeleteActionPerformed

    private void btnDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepartmentActionPerformed
        changeMainSubLayout(department);
        changeTransactionImage(false);
        tableDepartmentInitData();
    }//GEN-LAST:event_btnDepartmentActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        changeMainSubLayout(employee);
        changeTransactionImage(false);
        tableEmployeeInitData();
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBankActionPerformed
        changeMainSubLayout(bank);
        changeTransactionImage(false);
        tableBankInitData();
    }//GEN-LAST:event_btnBankActionPerformed

    private void btnTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionActionPerformed
        changeMainSubLayout(transaction);
        changeTransactionImage(true);
    }//GEN-LAST:event_btnTransactionActionPerformed

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDActionPerformed


    private void btnNewBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewBankActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) tblBank.getModel();
        if (!txtNamaBank.getText().trim().isEmpty() && !txtIdBank.getText().trim().isEmpty() && !txtCabangBank.getText().trim().isEmpty() && !txtAlamatBank.getText().trim().isEmpty()) {

            Object data[] = {txtIdBank.getText(),
                txtNamaBank.getText().toUpperCase(),
                txtCabangBank.getText(),
                txtAlamatBank.getText()};
            tableModel.addRow(data);

            txtIdBank.setText("");
            txtNamaBank.setText("");
            txtCabangBank.setText("");
            txtAlamatBank.setText("");
        }
    }//GEN-LAST:event_btnNewBankActionPerformed

    private void btnUpdateBank1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateBank1ActionPerformed
        int row = tblBank.getSelectedRow();
        if (tblBank.getSelectedRow() >= 0) {
            if (!txtNamaBank.getText().trim().isEmpty() && !txtIdBank.getText().trim().isEmpty() && !txtCabangBank.getText().trim().isEmpty() && !txtAlamatBank.getText().trim().isEmpty()) {
                DefaultTableModel tableModel = (DefaultTableModel) tblBank.getModel();
                tableModel.setValueAt(txtIdBank.getText(), row, 0);
                tableModel.setValueAt(txtNamaBank.getText().toUpperCase(), row, 1);
                tableModel.setValueAt(txtCabangBank, row, 2);
                tableModel.setValueAt(txtAlamatBank, row, 3);
                txtIdBank.setText("");
                txtNamaBank.setText("");
                txtCabangBank.setText("");
                txtAlamatBank.setText("");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please select the data", "App Information", 1);
        }
    }//GEN-LAST:event_btnUpdateBank1ActionPerformed

    private void btnDeleteBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBankActionPerformed
        int row = tblBank.getSelectedRow();
        DefaultTableModel tableModel = (DefaultTableModel) tblBank.getModel();
        if (tblBank.getSelectedRow() >= 0) {

            tableModel.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "Please select the data", "App Information", 1);
        }
    }//GEN-LAST:event_btnDeleteBankActionPerformed

    private void txtEmployeeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmployeeNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmployeeNameActionPerformed

    private void btnDepartmentAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepartmentAddActionPerformed
        if (!txtDepartmentName.getText().trim().isEmpty() && !txtDepartmentID.getText().trim().isEmpty()) {
            Object data[] = {txtDepartmentID.getText().toUpperCase(),
                txtDepartmentName.getText().toUpperCase()};
            DefaultTableModel tableModel = (DefaultTableModel) tblDepartment.getModel();
            tableModel.addRow(data);
            txtDepartmentID.setText("");
            txtDepartmentName.setText("");
        }
    }//GEN-LAST:event_btnDepartmentAddActionPerformed

    private void btnDepartmentDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepartmentDeleteActionPerformed
        // TODO add your handling code here:
        int row = tblDepartment.getSelectedRow();
        if (tblDepartment.getSelectedRow() >= 0) {
            DefaultTableModel tableModel = (DefaultTableModel) tblDepartment.getModel();
            tableModel.removeRow(row);
            txtDepartmentID.setText("");
            txtDepartmentName.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please Select Data", "App Information", 1);
        }
    }//GEN-LAST:event_btnDepartmentDeleteActionPerformed

    private void btnDepartmentUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepartmentUpdateActionPerformed
        // TODO add your handling code here:
        if (tblDepartment.getSelectedRow() >= 0) {
            int row = tblDepartment.getSelectedRow();
            DefaultTableModel tableModel = (DefaultTableModel) tblDepartment.getModel();
            tableModel.setValueAt(txtDepartmentID.getText().toUpperCase(), row, 0);
            tableModel.setValueAt(txtDepartmentName.getText().toUpperCase(), row, 1);
        } else {
            JOptionPane.showMessageDialog(this, "Please Select Data", "App Information", 1);
        }
    }//GEN-LAST:event_btnDepartmentUpdateActionPerformed

    private void btnSaveToDatabaseBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveToDatabaseBankActionPerformed

        try {

            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("delete from bank");
            // Execute SQL query
            myStmt.executeUpdate();
            for (int i = 0; i < tblBank.getRowCount(); i++) {
                // Prepare statement

                myStmt = myConn
                        .prepareStatement("insert into bank values (?,?,?,?)");

                myStmt.setInt(1, Integer.valueOf(tblBank.getValueAt(i, 0).toString()));
                myStmt.setString(2, tblBank.getValueAt(i, 1).toString());
                myStmt.setString(3, tblBank.getValueAt(i, 2).toString());
                myStmt.setString(4, tblBank.getValueAt(i, 3).toString());

                // Execute SQL query
                myStmt.executeUpdate();

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Please Ensure there is no item\n"
                    + " with the same id", "App Information", 1);
            Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btnSaveToDatabaseBankActionPerformed

    private void btnEmployeeSaveToDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeSaveToDatabaseActionPerformed
        try {

            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("delete from karyawan");
            // Execute SQL query
            myStmt.executeUpdate();
            for (Employee e : employeeList) {
                // Prepare statement

                myStmt = myConn.prepareStatement("insert into karyawan values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                myStmt.setInt(1, e.getId());
                myStmt.setString(2, e.getNik());
                myStmt.setString(3, e.getName());
                myStmt.setString(4, e.getJenisKelamin());
                myStmt.setString(5, e.getTempatLahir());
                myStmt.setString(6, e.getTanggalLahir());
                myStmt.setString(7, e.getAlamat());
                myStmt.setString(8, e.getNoHp());
                myStmt.setString(9, e.getEmail());
                myStmt.setString(10, e.getAgama());
                myStmt.setString(11, e.getStatus_perkawinan());
                myStmt.setString(12, e.getStatus_pajak());
                myStmt.setString(13, e.getId_department());
                myStmt.setString(14, e.getTipe_Karyawan());
                myStmt.setString(15, e.getTanggal_masuk());
                myStmt.setDouble(16, e.getGaji_kotor());
                myStmt.setDouble(17, e.getTunjangan());
                myStmt.setString(18, e.getIdBank());
                myStmt.setString(19, e.getNoRekening());

                // Execute SQL query
                myStmt.executeUpdate();

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Please Ensure there is no item\n"
                    + " with the same id", "App Information", 1);
            Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnEmployeeSaveToDatabaseActionPerformed

    private void btnDepartmentSaveToDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepartmentSaveToDatabaseActionPerformed
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("delete from departments");

            // Execute SQL query
            myStmt.executeUpdate();
            for (int i = 0; i < tblDepartment.getRowCount(); i++) {
                // Prepare statement

                myStmt = myConn.prepareStatement("insert into departments values (?,?)");

                myStmt.setInt(1, Integer.valueOf(tblDepartment.getValueAt(i, 0).toString()));
                myStmt.setString(2, tblDepartment.getValueAt(i, 1).toString());

                // Execute SQL query
                myStmt.executeUpdate();

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Please Ensure there is no item\n"
                    + " with the same id", "App Information", 1);
            Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnDepartmentSaveToDatabaseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmMainPayroll().setVisible(true);
            }
        });
    }

    private void changeMainLayout(Component comp) {
        jPanel1.removeAll();
        jPanel1.repaint();
        jPanel1.revalidate();

        jPanel1.add(comp);
        jPanel1.repaint();
        jPanel1.revalidate();
    }

    private void changeMainSubLayout(Component comp) {
        jPanel2.removeAll();
        jPanel2.repaint();
        jPanel2.revalidate();

        jPanel2.add(comp);
        jPanel2.repaint();
        jPanel2.revalidate();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel admin;
    private javax.swing.JPanel bank;
    private javax.swing.JButton btnBank;
    private javax.swing.JButton btnDeleteBank;
    private javax.swing.JButton btnDepartment;
    private javax.swing.JButton btnDepartmentAdd;
    private javax.swing.JButton btnDepartmentDelete;
    private javax.swing.JButton btnDepartmentSaveToDatabase;
    private javax.swing.JButton btnDepartmentUpdate;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnEmployeeDelete;
    private javax.swing.JButton btnEmployeeNew;
    private javax.swing.JButton btnEmployeeSaveToDatabase;
    private javax.swing.JButton btnEmployeeUpdate;
    private javax.swing.JButton btnNewBank;
    private javax.swing.JButton btnSaveToDatabaseBank;
    private javax.swing.JButton btnTransaction;
    private javax.swing.JButton btnUpdateBank1;
    private javax.swing.JButton btnVerify;
    private javax.swing.JComboBox<String> cboEmployeeAgama;
    private javax.swing.JComboBox<String> cboEmployeeIdBank;
    private javax.swing.JComboBox<String> cboEmployeeIdDepartment;
    private javax.swing.JComboBox<String> cboEmployeeJenisKelamin;
    private javax.swing.JComboBox<String> cboEmployeeStatusPajak;
    private javax.swing.JComboBox<String> cboEmployeeStatusPerkawinan;
    private javax.swing.JComboBox<String> cboEmployeeTipeKaryawan;
    private javax.swing.JPanel createTransaction1;
    private javax.swing.JPanel createTransaction2;
    private javax.swing.JPanel department;
    private javax.swing.JPanel employee;
    private javax.swing.JPanel empty;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblDepartmentID;
    private javax.swing.JLabel lblDepartmentName;
    private javax.swing.JLabel lblDepartmentTransaction;
    private javax.swing.JLabel lblNameTransaction;
    private javax.swing.JLabel lblPositionTransaction;
    private javax.swing.JPanel main;
    private javax.swing.JTable tblBank;
    private javax.swing.JTable tblDepartment;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JPanel transaction;
    private javax.swing.JTextField txtAlamatBank;
    private javax.swing.JTextField txtCabangBank;
    private javax.swing.JTextField txtDepartmentID;
    private javax.swing.JTextField txtDepartmentName;
    private javax.swing.JTextArea txtEmployeeAlamat;
    private javax.swing.JTextField txtEmployeeEmail;
    private javax.swing.JTextField txtEmployeeGajiKotor;
    private javax.swing.JTextField txtEmployeeID;
    private javax.swing.JTextField txtEmployeeName;
    private javax.swing.JTextField txtEmployeeNik;
    private javax.swing.JTextField txtEmployeeNoHP;
    private javax.swing.JTextField txtEmployeeNoRekening;
    private javax.swing.JTextField txtEmployeeTangalLahir;
    private javax.swing.JTextField txtEmployeeTanggalMasuk;
    private javax.swing.JTextField txtEmployeeTempatLahir;
    private javax.swing.JTextField txtEmployeeTunjangan;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtIdBank;
    private javax.swing.JTextField txtNamaBank;
    private javax.swing.JPanel user;
    private javax.swing.JPanel viewTransaction;
    // End of variables declaration//GEN-END:variables
}
