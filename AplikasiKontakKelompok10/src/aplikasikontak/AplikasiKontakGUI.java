package aplikasikontak;

import backend.*;
import gui.*;
import util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class AplikasiKontakGUI extends JFrame {
    private KontakManager kontakManager;
    private KontakFormPanel formPanel;
    private KontakListPanel listPanel;
    private JTree treeGrup;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    
    // Tombol
    private JButton btnTambah, btnEdit, btnHapus, btnSimpan, btnBatal, btnRefresh;
    private boolean modeEdit = false;
    private Kontak kontakSedangDiedit = null;
    
    public AplikasiKontakGUI() {
        kontakManager = new KontakManager();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadSampleData();
    }
    
    private void initComponents() {
        setTitle("Aplikasi Manajemen Kontak - Kelompok 10");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        formPanel = new KontakFormPanel();
        formPanel.setDaftarGrup(kontakManager.getDaftarGrup());
        
        listPanel = new KontakListPanel();
        
        rootNode = new DefaultMutableTreeNode("Grup Kontak");
        treeModel = new DefaultTreeModel(rootNode);
        treeGrup = new JTree(treeModel);
        treeGrup.setCellRenderer(new CustomTreeCellRenderer());
        
        for (GrupKontak grup : kontakManager.getDaftarGrup()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(grup);
            rootNode.add(node);
        }
        
        DefaultMutableTreeNode favoritNode = new DefaultMutableTreeNode("Favorit");
        rootNode.add(favoritNode);
        
        treeModel.reload();
        
        for (int i = 0; i < treeGrup.getRowCount(); i++) {
            treeGrup.expandRow(i);
        }
    }
    
    private void setupLayout() {
        // Panel kiri
        JPanel panelKiri = new JPanel(new BorderLayout(10, 10));
        
        JScrollPane treeScroll = new JScrollPane(treeGrup);
        treeScroll.setBorder(BorderFactory.createTitledBorder(""));
        treeScroll.setPreferredSize(new Dimension(250, 200));
        
        panelKiri.add(treeScroll, BorderLayout.NORTH);
        panelKiri.add(formPanel, BorderLayout.CENTER);
        
        // Panel kanan
        JPanel panelKanan = new JPanel(new BorderLayout());
        panelKanan.add(listPanel, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");
        btnRefresh = new JButton("Refresh");
        
        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);
        panelTombol.add(new JSeparator(SwingConstants.VERTICAL));
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);
        panelTombol.add(new JSeparator(SwingConstants.VERTICAL));
        panelTombol.add(btnRefresh);
        
        // Main layout
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, panelKiri, panelKanan
        );
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.5);
        
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(panelTombol, BorderLayout.SOUTH);
        
        // Set mnemonic
        btnTambah.setMnemonic('T');
        btnEdit.setMnemonic('E');
        btnHapus.setMnemonic('H');
        btnSimpan.setMnemonic('S');
        btnBatal.setMnemonic('B');
        btnRefresh.setMnemonic('R');
    }
    
    private void setupEventListeners() {
        btnTambah.addActionListener(e -> modeTambah());
        btnEdit.addActionListener(e -> modeEdit());
        btnHapus.addActionListener(e -> hapusKontak());
        btnSimpan.addActionListener(e -> simpanKontak());
        btnBatal.addActionListener(e -> resetMode());
        btnRefresh.addActionListener(e -> refreshData());
        
        listPanel.getBtnCari().addActionListener(e -> cariKontak());
        listPanel.getTxtCari().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cariKontak();
                }
            }
        });
        
        treeGrup.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                treeGrup.getLastSelectedPathComponent();
            
            if (node == null) return;
            
            Object userObject = node.getUserObject();
            if (userObject instanceof GrupKontak) {
                GrupKontak grup = (GrupKontak) userObject;
                ArrayList<Kontak> kontakByGrup = kontakManager.getKontakByGrup(grup.getNamaGrup());
                listPanel.updateKontakList(kontakByGrup);
            } else if ("Favorit".equals(userObject)) {
                ArrayList<Kontak> favorit = kontakManager.getKontakFavorit();
                listPanel.updateKontakList(favorit);
            } else if ("Grup Kontak".equals(userObject)) {
                refreshData();
            }
        });
        
        listPanel.getListKontak().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Kontak kontak = listPanel.getKontakTerpilih();
                if (kontak != null) {
                    tampilkanDetailKontak(kontak);
                }
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                simpanKeFile();
            }
            
            @Override
            public void windowOpened(WindowEvent e) {
                bacaDariFile();
            }
        });
    }
    
    private void modeTambah() {
        modeEdit = false;
        kontakSedangDiedit = null;
        formPanel.resetForm();
        setStatus("Mode: Tambah Kontak Baru");
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
    }
    
    private void modeEdit() {
        Kontak kontak = listPanel.getKontakTerpilih();
        if (kontak != null) {
            modeEdit = true;
            kontakSedangDiedit = kontak;
            formPanel.fillForm(kontak);
            setStatus("Mode: Edit Kontak - " + kontak.getNama());
            btnTambah.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Pilih kontak yang akan diedit",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void simpanKontak() {
        try {
            if (!formPanel.isFormValid()) {
                JOptionPane.showMessageDialog(this,
                    "Nama dan Telepon wajib diisi!",
                    "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Kontak kontak = formPanel.getKontakFromForm(kontakManager.getDaftarGrup());
            
            if (modeEdit) {
                int index = kontakManager.getDaftarKontak().indexOf(kontakSedangDiedit);
                if (index != -1) {
                    kontakManager.getDaftarKontak().set(index, kontak);
                }
                JOptionPane.showMessageDialog(this,
                    "Kontak berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                kontakManager.tambahKontak(kontak);
                JOptionPane.showMessageDialog(this,
                    "Kontak berhasil ditambahkan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
            
            refreshData();
            resetMode();
            
        } catch (InvalidPhoneNumberException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Nomor Telepon Invalid", JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateContactException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Kontak Duplikat", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error tidak terduga: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusKontak() {
        Kontak kontak = listPanel.getKontakTerpilih();
        if (kontak != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus kontak " + kontak.getNama() + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean berhasil = kontakManager.hapusKontak(kontak.getNomorTelepon());
                if (berhasil) {
                    JOptionPane.showMessageDialog(this,
                        "Kontak berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                    formPanel.resetForm();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Pilih kontak yang akan dihapus",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void cariKontak() {
        String keyword = listPanel.getKeyword();
        if (keyword.isEmpty()) {
            refreshData();
        } else {
            ArrayList<Kontak> hasil = kontakManager.cariKontak(keyword);
            listPanel.updateKontakList(hasil);
            setStatus("Hasil pencarian: " + hasil.size() + " kontak ditemukan");
        }
    }
    
    private void tampilkanDetailKontak(Kontak kontak) {
        StringBuilder detail = new StringBuilder();
        detail.append("=== DETAIL KONTAK ===\n\n");
        detail.append(kontak.toString());
        
        if (kontak.isFavorite()) {
            detail.append("\n\n=== INFORMASI FAVORIT ===\n");
            detail.append(kontak.getFavoritInfo().toString());
        }
        
        JTextArea textArea = new JTextArea(detail.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane,
            "Detail Kontak: " + kontak.getNama(),
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshData() {
        listPanel.updateKontakList(kontakManager.getDaftarKontak());
        updateTreeKontak();
        setStatus("Total kontak: " + kontakManager.getDaftarKontak().size());
    }
    
    private void updateTreeKontak() {
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            node.removeAllChildren();
            
            Object userObject = node.getUserObject();
            if (userObject instanceof GrupKontak) {
                GrupKontak grup = (GrupKontak) userObject;
                ArrayList<Kontak> kontakByGrup = kontakManager.getKontakByGrup(grup.getNamaGrup());
                for (Kontak kontak : kontakByGrup) {
                    node.add(new DefaultMutableTreeNode(kontak));
                }
            }
        }
        
        DefaultMutableTreeNode favoritNode = (DefaultMutableTreeNode) rootNode.getLastChild();
        favoritNode.removeAllChildren();
        ArrayList<Kontak> favorit = kontakManager.getKontakFavorit();
        for (Kontak kontak : favorit) {
            favoritNode.add(new DefaultMutableTreeNode(kontak));
        }
        
        treeModel.reload();
        
        for (int i = 0; i < treeGrup.getRowCount(); i++) {
            treeGrup.expandRow(i);
        }
    }
    
    private void resetMode() {
        modeEdit = false;
        kontakSedangDiedit = null;
        formPanel.resetForm();
        setStatus("Siap");
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(true);
        btnHapus.setEnabled(true);
    }
    
    private void setStatus(String message) {
        // Untuk status bar, bisa ditambahkan nanti
        System.out.println("Status: " + message);
    }
    
    private void loadSampleData() {
        try {
            KontakBisnis bisnis1 = new KontakBisnis("PT ABC", "081234567890", "PT ABC Corp");
            bisnis1.setEmail("info@abc.com");
            bisnis1.setGrup(new GrupBisnis());
            kontakManager.tambahKontak(bisnis1);
            
            KontakKeluarga keluarga1 = new KontakKeluarga("Budi", "081298765432", "Ayah");
            keluarga1.setAsFavorite(true);
            keluarga1.getFavoritInfo().setRating(3);
            keluarga1.getFavoritInfo().setCatatanKhusus("Penting!");
            keluarga1.setGrup(new GrupKeluarga());
            kontakManager.tambahKontak(keluarga1);
            
            Kontak teman1 = new Kontak("Sari", "081312345678");
            teman1.setEmail("sari@gmail.com");
            teman1.setGrup(new GrupTeman());
            kontakManager.tambahKontak(teman1);
            
            refreshData();
            
        } catch (Exception e) {
            System.err.println("Error loading sample data: " + e.getMessage());
        }
    }
    
    // Save/Load methods
    private void simpanKeFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("kontak_data.dat"))) {
            oos.writeObject(kontakManager.getDaftarKontak());
            System.out.println("Data berhasil disimpan ke file");
        } catch (IOException e) {
            System.err.println("Error saat menyimpan data: " + e.getMessage());
        }
    }
    
    private void bacaDariFile() {
        File file = new File("kontak_data.dat");
        if (!file.exists()) {
            loadSampleData();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("kontak_data.dat"))) {
            
            ArrayList<Kontak> kontakList = (ArrayList<Kontak>) ois.readObject();
            kontakManager = new KontakManager();
            
            for (Kontak kontak : kontakList) {
                try {
                    kontakManager.tambahKontak(kontak);
                } catch (DuplicateContactException e) {
                    System.err.println("Duplikat kontak: " + e.getMessage());
                }
            }
            
            refreshData();
            System.out.println("Data berhasil dimuat dari file");
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error saat membaca data: " + e.getMessage());
            loadSampleData();
        }
    }
}
}
