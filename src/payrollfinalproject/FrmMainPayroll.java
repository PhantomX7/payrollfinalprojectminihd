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
import util.SUtility;

/**
 *
 * @author Phantom
 */
public class FrmMainPayroll extends javax.swing.JFrame {

    Connection myConn = null;
    PreparedStatement myStmt = null;
    ResultSet myRs = null;
    ArrayList<Employee> employeeList = null;
    boolean foundEmployee = false;

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
        tableModel.setRowCount(0);

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

    private void tableTransactionInitData(String month, String year) {
        DefaultTableModel tableModel = (DefaultTableModel) tblTransaction.getModel();
        tableModel.setRowCount(0);

        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from transaksi where periode_payroll=? and periode_pengajian=?");

            myStmt.setString(1, year);
            myStmt.setString(2, month);
            // Execute SQL query
            myRs = myStmt.executeQuery();
            // Process result set
            if (myRs.isBeforeFirst()) {
                while (myRs.next()) {
                    Object data[] = {myRs.getInt("id_transaksi"), myRs.getString("karyawan"), myRs.getString("departemen"),
                        myRs.getString("jabatan"), myRs.getInt("total_absen"), myRs.getDouble("gaji_kotor"), myRs.getDouble("tunjangan"),
                        myRs.getDouble("potongan"), myRs.getDouble("total_THP"), myRs.getString("bank"), myRs.getString("no_rekening")};
                    tableModel.addRow(data);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No transaction data", "Error", 1);
            }

        } catch (SQLException ex) {

            Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tableDepartmentInitData() {

        DefaultTableModel tableModel = (DefaultTableModel) tblDepartment.getModel();
        tableModel.setRowCount(0);

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
        tableModel.setRowCount(0);

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
            @Override
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
            @Override
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
            @Override
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

    private void verifyEmployee() {
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from data_karyawan where nik=?");
            myStmt.setString(1, txtID.getText());
            // Execute SQL query
            myRs = myStmt.executeQuery();

            // Process result set
            if (myRs.isBeforeFirst()) {
                while (myRs.next()) {
                    foundEmployee = true;
                    String nik = myRs.getString("nik");
                    String name = myRs.getString("nama");
                    lblUserNik.setText(String.valueOf(nik));
                    lblUserName.setText(name);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verifyTransaction() {

        String nama = "";
        String department = "";
        String jabatan = "";
        Double gajikotor = 0.0;
        Double tunjangan = 0.0;
        String bank = "";
        String noRekening = "";
        String tipeKaryawan = "";
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from data_karyawan where nik=?");
            myStmt.setString(1, txtEmployeeTransaction.getText());
            // Execute SQL query
            myRs = myStmt.executeQuery();

            // Process result set
            if (myRs.isBeforeFirst()) {
                while (myRs.next()) {
                    foundEmployee = true;
                    String nik = myRs.getString("nik");
                    nama = myRs.getString("nama");
                    department = myRs.getString("department");
                    jabatan = myRs.getString("jabatan");
                    gajikotor = myRs.getDouble("gaji_kotor");
                    tunjangan = myRs.getDouble("tunjangan");
                    bank = myRs.getString("bank");
                    noRekening = myRs.getString("no_rekening");
                    tipeKaryawan = myRs.getString("tipe_karyawan");

                }
            }
            lblNameTransaction.setText(nama);
            lblDepartmentTransaction.setText(department);
            lblPositionTransaction.setText(jabatan);
            lblTunjanganTransaction.setText(Double.toString(tunjangan));
            lblGajiKotorTransaction.setText(String.format("%.2f", gajikotor));
            lblNameTransaction1.setText(nama);
            lblDepartmentTransaction1.setText(department);
            lblPositionTransaction1.setText(jabatan);
            lblTunjanganTransaction1.setText(Double.toString(tunjangan));
            lblGajiKotorTransaction1.setText(String.format("%.2f", gajikotor));
            lblTransactionBank.setText(bank);
            lblTransactionNoRekening.setText(noRekening);
            lblTransactionTipeKaryawan.setText(tipeKaryawan);
            lblTransactionTipeKaryawan1.setText(tipeKaryawan);

        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void changeTransactionImage(int b) {
        if (b == 1) {
            lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll1.jpg")));
        } else if (b == 2) {
            lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll2.jpg")));
        } else {
            lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll5.jpg")));
        }
    }

    private void checkpayroll() {
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from transaksi where karyawan=? and periode_pengajian =? and periode_payroll=?");
            myStmt.setString(1, lblUserName.getText());
            myStmt.setString(2, String.valueOf(cboUserMonth.getSelectedItem()));
            myStmt.setString(3, String.valueOf(cboUserYear.getSelectedItem()));

            // Execute SQL query
            myRs = myStmt.executeQuery();

            // Process result set
            if (myRs.isBeforeFirst()) {
                while (myRs.next()) {
                    String name = myRs.getString("karyawan");
                    String departemen = myRs.getString("departemen");
                    String jabatan = myRs.getString("jabatan");
                    Integer gaji = myRs.getInt("gaji_kotor");
                    Integer tunjangan = myRs.getInt("tunjangan");
                    Integer potongan = myRs.getInt("potongan");
                    Integer jumlah = myRs.getInt("total_THP");
                    lblUserTanggal.setText(String.valueOf(new java.util.Date()));
                    lblUserBulanTahun.setText(String.valueOf(cboUserMonth.getSelectedItem()) + " - " + String.valueOf(cboUserYear.getSelectedItem()));
                    lblUserNama.setText(name);
                    lblUserJabatan.setText(jabatan);
                    lblUserDepartment.setText(departemen);
                    lblUserGaji.setText(String.valueOf(gaji));
                    lblUserTunjangan.setText(String.valueOf(tunjangan));
                    lblUserPotongan.setText(String.valueOf(potongan));
                    lblUserJumlah.setText(String.valueOf(jumlah));
                }
            } else {
                JOptionPane.showMessageDialog(this, "No Transaction Found", "App Info", 12);
            }

        } catch (SQLException ex) {

            Logger.getLogger(FrmMainPayroll.class
                    .getName()).log(Level.SEVERE, null, ex);
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
        empty = new javax.swing.JPanel();
        transaction = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        createTransaction1 = new javax.swing.JPanel();
        txtEmployeeTransaction = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        createTransaction3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        lblNameTransaction1 = new javax.swing.JLabel();
        lblDepartmentTransaction1 = new javax.swing.JLabel();
        lblPositionTransaction1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        lblGajiKotorTransaction1 = new javax.swing.JLabel();
        lblTunjanganTransaction1 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        btnTransactionConfirm = new javax.swing.JButton();
        btnTransactionBack = new javax.swing.JButton();
        lblTransactionBulan = new javax.swing.JLabel();
        lblTransactionTahun = new javax.swing.JLabel();
        lblTransactionTotalAbsent = new javax.swing.JLabel();
        lblTransactionDeduction = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        lblTransactionBank = new javax.swing.JLabel();
        lblTransactionNoRekening = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        lblTransactionTakeHomePay = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        lblTransactionTipeKaryawan1 = new javax.swing.JLabel();
        createTransaction2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblNameTransaction = new javax.swing.JLabel();
        lblDepartmentTransaction = new javax.swing.JLabel();
        lblPositionTransaction = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtTransactionTotalAbsent = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblGajiKotorTransaction = new javax.swing.JLabel();
        lblTunjanganTransaction = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtTransactionDeduction = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        cboBulan = new javax.swing.JComboBox<>();
        cboTahun = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        btnTransactionNext = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        lblTransactionTipeKaryawan = new javax.swing.JLabel();
        viewTransaction = new javax.swing.JPanel();
        cboViewTransactionMonth = new javax.swing.JComboBox<>();
        jLabel50 = new javax.swing.JLabel();
        cboViewTransactionYear = new javax.swing.JComboBox<>();
        jLabel52 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblTransaction = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
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
        adminLogin = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        txtLoginUsername = new javax.swing.JTextField();
        txtLoginPassword = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        user = new javax.swing.JPanel();
        btnUserPayroll = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        cboUserMonth = new javax.swing.JComboBox<>();
        cboUserYear = new javax.swing.JComboBox<>();
        jLabel76 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        lblUserGaji = new javax.swing.JLabel();
        lblUserTunjangan = new javax.swing.JLabel();
        lblUserJumlah = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        lblUserBulanTahun = new javax.swing.JLabel();
        lblUserTanggal = new javax.swing.JLabel();
        lblUserNama = new javax.swing.JLabel();
        lblUserJabatan = new javax.swing.JLabel();
        lblUserDepartment = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        lblUserPotongan = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblUserNik = new javax.swing.JLabel();

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
        btnVerify.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                btnVerifyKeyTyped(evt);
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

        empty.setOpaque(false);
        empty.setLayout(null);
        jPanel2.add(empty, "card4");

        transaction.setOpaque(false);
        transaction.setLayout(null);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.CardLayout());

        createTransaction1.setOpaque(false);

        jLabel5.setText("Employee ID");

        jButton2.setText("Verify");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout createTransaction1Layout = new javax.swing.GroupLayout(createTransaction1);
        createTransaction1.setLayout(createTransaction1Layout);
        createTransaction1Layout.setHorizontalGroup(
            createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction1Layout.createSequentialGroup()
                .addGroup(createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTransaction1Layout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addComponent(jLabel5)
                        .addGap(60, 60, 60)
                        .addComponent(txtEmployeeTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(createTransaction1Layout.createSequentialGroup()
                        .addGap(406, 406, 406)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(763, Short.MAX_VALUE))
        );
        createTransaction1Layout.setVerticalGroup(
            createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction1Layout.createSequentialGroup()
                .addGap(333, 333, 333)
                .addGroup(createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmployeeTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(155, 155, 155)
                .addComponent(jButton2)
                .addContainerGap(209, Short.MAX_VALUE))
        );

        jPanel4.add(createTransaction1, "card2");

        createTransaction3.setOpaque(false);

        jLabel12.setText("Name:");

        jLabel13.setText("Department:");

        jLabel38.setText("Jabatan");

        lblNameTransaction1.setText("jLabel9");

        lblDepartmentTransaction1.setText("jLabel10");

        lblPositionTransaction1.setText("jLabel11");

        jLabel39.setText("Total Absent");

        jLabel40.setText("Gaji Kotor");

        jLabel41.setText("Tunjangan");

        lblGajiKotorTransaction1.setText("jLabel12");

        lblTunjanganTransaction1.setText("jLabel13");

        jLabel42.setText("Deduction");

        jLabel43.setText("Bulan");

        jLabel44.setText("Tahun");

        btnTransactionConfirm.setText("Confirm");
        btnTransactionConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionConfirmActionPerformed(evt);
            }
        });

        btnTransactionBack.setText("Back");
        btnTransactionBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionBackActionPerformed(evt);
            }
        });

        lblTransactionBulan.setText("jLabel46");

        lblTransactionTahun.setText("jLabel46");

        lblTransactionTotalAbsent.setText("jLabel46");

        lblTransactionDeduction.setText("jLabel46");

        jLabel46.setText("Bank");

        jLabel47.setText("No Rekening");

        lblTransactionBank.setText("jLabel48");

        lblTransactionNoRekening.setText("jLabel48");

        jLabel48.setText("Total Take Home Pay");

        lblTransactionTakeHomePay.setText("jLabel49");

        jLabel49.setText("Tipe Karyawan");

        lblTransactionTipeKaryawan1.setText("jLabel50");

        javax.swing.GroupLayout createTransaction3Layout = new javax.swing.GroupLayout(createTransaction3);
        createTransaction3.setLayout(createTransaction3Layout);
        createTransaction3Layout.setHorizontalGroup(
            createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction3Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel39)
                    .addComponent(jLabel38)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12)
                    .addComponent(jLabel42)
                    .addComponent(jLabel48)
                    .addComponent(jLabel46)
                    .addComponent(jLabel47))
                .addGap(42, 42, 42)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTransaction3Layout.createSequentialGroup()
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(createTransaction3Layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addComponent(btnTransactionBack)
                                .addGap(62, 62, 62)
                                .addComponent(btnTransactionConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblTransactionTakeHomePay)
                            .addComponent(lblTransactionDeduction, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTransactionBank)
                            .addComponent(lblTransactionNoRekening)
                            .addComponent(lblTransactionTotalAbsent))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(createTransaction3Layout.createSequentialGroup()
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblNameTransaction1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDepartmentTransaction1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPositionTransaction1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(244, 244, 244)
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel40)
                            .addComponent(jLabel41)
                            .addComponent(jLabel49))
                        .addGap(53, 53, 53)
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblTunjanganTransaction1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                                .addComponent(lblGajiKotorTransaction1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblTransactionTipeKaryawan1))
                        .addContainerGap(676, Short.MAX_VALUE))))
            .addGroup(createTransaction3Layout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addComponent(jLabel43)
                .addGap(42, 42, 42)
                .addComponent(lblTransactionBulan)
                .addGap(52, 52, 52)
                .addComponent(jLabel44)
                .addGap(46, 46, 46)
                .addComponent(lblTransactionTahun)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        createTransaction3Layout.setVerticalGroup(
            createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction3Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(lblNameTransaction1)
                    .addComponent(jLabel40)
                    .addComponent(lblGajiKotorTransaction1))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(lblDepartmentTransaction1)
                    .addComponent(jLabel41)
                    .addComponent(lblTunjanganTransaction1))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(lblPositionTransaction1)
                    .addComponent(jLabel49)
                    .addComponent(lblTransactionTipeKaryawan1))
                .addGap(56, 56, 56)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(jLabel44)
                    .addComponent(lblTransactionBulan)
                    .addComponent(lblTransactionTahun))
                .addGap(54, 54, 54)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(lblTransactionTotalAbsent))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(lblTransactionDeduction))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(lblTransactionTakeHomePay))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(lblTransactionBank))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(lblTransactionNoRekening))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTransactionConfirm)
                    .addComponent(btnTransactionBack))
                .addGap(232, 232, 232))
        );

        jPanel4.add(createTransaction3, "card4");

        createTransaction2.setOpaque(false);

        jLabel6.setText("Name:");

        jLabel7.setText("Department:");

        jLabel8.setText("Jabatan");

        lblNameTransaction.setText("jLabel9");

        lblDepartmentTransaction.setText("jLabel10");

        lblPositionTransaction.setText("jLabel11");

        jLabel9.setText("Total Absent");

        jLabel10.setText("Gaji Kotor");

        jLabel11.setText("Tunjangan");

        lblGajiKotorTransaction.setText("jLabel12");

        lblTunjanganTransaction.setText("jLabel13");

        jLabel14.setText("Deduction");

        jLabel36.setText("Bulan");

        cboBulan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        cboTahun.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025" }));
        cboTahun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTahunActionPerformed(evt);
            }
        });

        jLabel37.setText("Tahun");

        btnTransactionNext.setText("Next");
        btnTransactionNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionNextActionPerformed(evt);
            }
        });

        jLabel51.setText("Tipe Karyawan");

        lblTransactionTipeKaryawan.setText("jLabel52");

        javax.swing.GroupLayout createTransaction2Layout = new javax.swing.GroupLayout(createTransaction2);
        createTransaction2.setLayout(createTransaction2Layout);
        createTransaction2Layout.setHorizontalGroup(
            createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction2Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36)
                    .addComponent(jLabel14)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(42, 42, 42)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(createTransaction2Layout.createSequentialGroup()
                        .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(createTransaction2Layout.createSequentialGroup()
                                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtTransactionTotalAbsent, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblNameTransaction, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDepartmentTransaction, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblPositionTransaction, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(244, 244, 244)
                                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel51))
                                .addGap(53, 53, 53)
                                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblTunjanganTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                                    .addComponent(lblGajiKotorTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblTransactionTipeKaryawan)))
                            .addComponent(txtTransactionDeduction, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(createTransaction2Layout.createSequentialGroup()
                        .addComponent(cboBulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addComponent(jLabel37)
                        .addGap(30, 30, 30)
                        .addComponent(cboTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(957, 957, 957))))
            .addGroup(createTransaction2Layout.createSequentialGroup()
                .addGap(294, 294, 294)
                .addComponent(btnTransactionNext, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        createTransaction2Layout.setVerticalGroup(
            createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction2Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblNameTransaction)
                    .addComponent(jLabel10)
                    .addComponent(lblGajiKotorTransaction))
                .addGap(18, 18, 18)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblDepartmentTransaction)
                    .addComponent(jLabel11)
                    .addComponent(lblTunjanganTransaction))
                .addGap(18, 18, 18)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblPositionTransaction)
                    .addComponent(jLabel51)
                    .addComponent(lblTransactionTipeKaryawan))
                .addGap(89, 89, 89)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(cboBulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(18, 18, 18)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTransactionTotalAbsent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtTransactionDeduction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(93, 93, 93)
                .addComponent(btnTransactionNext)
                .addContainerGap(288, Short.MAX_VALUE))
        );

        jPanel4.add(createTransaction2, "card4");

        viewTransaction.setOpaque(false);

        cboViewTransactionMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        jLabel50.setText("Bulan");

        cboViewTransactionYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025" }));

        jLabel52.setText("Tahun");

        tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID transaction", "Nama", "Departmen", "Jabatan", "Absen", "Gaji Kotor", "Tunjangan", "Potogan", "THP", "Bank", "No Rekening"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(tblTransaction);

        jButton3.setText("Check Transaction");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout viewTransactionLayout = new javax.swing.GroupLayout(viewTransaction);
        viewTransaction.setLayout(viewTransactionLayout);
        viewTransactionLayout.setHorizontalGroup(
            viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewTransactionLayout.createSequentialGroup()
                .addGroup(viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(viewTransactionLayout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboViewTransactionMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(166, 166, 166)
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboViewTransactionYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(101, 101, 101)
                        .addComponent(jButton3))
                    .addGroup(viewTransactionLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(140, Short.MAX_VALUE))
        );
        viewTransactionLayout.setVerticalGroup(
            viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewTransactionLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboViewTransactionMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50)
                    .addComponent(cboViewTransactionYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52)
                    .addComponent(jButton3))
                .addGap(81, 81, 81)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(166, Short.MAX_VALUE))
        );

        jPanel4.add(viewTransaction, "card3");

        transaction.add(jPanel4);
        jPanel4.setBounds(0, 150, 1350, 750);

        jButton9.setContentAreaFilled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        transaction.add(jButton9);
        jButton9.setBounds(130, 20, 70, 90);

        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        transaction.add(jButton1);
        jButton1.setBounds(440, 20, 90, 90);

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

        adminLogin.setLayout(null);

        jButton8.setContentAreaFilled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        adminLogin.add(jButton8);
        jButton8.setBounds(30, 30, 100, 100);

        btnLogin.setContentAreaFilled(false);
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        adminLogin.add(btnLogin);
        btnLogin.setBounds(1050, 580, 290, 70);

        txtLoginUsername.setFont(new java.awt.Font("Calibri", 1, 48)); // NOI18N
        txtLoginUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLoginUsername.setBorder(null);
        txtLoginUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginUsernameActionPerformed(evt);
            }
        });
        adminLogin.add(txtLoginUsername);
        txtLoginUsername.setBounds(1000, 270, 390, 60);

        txtLoginPassword.setFont(new java.awt.Font("Calibri", 1, 48)); // NOI18N
        txtLoginPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLoginPassword.setBorder(null);
        txtLoginPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginPasswordActionPerformed(evt);
            }
        });
        adminLogin.add(txtLoginPassword);
        txtLoginPassword.setBounds(1000, 455, 390, 60);

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll4.jpg"))); // NOI18N
        adminLogin.add(jLabel45);
        jLabel45.setBounds(0, 0, 1600, 900);

        jPanel1.add(adminLogin, "card5");

        user.setOpaque(false);

        btnUserPayroll.setText("Check Payroll");
        btnUserPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserPayrollbtnPayrollActionPerformed(evt);
            }
        });

        jButton12.setText("back");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12jButton10ActionPerformed(evt);
            }
        });

        cboUserMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        cboUserYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025" }));

        jLabel76.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel76.setText("Month :");

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel77.setText("Tanggal tercetak :");

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel78.setText("Nama :");

        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel79.setText("Jabatan :");

        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel80.setText("Department :");

        jLabel81.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel81.setText("Gaji Pokok :");

        jLabel82.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel82.setText("Jumlah :");

        jLabel83.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel83.setText("Tunjangan :");

        lblUserGaji.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblUserGaji.setText("ads");

        lblUserTunjangan.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblUserTunjangan.setText("ads");

        lblUserJumlah.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblUserJumlah.setText("ads");

        jLabel84.setForeground(new java.awt.Color(204, 204, 204));
        jLabel84.setText("Legalize by HRD Department ");

        jLabel85.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel85.setText("Bulan-Tahun :");

        lblUserBulanTahun.setText("jLabel69");

        lblUserTanggal.setText("jLabel70");

        lblUserNama.setText("jLabel70");

        lblUserJabatan.setText("jLabel69");

        lblUserDepartment.setText("jLabel69");

        jLabel86.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel86.setText("Potongan :");

        lblUserPotongan.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblUserPotongan.setText("ads");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel84))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel78)
                            .addComponent(jLabel79)
                            .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUserTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUserBulanTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblUserJabatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblUserDepartment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblUserNama, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(415, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel86, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel83, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(34, 34, 34)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUserJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserTunjangan, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserGaji, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserPotongan, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(329, 329, 329))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserTanggal)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserBulanTahun)
                    .addComponent(jLabel85))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserNama)
                    .addComponent(jLabel78))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserJabatan)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserDepartment)
                    .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(67, 67, 67)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel81)
                    .addComponent(lblUserGaji))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83)
                    .addComponent(lblUserTunjangan))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(lblUserPotongan))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(lblUserJumlah))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jLabel84)
                .addGap(33, 33, 33))
        );

        lblUserName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblUserName.setText("jLabel1");

        lblUserNik.setText("jLabel53");

        javax.swing.GroupLayout userLayout = new javax.swing.GroupLayout(user);
        user.setLayout(userLayout);
        userLayout.setHorizontalGroup(
            userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userLayout.createSequentialGroup()
                .addGroup(userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(userLayout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jButton12))
                    .addGroup(userLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(userLayout.createSequentialGroup()
                                .addComponent(jLabel76)
                                .addGap(28, 28, 28)
                                .addGroup(userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(userLayout.createSequentialGroup()
                                        .addComponent(cboUserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboUserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblUserNik)
                                            .addComponent(btnUserPayroll))))))))
                .addContainerGap(744, Short.MAX_VALUE))
        );
        userLayout.setVerticalGroup(
            userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserNik))
                .addGap(36, 36, 36)
                .addGroup(userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUserPayroll)
                    .addComponent(cboUserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboUserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(64, 64, 64)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
                .addComponent(jButton12)
                .addGap(48, 48, 48))
        );

        jPanel1.add(user, "card2");

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 1600, 900);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed
        verifyEmployee();
        if (foundEmployee == true) {
            changeMainLayout(user);
        } else if (txtID.getText().equals("exit")) {
            System.exit(0);
        } else if (txtID.getText().equals("admin")) {
            changeMainLayout(adminLogin);
        } else {
            JOptionPane.showMessageDialog(this, "No Such User", "App Info", 2);
        }
    }//GEN-LAST:event_btnVerifyActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        changeMainLayout(adminLogin);
        txtEmployeeTransaction.setText("");
        txtID.setText("");
        foundEmployee = false;
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
        changeTransactionImage(1);
        tableDepartmentInitData();
    }//GEN-LAST:event_btnDepartmentActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        changeMainSubLayout(employee);
        changeTransactionImage(1);
        tableEmployeeInitData();
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBankActionPerformed
        changeMainSubLayout(bank);
        changeTransactionImage(1);
        tableBankInitData();
    }//GEN-LAST:event_btnBankActionPerformed

    private void btnTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionActionPerformed
        changeMainSubLayout(transaction);
        changeTransactionImage(2);
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

    private void cboTahunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTahunActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTahunActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        verifyTransaction();
        if (foundEmployee == true) {
            changeTransactionLayout(createTransaction2);
        } else {
            JOptionPane.showMessageDialog(this, "No Such User", "App Info", 2);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtLoginPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginPasswordActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        if (!txtLoginUsername.getText().trim().equals("admin")) {
            JOptionPane.showMessageDialog(this, "Username don't match", "App Info", 2);
        } else if (!txtLoginPassword.getText().trim().equals("admin")) {
            JOptionPane.showMessageDialog(this, "Password don't match", "App Info", 2);
        } else {
            txtLoginUsername.setText("");
            txtLoginPassword.setText("");
            changeMainLayout(admin);
        }
        changeTransactionImage(3);
        changeMainSubLayout(empty);

    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtLoginUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginUsernameActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        txtLoginUsername.setText("");
        txtLoginPassword.setText("");
        txtID.setText("");
        changeMainLayout(main);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void btnTransactionNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionNextActionPerformed

        if (!txtTransactionDeduction.getText().trim().isEmpty() && !txtTransactionTotalAbsent.getText().trim().isEmpty()) {
            changeTransactionLayout(createTransaction3);
            lblTransactionBulan.setText(cboBulan.getSelectedItem().toString());
            lblTransactionTahun.setText(cboTahun.getSelectedItem().toString());
            lblTransactionTotalAbsent.setText(txtTransactionTotalAbsent.getText());
            lblTransactionDeduction.setText(txtTransactionDeduction.getText());
            if (lblTransactionTipeKaryawan.getText().equalsIgnoreCase("harian")) {
                double pay = (30 - Double.valueOf(lblTransactionTotalAbsent.getText())) / 30;
                double gKotor = Double.valueOf(lblGajiKotorTransaction.getText());
                double tunjang = Double.valueOf(lblTunjanganTransaction.getText());
                double deduct = Double.valueOf(lblTransactionDeduction.getText());
                lblTransactionTakeHomePay.setText(String.format("%.2f", (double) ((double) pay * gKotor) + tunjang - deduct));
            } else if (lblTransactionTipeKaryawan.getText().equalsIgnoreCase("bulanan")) {
                double pay = 1;
                double gKotor = Double.valueOf(lblGajiKotorTransaction.getText());
                double tunjang = Double.valueOf(lblTunjanganTransaction.getText());
                double deduct = Double.valueOf(lblTransactionDeduction.getText());
                lblTransactionTakeHomePay.setText(String.format("%.2f", (double) ((double) pay * gKotor) + tunjang - deduct));
            }
        }

    }//GEN-LAST:event_btnTransactionNextActionPerformed

    private void btnTransactionBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionBackActionPerformed
        changeTransactionLayout(createTransaction2);
    }//GEN-LAST:event_btnTransactionBackActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        changeTransactionLayout(createTransaction1);
        txtEmployeeTransaction.setText("");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void btnTransactionConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionConfirmActionPerformed
        int x = SUtility.msq(this, "Are you sure?");
        if (x == 0) {
            try {

                myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
                // Prepare statement
                int idNumber = 1;
                myStmt = myConn
                        .prepareStatement("select * from transaksi");
                myRs = myStmt.executeQuery();

                // Process result set
                if (myRs.isBeforeFirst()) {
                    while (myRs.next()) {
                        idNumber++;
                    }
                } else {
                    idNumber = 1;
                }

                // Prepare statement
                myStmt = myConn
                        .prepareStatement("insert into transaksi values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

                myStmt.setInt(1, idNumber);
                myStmt.setString(2, lblTransactionTahun.getText());
                myStmt.setString(3, lblTransactionBulan.getText());
                myStmt.setString(4, lblNameTransaction.getText());
                myStmt.setString(5, lblDepartmentTransaction.getText());
                myStmt.setString(6, lblPositionTransaction.getText());
                myStmt.setInt(7, Integer.valueOf(lblTransactionTotalAbsent.getText()));
                myStmt.setDouble(8, Double.valueOf(lblGajiKotorTransaction.getText()));
                myStmt.setDouble(9, Double.valueOf(lblTunjanganTransaction.getText()));
                myStmt.setDouble(10, Double.valueOf(lblTransactionDeduction.getText()));
                myStmt.setDouble(11, Double.valueOf(lblTransactionTakeHomePay.getText()));
                myStmt.setString(12, lblTransactionBank.getText());
                myStmt.setString(13, lblTransactionNoRekening.getText());

                // Execute SQL query
                myStmt.executeUpdate();

            } catch (SQLException ex) {

                Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
            }
            changeTransactionLayout(createTransaction1);

        }
    }//GEN-LAST:event_btnTransactionConfirmActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        changeTransactionLayout(viewTransaction);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        tableTransactionInitData(cboViewTransactionMonth.getSelectedItem().toString(), cboViewTransactionYear.getSelectedItem().toString());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnUserPayrollbtnPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserPayrollbtnPayrollActionPerformed
        // TODO add your handling code here:
        checkpayroll();
    }//GEN-LAST:event_btnUserPayrollbtnPayrollActionPerformed

    private void jButton12jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12jButton10ActionPerformed
        changeMainLayout(main);
    }//GEN-LAST:event_jButton12jButton10ActionPerformed

    private void btnVerifyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnVerifyKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_btnVerifyKeyTyped

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

    private void changeTransactionLayout(Component comp) {
        jPanel4.removeAll();
        jPanel4.repaint();
        jPanel4.revalidate();

        jPanel4.add(comp);
        jPanel4.repaint();
        jPanel4.revalidate();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel admin;
    private javax.swing.JPanel adminLogin;
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
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnNewBank;
    private javax.swing.JButton btnSaveToDatabaseBank;
    private javax.swing.JButton btnTransaction;
    private javax.swing.JButton btnTransactionBack;
    private javax.swing.JButton btnTransactionConfirm;
    private javax.swing.JButton btnTransactionNext;
    private javax.swing.JButton btnUpdateBank1;
    private javax.swing.JButton btnUserPayroll;
    private javax.swing.JButton btnVerify;
    private javax.swing.JComboBox<String> cboBulan;
    private javax.swing.JComboBox<String> cboEmployeeAgama;
    private javax.swing.JComboBox<String> cboEmployeeIdBank;
    private javax.swing.JComboBox<String> cboEmployeeIdDepartment;
    private javax.swing.JComboBox<String> cboEmployeeJenisKelamin;
    private javax.swing.JComboBox<String> cboEmployeeStatusPajak;
    private javax.swing.JComboBox<String> cboEmployeeStatusPerkawinan;
    private javax.swing.JComboBox<String> cboEmployeeTipeKaryawan;
    private javax.swing.JComboBox<String> cboTahun;
    private javax.swing.JComboBox<String> cboUserMonth;
    private javax.swing.JComboBox<String> cboUserYear;
    private javax.swing.JComboBox<String> cboViewTransactionMonth;
    private javax.swing.JComboBox<String> cboViewTransactionYear;
    private javax.swing.JPanel createTransaction1;
    private javax.swing.JPanel createTransaction2;
    private javax.swing.JPanel createTransaction3;
    private javax.swing.JPanel department;
    private javax.swing.JPanel employee;
    private javax.swing.JPanel empty;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblDepartmentID;
    private javax.swing.JLabel lblDepartmentName;
    private javax.swing.JLabel lblDepartmentTransaction;
    private javax.swing.JLabel lblDepartmentTransaction1;
    private javax.swing.JLabel lblGajiKotorTransaction;
    private javax.swing.JLabel lblGajiKotorTransaction1;
    private javax.swing.JLabel lblNameTransaction;
    private javax.swing.JLabel lblNameTransaction1;
    private javax.swing.JLabel lblPositionTransaction;
    private javax.swing.JLabel lblPositionTransaction1;
    private javax.swing.JLabel lblTransactionBank;
    private javax.swing.JLabel lblTransactionBulan;
    private javax.swing.JLabel lblTransactionDeduction;
    private javax.swing.JLabel lblTransactionNoRekening;
    private javax.swing.JLabel lblTransactionTahun;
    private javax.swing.JLabel lblTransactionTakeHomePay;
    private javax.swing.JLabel lblTransactionTipeKaryawan;
    private javax.swing.JLabel lblTransactionTipeKaryawan1;
    private javax.swing.JLabel lblTransactionTotalAbsent;
    private javax.swing.JLabel lblTunjanganTransaction;
    private javax.swing.JLabel lblTunjanganTransaction1;
    private javax.swing.JLabel lblUserBulanTahun;
    private javax.swing.JLabel lblUserDepartment;
    private javax.swing.JLabel lblUserGaji;
    private javax.swing.JLabel lblUserJabatan;
    private javax.swing.JLabel lblUserJumlah;
    private javax.swing.JLabel lblUserNama;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JLabel lblUserNik;
    private javax.swing.JLabel lblUserPotongan;
    private javax.swing.JLabel lblUserTanggal;
    private javax.swing.JLabel lblUserTunjangan;
    private javax.swing.JPanel main;
    private javax.swing.JTable tblBank;
    private javax.swing.JTable tblDepartment;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JTable tblTransaction;
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
    private javax.swing.JTextField txtEmployeeTransaction;
    private javax.swing.JTextField txtEmployeeTunjangan;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtIdBank;
    private javax.swing.JTextField txtLoginPassword;
    private javax.swing.JTextField txtLoginUsername;
    private javax.swing.JTextField txtNamaBank;
    private javax.swing.JTextField txtTransactionDeduction;
    private javax.swing.JTextField txtTransactionTotalAbsent;
    private javax.swing.JPanel user;
    private javax.swing.JPanel viewTransaction;
    // End of variables declaration//GEN-END:variables
}
