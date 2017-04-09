/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payrollfinalproject;

import java.awt.Component;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
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
        setSize(880, 510);
        connectDatabase();
        banklistener();
        employeeListener();
        departmentListener();
        setLocationRelativeTo(null);
    }

    private void connectDatabase() {
        try {
            myConn = DriverManager.getConnection(DbConn.JDBC_URL, DbConn.JDBC_USERNAME, DbConn.JDBC_PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void toPDF(int id_transaksi) {
        try {
            JasperReport jasperReport = null;
            InputStream path = this.getClass().getResourceAsStream("report3.jrxml");
            JasperPrint jasperPrint = null;
            jasperReport = JasperCompileManager.compileReport(path);
            Map parameters = new HashMap();
            parameters.put("id_transaksi", id_transaksi);
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, myConn);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tableBankInitData() {
        DefaultTableModel tableModel = (DefaultTableModel) tblBank.getModel();
        tableModel.setRowCount(0);

        try {
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
            // Prepare statement
            myStmt = myConn.prepareStatement("select * from transaksi where karyawan=? and periode_pengajian =? and periode_payroll=?");
            myStmt.setString(1, lblUserName.getText());
            myStmt.setString(2, String.valueOf(cboUserMonth.getSelectedItem()));
            myStmt.setString(3, String.valueOf(cboUserYear.getSelectedItem()));

            // Execute SQL query
            myRs = myStmt.executeQuery();

            // Process result set
            if (myRs.isBeforeFirst()) {
                pnlPayroll.setVisible(true);
                while (myRs.next()) {
                    Integer id = myRs.getInt("id_transaksi");
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
                    lblTransactionId.setText(String.valueOf(id));
                    btnPdf.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No Transaction Found", "App Info", 1);
                btnPdf.setVisible(false);
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
        jButton4 = new javax.swing.JButton();
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
        btnBrowse = new javax.swing.JButton();
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
        txtLoginPassword = new javax.swing.JPasswordField();
        txtLoginUsername = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        user = new javax.swing.JPanel();
        btnUserPayroll = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        cboUserMonth = new javax.swing.JComboBox<>();
        cboUserYear = new javax.swing.JComboBox<>();
        jLabel76 = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblUserNik = new javax.swing.JLabel();
        pnlPayroll = new javax.swing.JPanel();
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
        jLabel85 = new javax.swing.JLabel();
        lblUserBulanTahun = new javax.swing.JLabel();
        lblUserTanggal = new javax.swing.JLabel();
        lblUserNama = new javax.swing.JLabel();
        lblUserJabatan = new javax.swing.JLabel();
        lblUserDepartment = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        lblUserPotongan = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        lblTransactionId = new javax.swing.JLabel();
        btnPdf = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Payroll");
        setPreferredSize(new java.awt.Dimension(1600, 900));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.CardLayout());

        main.setOpaque(false);
        main.setLayout(null);

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 70)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("!");
        jButton4.setContentAreaFilled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        main.add(jButton4);
        jButton4.setBounds(0, 0, 90, 80);

        txtID.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        txtID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtID.setBorder(null);
        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });
        main.add(txtID);
        txtID.setBounds(547, 220, 210, 30);

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
        btnVerify.setBounds(580, 300, 160, 40);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll3.jpg"))); // NOI18N
        main.add(jLabel15);
        jLabel15.setBounds(0, 0, 880, 490);

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
        btnEmployee.setBounds(60, 220, 60, 60);

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

        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout createTransaction1Layout = new javax.swing.GroupLayout(createTransaction1);
        createTransaction1.setLayout(createTransaction1Layout);
        createTransaction1Layout.setHorizontalGroup(
            createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction1Layout.createSequentialGroup()
                .addGroup(createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTransaction1Layout.createSequentialGroup()
                        .addGap(137, 137, 137)
                        .addComponent(jLabel5)
                        .addGap(60, 60, 60)
                        .addComponent(txtEmployeeTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(btnBrowse))
                    .addGroup(createTransaction1Layout.createSequentialGroup()
                        .addGap(326, 326, 326)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(529, Short.MAX_VALUE))
        );
        createTransaction1Layout.setVerticalGroup(
            createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction1Layout.createSequentialGroup()
                .addGap(236, 236, 236)
                .addGroup(createTransaction1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmployeeTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse))
                .addGap(36, 36, 36)
                .addComponent(jButton2)
                .addContainerGap(265, Short.MAX_VALUE))
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
                .addGap(105, 105, 105)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTransaction3Layout.createSequentialGroup()
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel38)
                                .addComponent(jLabel13)
                                .addComponent(jLabel12)
                                .addComponent(jLabel42)
                                .addComponent(jLabel48)
                                .addComponent(jLabel46)
                                .addComponent(jLabel47)
                                .addComponent(jLabel39))
                            .addGroup(createTransaction3Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel43)
                                .addGap(18, 18, 18)
                                .addComponent(lblTransactionBulan)))
                        .addGap(33, 33, 33)
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTransactionTakeHomePay)
                            .addComponent(lblTransactionDeduction, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTransactionBank)
                            .addComponent(lblTransactionNoRekening)
                            .addGroup(createTransaction3Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addGap(18, 18, 18)
                                .addComponent(lblTransactionTahun))
                            .addGroup(createTransaction3Layout.createSequentialGroup()
                                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblNameTransaction1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDepartmentTransaction1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblPositionTransaction1, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(94, 94, 94)
                                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel40)
                                    .addComponent(jLabel41)
                                    .addComponent(jLabel49))
                                .addGap(33, 33, 33)
                                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblGajiKotorTransaction1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTunjanganTransaction1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTransactionTipeKaryawan1)))
                            .addComponent(lblTransactionTotalAbsent)))
                    .addGroup(createTransaction3Layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(btnTransactionBack)
                        .addGap(96, 96, 96)
                        .addComponent(btnTransactionConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(584, Short.MAX_VALUE))
        );
        createTransaction3Layout.setVerticalGroup(
            createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction3Layout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(lblNameTransaction1)
                    .addComponent(jLabel40)
                    .addComponent(lblGajiKotorTransaction1))
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTransaction3Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel43)
                            .addComponent(lblTransactionBulan)
                            .addComponent(jLabel44)
                            .addComponent(lblTransactionTahun)))
                    .addGroup(createTransaction3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDepartmentTransaction1)
                            .addComponent(jLabel41)
                            .addComponent(lblTunjanganTransaction1))
                        .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(createTransaction3Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel38)
                                    .addComponent(lblPositionTransaction1)
                                    .addComponent(jLabel49)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createTransaction3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTransactionTipeKaryawan1)
                                .addGap(1, 1, 1)))))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(lblTransactionTotalAbsent))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(lblTransactionDeduction))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(lblTransactionTakeHomePay))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(lblTransactionBank))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(lblTransactionNoRekening))
                .addGap(18, 18, 18)
                .addGroup(createTransaction3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTransactionConfirm)
                    .addComponent(btnTransactionBack))
                .addContainerGap(211, Short.MAX_VALUE))
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
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTransactionDeduction, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(createTransaction2Layout.createSequentialGroup()
                                .addComponent(cboBulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel37)
                                .addGap(18, 18, 18)
                                .addComponent(cboTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(createTransaction2Layout.createSequentialGroup()
                                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtTransactionTotalAbsent, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblNameTransaction, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDepartmentTransaction, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblPositionTransaction, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(93, 93, 93)
                                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel51))
                                .addGap(53, 53, 53)
                                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblTunjanganTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblGajiKotorTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTransactionTipeKaryawan)))))
                    .addGroup(createTransaction2Layout.createSequentialGroup()
                        .addGap(269, 269, 269)
                        .addComponent(btnTransactionNext, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(637, Short.MAX_VALUE))
        );
        createTransaction2Layout.setVerticalGroup(
            createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTransaction2Layout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(lblGajiKotorTransaction)
                    .addComponent(jLabel6)
                    .addComponent(lblNameTransaction))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTunjanganTransaction)
                    .addComponent(jLabel11)
                    .addComponent(jLabel7)
                    .addComponent(lblDepartmentTransaction))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblPositionTransaction)
                    .addComponent(jLabel51)
                    .addComponent(lblTransactionTipeKaryawan))
                .addGap(18, 18, 18)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(cboBulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(cboTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTransactionTotalAbsent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTransaction2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtTransactionDeduction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addComponent(btnTransactionNext)
                .addContainerGap(224, Short.MAX_VALUE))
        );

        jPanel4.add(createTransaction2, "card4");

        viewTransaction.setOpaque(false);

        cboViewTransactionMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        jLabel50.setText("Bulan");

        cboViewTransactionYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025" }));
        cboViewTransactionYear.setSelectedIndex(6);

        jLabel52.setText("Tahun");

        tblTransaction.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
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
                        .addGap(85, 85, 85)
                        .addComponent(jLabel50)
                        .addGap(18, 18, 18)
                        .addComponent(cboViewTransactionMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)
                        .addComponent(jLabel52)
                        .addGap(18, 18, 18)
                        .addComponent(cboViewTransactionYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(107, 107, 107)
                        .addComponent(jButton3))
                    .addGroup(viewTransactionLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(445, Short.MAX_VALUE))
        );
        viewTransactionLayout.setVerticalGroup(
            viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewTransactionLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addGroup(viewTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboViewTransactionMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50)
                    .addComponent(jLabel52)
                    .addComponent(cboViewTransactionYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(173, Short.MAX_VALUE))
        );

        jPanel4.add(viewTransaction, "card3");

        transaction.add(jPanel4);
        jPanel4.setBounds(0, 0, 1120, 590);

        jButton9.setContentAreaFilled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        transaction.add(jButton9);
        jButton9.setBounds(78, 20, 39, 45);

        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        transaction.add(jButton1);
        jButton1.setBounds(237, 20, 62, 40);

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
                        .addGap(120, 120, 120)
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
                            .addComponent(txtAlamatBank)
                            .addComponent(txtCabangBank, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(bankLayout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(94, Short.MAX_VALUE))
            .addGroup(bankLayout.createSequentialGroup()
                .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bankLayout.createSequentialGroup()
                        .addGap(252, 252, 252)
                        .addComponent(btnUpdateBank1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(123, 123, 123)
                        .addComponent(btnDeleteBank, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bankLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(btnNewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSaveToDatabaseBank, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        bankLayout.setVerticalGroup(
            bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bankLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnUpdateBank1, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                        .addComponent(btnDeleteBank, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSaveToDatabaseBank, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnNewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jPanel2.add(bank, "card5");

        employee.setOpaque(false);

        btnEmployeeNew.setContentAreaFilled(false);
        btnEmployeeNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeNewActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
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

        tblEmployee.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        tblEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "NIK"
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

        txtEmployeeID.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        txtEmployeeNik.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel3.setText("NIK");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel4.setText("Jenis kelamin");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel16.setText("Nama");

        txtEmployeeName.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        txtEmployeeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmployeeNameActionPerformed(evt);
            }
        });

        cboEmployeeJenisKelamin.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        cboEmployeeJenisKelamin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PRIA", "WANITA" }));

        txtEmployeeTempatLahir.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel17.setText("Tempat Lahir");

        txtEmployeeAlamat.setColumns(5);
        txtEmployeeAlamat.setFont(new java.awt.Font("Monospaced", 0, 9)); // NOI18N
        txtEmployeeAlamat.setRows(3);
        jScrollPane1.setViewportView(txtEmployeeAlamat);

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel22.setText("Alamat");

        txtEmployeeNoHP.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel23.setText("No HP");

        txtEmployeeEmail.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel24.setText("Email");

        cboEmployeeAgama.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        cboEmployeeAgama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Buddha", "Kristen", "Islam", "Hindu" }));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel25.setText("Agama");

        cboEmployeeStatusPerkawinan.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        cboEmployeeStatusPerkawinan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lajang", "Menikah" }));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel26.setText("Status Perkawinan");

        cboEmployeeStatusPajak.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        cboEmployeeStatusPajak.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TK0", "TK1", "TK2", "TK3", "K0", "K1", "K2", "K3" }));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel27.setText("Status Pajak");

        cboEmployeeIdDepartment.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        cboEmployeeIdDepartment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel28.setText("Department");

        cboEmployeeTipeKaryawan.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        cboEmployeeTipeKaryawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Harian", "Bulanan" }));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel29.setText("Tipe Karyawan");

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel30.setText("Tanggal Masuk");

        txtEmployeeGajiKotor.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel31.setText("Gaji Kotor");

        txtEmployeeTunjangan.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel32.setText("Tunjangan");

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel33.setText("Bank");

        cboEmployeeIdBank.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        cboEmployeeIdBank.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtEmployeeNoRekening.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel34.setText("No Rekening");

        txtEmployeeTangalLahir.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel35.setText("Tanggal Lahir");

        txtEmployeeTanggalMasuk.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

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
                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(employeeLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(btnEmployeeNew, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(132, 132, 132)
                        .addComponent(btnEmployeeUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(128, 128, 128)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(employeeLayout.createSequentialGroup()
                                .addComponent(btnEmployeeDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(137, 137, 137)
                                .addComponent(btnEmployeeSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(employeeLayout.createSequentialGroup()
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel26)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel30)
                                    .addComponent(jLabel31)
                                    .addComponent(jLabel32)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cboEmployeeStatusPerkawinan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboEmployeeStatusPajak, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboEmployeeIdDepartment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboEmployeeTipeKaryawan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtEmployeeTanggalMasuk)
                                    .addComponent(txtEmployeeGajiKotor)
                                    .addComponent(txtEmployeeTunjangan)
                                    .addComponent(cboEmployeeIdBank, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtEmployeeNoRekening, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(employeeLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel16)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24)
                            .addComponent(jLabel25))
                        .addGap(18, 18, 18)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmployeeNik)
                            .addComponent(cboEmployeeJenisKelamin, 0, 84, Short.MAX_VALUE)
                            .addComponent(txtEmployeeTempatLahir)
                            .addComponent(txtEmployeeTangalLahir)
                            .addComponent(txtEmployeeName)
                            .addComponent(txtEmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1)
                            .addComponent(txtEmployeeNoHP)
                            .addComponent(txtEmployeeEmail)
                            .addComponent(cboEmployeeAgama, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        employeeLayout.setVerticalGroup(
            employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(employeeLayout.createSequentialGroup()
                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, employeeLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnEmployeeSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(employeeLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnEmployeeNew, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnEmployeeUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, employeeLayout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(btnEmployeeDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(employeeLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel2)
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(employeeLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtEmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26)
                                .addComponent(cboEmployeeStatusPerkawinan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(2, 2, 2)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmployeeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel27)
                            .addComponent(cboEmployeeStatusPajak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmployeeNik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel28)
                            .addComponent(cboEmployeeIdDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboEmployeeJenisKelamin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel29)
                            .addComponent(cboEmployeeTipeKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtEmployeeTempatLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel30)
                                .addComponent(txtEmployeeTanggalMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(txtEmployeeTangalLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31)
                            .addComponent(txtEmployeeGajiKotor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(employeeLayout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel33)
                                    .addComponent(cboEmployeeIdBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txtEmployeeTunjangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(txtEmployeeNoHP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34)
                            .addComponent(txtEmployeeNoRekening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(txtEmployeeEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(employeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(cboEmployeeAgama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(109, Short.MAX_VALUE))
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
                        .addGap(75, 75, 75)
                        .addComponent(btnDepartmentAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(127, 127, 127)
                        .addComponent(btnDepartmentUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(126, 126, 126)
                        .addComponent(btnDepartmentDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(139, 139, 139)
                        .addComponent(btnDepartmentSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(departmentLayout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(departmentLayout.createSequentialGroup()
                                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDepartmentName)
                                    .addComponent(lblDepartmentID))
                                .addGap(51, 51, 51)
                                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDepartmentName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDepartmentID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        departmentLayout.setVerticalGroup(
            departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(departmentLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnDepartmentDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                        .addComponent(btnDepartmentUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDepartmentAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnDepartmentSaveToDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepartmentID)
                    .addComponent(txtDepartmentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(departmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepartmentName)
                    .addComponent(txtDepartmentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        jPanel2.add(department, "card6");

        admin.add(jPanel2);
        jPanel2.setBounds(170, 0, 710, 500);

        jButton7.setContentAreaFilled(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        admin.add(jButton7);
        jButton7.setBounds(70, 20, 50, 50);

        btnDepartment.setContentAreaFilled(false);
        btnDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepartmentActionPerformed(evt);
            }
        });
        admin.add(btnDepartment);
        btnDepartment.setBounds(60, 300, 60, 60);

        btnBank.setContentAreaFilled(false);
        btnBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBankActionPerformed(evt);
            }
        });
        admin.add(btnBank);
        btnBank.setBounds(60, 380, 60, 60);

        btnTransaction.setContentAreaFilled(false);
        btnTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionActionPerformed(evt);
            }
        });
        admin.add(btnTransaction);
        btnTransaction.setBounds(60, 140, 70, 70);

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll1.jpg"))); // NOI18N
        admin.add(lblBackground);
        lblBackground.setBounds(0, 0, 880, 500);

        jPanel1.add(admin, "card3");

        adminLogin.setLayout(null);

        jButton8.setContentAreaFilled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        adminLogin.add(jButton8);
        jButton8.setBounds(20, 20, 50, 50);

        btnLogin.setContentAreaFilled(false);
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        btnLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLoginKeyReleased(evt);
            }
        });
        adminLogin.add(btnLogin);
        btnLogin.setBounds(580, 330, 160, 30);

        txtLoginPassword.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        txtLoginPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLoginPassword.setBorder(null);
        txtLoginPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginPasswordActionPerformed(evt);
            }
        });
        adminLogin.add(txtLoginPassword);
        txtLoginPassword.setBounds(550, 250, 210, 30);

        txtLoginUsername.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        txtLoginUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLoginUsername.setBorder(null);
        txtLoginUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginUsernameActionPerformed(evt);
            }
        });
        adminLogin.add(txtLoginUsername);
        txtLoginUsername.setBounds(550, 150, 210, 30);

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll4.jpg"))); // NOI18N
        adminLogin.add(jLabel45);
        jLabel45.setBounds(0, 0, 880, 500);

        jPanel1.add(adminLogin, "card5");

        user.setOpaque(false);
        user.setLayout(null);

        btnUserPayroll.setText("Check Payroll");
        btnUserPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserPayrollbtnPayrollActionPerformed(evt);
            }
        });
        user.add(btnUserPayroll);
        btnUserPayroll.setBounds(500, 120, 160, 23);

        jButton12.setContentAreaFilled(false);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12jButton10ActionPerformed(evt);
            }
        });
        user.add(jButton12);
        jButton12.setBounds(20, 20, 50, 50);

        cboUserMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        user.add(cboUserMonth);
        cboUserMonth.setBounds(330, 120, 50, 20);

        cboUserYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025" }));
        user.add(cboUserYear);
        cboUserYear.setBounds(400, 120, 70, 20);

        jLabel76.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel76.setText("Month :");
        user.add(jLabel76);
        jLabel76.setBounds(240, 120, 66, 26);

        lblUserName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblUserName.setText("jLabel1");
        user.add(lblUserName);
        lblUserName.setBounds(170, 90, 300, 20);

        lblUserNik.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblUserNik.setText("jLabel53");
        user.add(lblUserNik);
        lblUserNik.setBounds(170, 110, 130, 10);

        pnlPayroll.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlPayroll.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlPayroll.setOpaque(false);

        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel77.setText("Tanggal tercetak :");

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel78.setText("Nama :");

        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel79.setText("Jabatan :");

        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel80.setText("Department :");

        jLabel81.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel81.setText("Gaji Pokok :");

        jLabel82.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel82.setText("Jumlah THP:");

        jLabel83.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel83.setText("Tunjangan :");

        lblUserGaji.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUserGaji.setText("ads");

        lblUserTunjangan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUserTunjangan.setText("ads");

        lblUserJumlah.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUserJumlah.setText("ads");

        jLabel85.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel85.setText("Bulan-Tahun :");

        lblUserBulanTahun.setText("jLabel69");

        lblUserTanggal.setText("jLabel70");

        lblUserNama.setText("jLabel70");

        lblUserJabatan.setText("jLabel69");

        lblUserDepartment.setText("jLabel69");

        jLabel86.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel86.setText("Potongan :");

        lblUserPotongan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblUserPotongan.setText("ads");

        jLabel54.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel54.setText("Rp.");

        jLabel55.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel55.setText("Rp.");

        jLabel56.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel56.setText("Rp.");

        jLabel57.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel57.setText("Rp.");

        jLabel58.setForeground(new java.awt.Color(204, 204, 204));
        jLabel58.setText("Legalize by HRD Department ");

        javax.swing.GroupLayout pnlPayrollLayout = new javax.swing.GroupLayout(pnlPayroll);
        pnlPayroll.setLayout(pnlPayrollLayout);
        pnlPayrollLayout.setHorizontalGroup(
            pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPayrollLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel58)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPayrollLayout.createSequentialGroup()
                .addContainerGap(86, Short.MAX_VALUE)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPayrollLayout.createSequentialGroup()
                        .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel78)
                            .addComponent(jLabel79)
                            .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblUserBulanTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUserJabatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUserDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUserNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUserTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPayrollLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlPayrollLayout.createSequentialGroup()
                                    .addComponent(jLabel81)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblUserGaji, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(pnlPayrollLayout.createSequentialGroup()
                                        .addComponent(jLabel86)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblUserPotongan, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pnlPayrollLayout.createSequentialGroup()
                                        .addComponent(jLabel83)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblUserTunjangan, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(pnlPayrollLayout.createSequentialGroup()
                                .addComponent(jLabel82)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblUserJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))))
                .addGap(21, 21, 21))
        );
        pnlPayrollLayout.setVerticalGroup(
            pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPayrollLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserTanggal)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserBulanTahun)
                    .addComponent(jLabel85))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserNama)
                    .addComponent(jLabel78))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserJabatan)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserDepartment)
                    .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel81)
                    .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblUserGaji)
                        .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel83)
                    .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblUserTunjangan)
                        .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel86, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblUserPotongan)
                        .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPayrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblUserJumlah)
                        .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel58)
                .addGap(179, 179, 179))
        );

        user.add(pnlPayroll);
        pnlPayroll.setBounds(240, 160, 450, 240);

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel53.setText("Transaction No :");
        user.add(jLabel53);
        jLabel53.setBounds(580, 90, 140, 14);

        lblTransactionId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        user.add(lblTransactionId);
        lblTransactionId.setBounds(700, 90, 80, 20);

        btnPdf.setText("Generate Script");
        btnPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPdfActionPerformed(evt);
            }
        });
        user.add(btnPdf);
        btnPdf.setBounds(360, 410, 160, 23);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/payroll7.jpg"))); // NOI18N
        user.add(jLabel1);
        jLabel1.setBounds(0, 0, 880, 495);

        jPanel1.add(user, "card2");

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 880, 500);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed
        verifyButtonPress();
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
        verifyButtonPress();
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
                tableModel.setValueAt(txtCabangBank.getText().toUpperCase(), row, 2);
                tableModel.setValueAt(txtAlamatBank.getText(), row, 3);

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

        int x = SUtility.msq(this, "Are you sure?");
        if (x == 0) {
            try {
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

        }
    }//GEN-LAST:event_btnSaveToDatabaseBankActionPerformed

    private void btnEmployeeSaveToDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeSaveToDatabaseActionPerformed
        int x = SUtility.msq(this, "Are you sure?");
        if (x == 0) {
            try {
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

                myRs.close();
                myStmt.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Please Ensure there is no item\n"
                        + " with the same id", "App Information", 1);
                Logger.getLogger(FrmMainPayroll.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnEmployeeSaveToDatabaseActionPerformed

    private void btnDepartmentSaveToDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepartmentSaveToDatabaseActionPerformed
        int x = SUtility.msq(this, "Are you sure?");
        if (x == 0) {
            try {
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

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        loginAdmin();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtLoginUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginUsernameActionPerformed
        loginAdmin();
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

                myRs.close();
                myStmt.close();

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
        checkpayroll();
    }//GEN-LAST:event_btnUserPayrollbtnPayrollActionPerformed

    private void jButton12jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12jButton10ActionPerformed
        changeMainLayout(main);
    }//GEN-LAST:event_jButton12jButton10ActionPerformed

    private void btnVerifyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnVerifyKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_btnVerifyKeyTyped

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        executeBrowse();
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPdfActionPerformed
        // TODO add your handling code here:
        toPDF(Integer.valueOf(lblTransactionId.getText()));

    }//GEN-LAST:event_btnPdfActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        DlgInfo info = new DlgInfo(this, true);
        info.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnLoginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnLoginKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLoginKeyReleased

    private void txtLoginPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginPasswordActionPerformed
        loginAdmin();
    }//GEN-LAST:event_txtLoginPasswordActionPerformed

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
            @Override
            public void run() {
                new FrmMainPayroll().setVisible(true);
            }
        });
    }

    private void verifyButtonPress() {
        pnlPayroll.setVisible(false);
        verifyEmployee();
        if (foundEmployee == true) {
            foundEmployee = false;
            changeMainLayout(user);
        } else if (txtID.getText().trim().equals("exit")) {
            System.exit(0);
        } else if (txtID.getText().trim().equals("admin")) {
            changeMainLayout(adminLogin);
        } else if (txtID.getText().trim().equals("about")) {
            DlgAbout about = new DlgAbout(this, true);
            about.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No Such User/command", "App Info", 2);
        }
        btnPdf.setVisible(false);
    }

    private void loginAdmin() {
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

    }

    private void executeBrowse() {
        DlgEmployeeList employee = new DlgEmployeeList(this, true, myConn);
        employee.setVisible(true);
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
    private javax.swing.JButton btnBrowse;
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
    private javax.swing.JButton btnPdf;
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
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
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
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
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
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
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
    private javax.swing.JLabel lblTransactionId;
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
    private javax.swing.JPanel pnlPayroll;
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
    public static javax.swing.JTextField txtEmployeeTransaction;
    private javax.swing.JTextField txtEmployeeTunjangan;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtIdBank;
    private javax.swing.JPasswordField txtLoginPassword;
    private javax.swing.JTextField txtLoginUsername;
    private javax.swing.JTextField txtNamaBank;
    private javax.swing.JTextField txtTransactionDeduction;
    private javax.swing.JTextField txtTransactionTotalAbsent;
    private javax.swing.JPanel user;
    private javax.swing.JPanel viewTransaction;
    // End of variables declaration//GEN-END:variables
}
