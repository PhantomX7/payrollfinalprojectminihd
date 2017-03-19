/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payrollfinalproject;

/**
 *
 * @author Phantom
 */
public class Employee {

    private String id;
    private String name;
    private String nik;
    private String jenisKelamin;
    private String tempatLahir;
    private String tanggalLahir;
    private String alamat;
    private String noHp;
    private String email;
    private String agama;
    private String status_perkawinan;
    private String status_pajak;
    private String id_department;
    private String tipe_Karyawan;
    private String tanggal_masuk;
    private Double gaji_kotor;
    private Double tunjangan;
    private String idBank;
    private String noRekening;

    public Employee(String id, String name, String nik, String jenisKelamin, String tempatLahir, String tanggalLahir, String alamat, String noHp, String email, String agama, String status_perkawinan, String status_pajak, String id_department, String tipe_Karyawan, String tanggal_masuk, Double gaji_kotor, Double tunjangan, String idBank, String noRekening) {
        this.id = id;
        this.name = name;
        this.nik = nik;
        this.jenisKelamin = jenisKelamin;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.alamat = alamat;
        this.noHp = noHp;
        this.email = email;
        this.agama = agama;
        this.status_perkawinan = status_perkawinan;
        this.status_pajak = status_pajak;
        this.id_department = id_department;
        this.tipe_Karyawan = tipe_Karyawan;
        this.tanggal_masuk = tanggal_masuk;
        this.gaji_kotor = gaji_kotor;
        this.tunjangan = tunjangan;
        this.idBank = idBank;
        this.noRekening = noRekening;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgama() {
        return agama;
    }

    public void setAgama(String agama) {
        this.agama = agama;
    }

    public String getStatus_perkawinan() {
        return status_perkawinan;
    }

    public void setStatus_perkawinan(String status_perkawinan) {
        this.status_perkawinan = status_perkawinan;
    }

    public String getStatus_pajak() {
        return status_pajak;
    }

    public void setStatus_pajak(String status_pajak) {
        this.status_pajak = status_pajak;
    }

    public String getId_department() {
        return id_department;
    }

    public void setId_department(String id_department) {
        this.id_department = id_department;
    }

    public String getTipe_Karyawan() {
        return tipe_Karyawan;
    }

    public void setTipe_Karyawan(String tipe_Karyawan) {
        this.tipe_Karyawan = tipe_Karyawan;
    }

    public String getTanggal_masuk() {
        return tanggal_masuk;
    }

    public void setTanggal_masuk(String tanggal_masuk) {
        this.tanggal_masuk = tanggal_masuk;
    }

    public Double getGaji_kotor() {
        return gaji_kotor;
    }

    public void setGaji_kotor(Double gaji_kotor) {
        this.gaji_kotor = gaji_kotor;
    }

    public Double getTunjangan() {
        return tunjangan;
    }

    public void setTunjangan(Double tunjangan) {
        this.tunjangan = tunjangan;
    }

    public String getIdBank() {
        return idBank;
    }

    public void setIdBank(String idBank) {
        this.idBank = idBank;
    }

    public String getNoRekening() {
        return noRekening;
    }

    public void setNoRekening(String noRekening) {
        this.noRekening = noRekening;
    }
    
    
}
