package gui;

import backend.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class KontakFormPanel extends JPanel {
    private JTextField txtNama, txtTelepon, txtEmail, txtAlamat;
    private JComboBox<String> cmbGrup;
    private JComboBox<String> cmbTipeKontak;
    private JPanel panelDetailKhusus;
    private JCheckBox chkFavorite;
    
    // Field khusus untuk KontakBisnis
    private JTextField txtPerusahaan, txtJabatan, txtWebsite;
    
    // Field khusus untuk KontakKeluarga
    private JTextField txtHubungan, txtTanggalLahir, txtCatatanKeluarga;
    
    public KontakFormPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Form Kontak"
        ));
        
        JPanel panelUtama = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tipe Kontak
        gbc.gridx = 0; gbc.gridy = 0;
        panelUtama.add(new JLabel("Tipe Kontak:"), gbc);
        
        gbc.gridx = 1;
        cmbTipeKontak = new JComboBox<>(new String[]{"Umum", "Bisnis", "Keluarga"});
        panelUtama.add(cmbTipeKontak, gbc);
        
        // Nama
        gbc.gridx = 0; gbc.gridy = 1;
        panelUtama.add(new JLabel("Nama*:"), gbc);
        
        gbc.gridx = 1;
        txtNama = new JTextField(20);
        panelUtama.add(txtNama, gbc);
        
        // Telepon
        gbc.gridx = 0; gbc.gridy = 2;
        panelUtama.add(new JLabel("Telepon*:"), gbc);
        
        gbc.gridx = 1;
        txtTelepon = new JTextField(20);
        panelUtama.add(txtTelepon, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        panelUtama.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panelUtama.add(txtEmail, gbc);
        
        // Alamat
        gbc.gridx = 0; gbc.gridy = 4;
        panelUtama.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1;
        txtAlamat = new JTextField(20);
        panelUtama.add(txtAlamat, gbc);
        
        // Grup
        gbc.gridx = 0; gbc.gridy = 5;
        panelUtama.add(new JLabel("Grup:"), gbc);
        
        gbc.gridx = 1;
        cmbGrup = new JComboBox<>();
        panelUtama.add(cmbGrup, gbc);
        
        // Favorite
        gbc.gridx = 0; gbc.gridy = 6;
        panelUtama.add(new JLabel(""), gbc);
        
        gbc.gridx = 1;
        chkFavorite = new JCheckBox("Tambahkan ke Favorit");
        panelUtama.add(chkFavorite, gbc);
        
        // Panel untuk detail khusus
        panelDetailKhusus = new JPanel();
        panelDetailKhusus.setLayout(new CardLayout());
        panelDetailKhusus.setBorder(BorderFactory.createTitledBorder("Detail Khusus"));
        
        // Panel untuk Bisnis
        JPanel panelBisnis = new JPanel(new GridLayout(3, 2, 5, 5));
        panelBisnis.add(new JLabel("Perusahaan:"));
        txtPerusahaan = new JTextField();
        panelBisnis.add(txtPerusahaan);
        
        panelBisnis.add(new JLabel("Jabatan:"));
        txtJabatan = new JTextField();
        panelBisnis.add(txtJabatan);
        
        panelBisnis.add(new JLabel("Website:"));
        txtWebsite = new JTextField();
        panelBisnis.add(txtWebsite);
        
        // Panel untuk Keluarga
        JPanel panelKeluarga = new JPanel(new GridLayout(3, 2, 5, 5));
        panelKeluarga.add(new JLabel("Hubungan:"));
        txtHubungan = new JTextField();
        panelKeluarga.add(txtHubungan);
        
        panelKeluarga.add(new JLabel("Tanggal Lahir:"));
        txtTanggalLahir = new JTextField();
        panelKeluarga.add(txtTanggalLahir);
        
        panelKeluarga.add(new JLabel("Catatan:"));
        txtCatatanKeluarga = new JTextField();
        panelKeluarga.add(txtCatatanKeluarga);
        
        // Panel untuk Umum (kosong)
        JPanel panelUmum = new JPanel();
        panelUmum.add(new JLabel("Tidak ada detail khusus"));
        
        // Tambahkan semua ke CardLayout
        panelDetailKhusus.add(panelUmum, "Umum");
        panelDetailKhusus.add(panelBisnis, "Bisnis");
        panelDetailKhusus.add(panelKeluarga, "Keluarga");
        
        // Tambahkan panel detail ke form
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelUtama.add(panelDetailKhusus, gbc);
        
        add(panelUtama, BorderLayout.CENTER);
        
        // Listener untuk combo box tipe kontak
        cmbTipeKontak.addActionListener(e -> {
            String tipe = (String) cmbTipeKontak.getSelectedItem();
            CardLayout cl = (CardLayout) panelDetailKhusus.getLayout();
            cl.show(panelDetailKhusus, tipe);
        });
    }
    
    public Kontak getKontakFromForm(ArrayList<GrupKontak> daftarGrup) 
            throws backend.InvalidPhoneNumberException {
        String tipe = (String) cmbTipeKontak.getSelectedItem();
        String nama = txtNama.getText();
        String telepon = txtTelepon.getText();
        
        Kontak kontak = null;
        
        switch (tipe) {
            case "Bisnis":
                kontak = new KontakBisnis(nama, telepon, txtPerusahaan.getText());
                ((KontakBisnis) kontak).setJabatan(txtJabatan.getText());
                ((KontakBisnis) kontak).setWebsite(txtWebsite.getText());
                break;
            case "Keluarga":
                kontak = new KontakKeluarga(nama, telepon, txtHubungan.getText());
                ((KontakKeluarga) kontak).setTanggalLahir(txtTanggalLahir.getText());
                ((KontakKeluarga) kontak).setCatatanKeluarga(txtCatatanKeluarga.getText());
                break;
            default:
                kontak = new Kontak(nama, telepon);
                break;
        }
        
        kontak.setEmail(txtEmail.getText());
        kontak.setAlamat(txtAlamat.getText());
        kontak.setAsFavorite(chkFavorite.isSelected());
        
        String grupTerpilih = (String) cmbGrup.getSelectedItem();
        if (grupTerpilih != null && !grupTerpilih.equals("Tanpa Grup")) {
            for (GrupKontak grup : daftarGrup) {
                if (grup.getNamaGrup().equals(grupTerpilih)) {
                    kontak.setGrup(grup);
                    break;
                }
            }
        }
        
        return kontak;
    }
    
    public void fillForm(Kontak kontak) {
        txtNama.setText(kontak.getNama());
        txtTelepon.setText(kontak.getNomorTelepon());
        txtEmail.setText(kontak.getEmail());
        txtAlamat.setText(kontak.getAlamat());
        chkFavorite.setSelected(kontak.isFavorite());
        
        if (kontak.getGrup() != null) {
            cmbGrup.setSelectedItem(kontak.getGrup().getNamaGrup());
        }
        
        if (kontak instanceof KontakBisnis) {
            cmbTipeKontak.setSelectedItem("Bisnis");
            KontakBisnis bisnis = (KontakBisnis) kontak;
            txtPerusahaan.setText(bisnis.getNamaPerusahaan());
            txtJabatan.setText(bisnis.getJabatan());
            txtWebsite.setText(bisnis.getWebsite());
        } else if (kontak instanceof KontakKeluarga) {
            cmbTipeKontak.setSelectedItem("Keluarga");
            KontakKeluarga keluarga = (KontakKeluarga) kontak;
            txtHubungan.setText(keluarga.getHubunganKeluarga());
            txtTanggalLahir.setText(keluarga.getTanggalLahir());
            txtCatatanKeluarga.setText(keluarga.getCatatanKeluarga());
        } else {
            cmbTipeKontak.setSelectedItem("Umum");
        }
    }
    
    public void resetForm() {
        txtNama.setText("");
        txtTelepon.setText("");
        txtEmail.setText("");
        txtAlamat.setText("");
        txtPerusahaan.setText("");
        txtJabatan.setText("");
        txtWebsite.setText("");
        txtHubungan.setText("");
        txtTanggalLahir.setText("");
        txtCatatanKeluarga.setText("");
        chkFavorite.setSelected(false);
        if (cmbGrup.getItemCount() > 0) cmbGrup.setSelectedIndex(0);
        cmbTipeKontak.setSelectedIndex(0);
    }
    
    public void setDaftarGrup(ArrayList<GrupKontak> daftarGrup) {
        cmbGrup.removeAllItems();
        cmbGrup.addItem("Tanpa Grup");
        for (GrupKontak grup : daftarGrup) {
            cmbGrup.addItem(grup.getNamaGrup());
        }
    }
    
    public boolean isFormValid() {
        return !txtNama.getText().trim().isEmpty() &&
               !txtTelepon.getText().trim().isEmpty();
    }
}