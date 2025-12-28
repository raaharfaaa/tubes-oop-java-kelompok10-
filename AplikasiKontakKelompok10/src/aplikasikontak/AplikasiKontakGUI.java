package aplikasikontak;

import backend.*;
import export.*;
import gui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.*;

public class AplikasiKontakGUI extends JFrame {
    private KontakManager kontakManager;
    private Export exportController;
    private KontakFormPanel formPanel;
    private KontakListPanel listPanel;
    private JTree treeGrup;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    
    // Tombol
    private JButton btnTambah, btnEdit, btnHapus, btnSimpan, btnBatal, btnRefresh;
    private JButton btnExport, btnImport;
    private boolean modeEdit = false;
    private Kontak kontakSedangDiedit = null;
    
    // Menu items
    private JMenuItem menuExportCSV, menuExportExcel, menuExportFavorites, menuExportByGroup;
    private JMenuItem menuImportCSV, menuBackup;
    
    public AplikasiKontakGUI() {
        kontakManager = new KontakManager();
        exportController = new Export(kontakManager);
        initComponents();
        setupLayout();
        setupMenuBar();
        setupEventListeners();
        loadSampleData();
    }
    
    private void initComponents() {
        setTitle("Aplikasi Manajemen Kontak - Kelompok 10");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Set icon jika ada
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Ignore jika icon tidak ada
        }
        
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
        treeScroll.setBorder(BorderFactory.createTitledBorder("Navigasi"));
        treeScroll.setPreferredSize(new Dimension(250, 300));
        
        panelKiri.add(treeScroll, BorderLayout.NORTH);
        panelKiri.add(formPanel, BorderLayout.CENTER);
        
        // Panel kanan
        JPanel panelKanan = new JPanel(new BorderLayout());
        panelKanan.add(listPanel, BorderLayout.CENTER);
        
        // Panel tombol utama
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
        
        // Panel tombol ekspor/impor (toolbar)
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        btnImport = createToolbarButton("📥 Import", "Import dari CSV");
        btnImport.addActionListener(e -> importData());
        
        btnExport = createToolbarButton("📤 Export", "Export Data");
        
        // Dropdown untuk export
        btnExport.addActionListener(e -> {
            JPopupMenu exportMenu = new JPopupMenu();
            
            JMenuItem csvItem = new JMenuItem("Export ke CSV");
            csvItem.addActionListener(ev -> exportToCSV());
            
            JMenuItem excelItem = new JMenuItem("Export ke Excel");
            excelItem.addActionListener(ev -> exportToExcel());
            
            JMenuItem favItem = new JMenuItem("Export Kontak Favorite");
            favItem.addActionListener(ev -> exportFavorites());
            
            JMenuItem groupItem = new JMenuItem("Export Per Grup...");
            groupItem.addActionListener(ev -> exportByGroup());
            
            exportMenu.add(csvItem);
            exportMenu.add(excelItem);
            exportMenu.addSeparator();
            exportMenu.add(favItem);
            exportMenu.add(groupItem);
            
            exportMenu.show(btnExport, 0, btnExport.getHeight());
        });
        
        JButton btnBackup = createToolbarButton("💾 Backup", "Backup Data");
        btnBackup.addActionListener(e -> backupData());
        
        toolBar.add(btnImport);
        toolBar.add(btnExport);
        toolBar.add(btnBackup);
        toolBar.add(Box.createHorizontalGlue());
        
        // Status bar
        JLabel statusLabel = new JLabel("Siap");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        toolBar.add(statusLabel);
        
        // Main layout
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, panelKiri, panelKanan
        );
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
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
    
    private JButton createToolbarButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        return button;
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu File
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');
        
        // New
        JMenuItem menuNew = new JMenuItem("Baru");
        menuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        menuNew.addActionListener(e -> modeTambah());
        menuFile.add(menuNew);
        
        menuFile.addSeparator();
        
        // Import
        menuImportCSV = new JMenuItem("Import dari CSV...");
        menuImportCSV.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        menuImportCSV.addActionListener(e -> importData());
        menuFile.add(menuImportCSV);
        
        // Export submenu
        JMenu menuExport = new JMenu("Ekspor ke...");
        menuExport.setMnemonic('E');
        
        menuExportCSV = new JMenuItem("Format CSV");
        menuExportCSV.addActionListener(e -> exportToCSV());
        menuExport.add(menuExportCSV);
        
        menuExportExcel = new JMenuItem("Format Excel");
        menuExportExcel.addActionListener(e -> exportToExcel());
        menuExport.add(menuExportExcel);
        
        menuExport.addSeparator();
        
        menuExportFavorites = new JMenuItem("Kontak Favorite");
        menuExportFavorites.addActionListener(e -> exportFavorites());
        menuExport.add(menuExportFavorites);
        
        menuExportByGroup = new JMenuItem("Per Grup...");
        menuExportByGroup.addActionListener(e -> exportByGroup());
        menuExport.add(menuExportByGroup);
        
        menuFile.add(menuExport);
        menuFile.addSeparator();
        
        // Backup
        menuBackup = new JMenuItem("Backup Data");
        menuBackup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        menuBackup.addActionListener(e -> backupData());
        menuFile.add(menuBackup);
        
        menuFile.addSeparator();
        
        // Print (optional)
        JMenuItem menuPrint = new JMenuItem("Cetak...");
        menuPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        menuPrint.addActionListener(e -> printData());
        menuFile.add(menuPrint);
        
        menuFile.addSeparator();
        
        // Exit
        JMenuItem menuExit = new JMenuItem("Keluar");
        menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        menuExit.addActionListener(e -> System.exit(0));
        menuFile.add(menuExit);
        
        // Menu Edit
        JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic('E');
        
        JMenuItem menuEditContact = new JMenuItem("Edit Kontak");
        menuEditContact.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        menuEditContact.addActionListener(e -> modeEdit());
        menuEdit.add(menuEditContact);
        
        JMenuItem menuDeleteContact = new JMenuItem("Hapus Kontak");
        menuDeleteContact.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        menuDeleteContact.addActionListener(e -> hapusKontak());
        menuEdit.add(menuDeleteContact);
        
        menuEdit.addSeparator();
        
        JMenuItem menuFind = new JMenuItem("Cari...");
        menuFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        menuFind.addActionListener(e -> listPanel.getTxtCari().requestFocus());
        menuEdit.add(menuFind);
        
        // Menu View
        JMenu menuView = new JMenu("Tampilan");
        menuView.setMnemonic('V');
        
        JCheckBoxMenuItem menuShowFavorites = new JCheckBoxMenuItem("Tampilkan Hanya Favorite");
        menuShowFavorites.addActionListener(e -> {
            if (menuShowFavorites.isSelected()) {
                ArrayList<Kontak> favorit = kontakManager.getKontakFavorit();
                listPanel.updateKontakList(favorit);
            } else {
                refreshData();
            }
        });
        menuView.add(menuShowFavorites);
        
        // Menu Help
        JMenu menuHelp = new JMenu("Bantuan");
        menuHelp.setMnemonic('B');
        
        JMenuItem menuAbout = new JMenuItem("Tentang");
        menuAbout.addActionListener(e -> showAboutDialog());
        menuHelp.add(menuAbout);
        
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuView);
        menuBar.add(menuHelp);
        
        setJMenuBar(menuBar);
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
                updateStatus("Menampilkan grup: " + grup.getNamaGrup() + " (" + kontakByGrup.size() + " kontak)");
            } else if ("Favorit".equals(userObject)) {
                ArrayList<Kontak> favorit = kontakManager.getKontakFavorit();
                listPanel.updateKontakList(favorit);
                updateStatus("Menampilkan kontak favorit: " + favorit.size() + " kontak");
            } else if ("Grup Kontak".equals(userObject)) {
                refreshData();
            }
        });
        
        listPanel.getListKontak().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Kontak kontak = listPanel.getKontakTerpilih();
                if (kontak != null) {
                    //tampilkanDetailKontak(kontak);
                }
            }
        });
        
        // Double click untuk edit
        listPanel.getListKontak().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modeEdit();
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
        
        // Shortcut keys
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "tambah"
        );
        getRootPane().getActionMap().put("tambah", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modeTambah();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "simpan"
        );
        getRootPane().getActionMap().put("simpan", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanKontak();
            }
        });
    }
    
    // ============ METHOD EKSPOR/IMPOR ============
    
    void exportToCSV() {
        boolean success = exportController.exportToCSV();
        if (success) {
            updateStatus("Data berhasil diekspor ke CSV");
        }
    }
    
    private void exportToExcel() {
        boolean success = exportController.exportToExcel();
        if (success) {
            updateStatus("Data berhasil diekspor ke Excel");
        }
    }
    
    private void exportFavorites() {
        boolean success = exportController.exportFavorites();
        if (success) {
            updateStatus("Kontak favorite berhasil diekspor");
        }
    }
    
    private void exportByGroup() {
        // Ambil daftar grup yang ada
        ArrayList<String> groups = new ArrayList<>();
        for (GrupKontak grup : kontakManager.getDaftarGrup()) {
            groups.add(grup.getNamaGrup());
        }
        
        if (groups.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tidak ada grup yang tersedia!",
                "Tidak Ada Grup",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String selectedGroup = (String) JOptionPane.showInputDialog(
            this,
            "Pilih grup yang akan diekspor:",
            "Ekspor Per Grup",
            JOptionPane.QUESTION_MESSAGE,
            null,
            groups.toArray(),
            groups.get(0)
        );
        
        if (selectedGroup != null && !selectedGroup.isEmpty()) {
            boolean success = exportController.exportByGroup(selectedGroup);
            if (success) {
                updateStatus("Grup '" + selectedGroup + "' berhasil diekspor");
            }
        }
    }
    
    private void importData() {
        int importedCount = exportController.importFromCSV();
        if (importedCount > 0) {
            refreshData();
            updateStatus(importedCount + " kontak berhasil diimport");
        }
    }
    
    private void backupData() {
        String backupFile = exportController.backupData();
        if (backupFile != null) {
            updateStatus("Backup berhasil dibuat: " + new File(backupFile).getName());
        }
    }
    
    // ============ METHOD UTAMA APLIKASI ============
    
    private void modeTambah() {
        modeEdit = false;
        kontakSedangDiedit = null;
        formPanel.resetForm();
        updateStatus("Mode: Tambah Kontak Baru");
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        formPanel.requestFocus();
    }
    
    private void modeEdit() {
        Kontak kontak = listPanel.getKontakTerpilih();
        if (kontak != null) {
            modeEdit = true;
            kontakSedangDiedit = kontak;
            formPanel.fillForm(kontak);
            updateStatus("Mode: Edit Kontak - " + kontak.getNama());
            btnTambah.setEnabled(false);
            formPanel.requestFocus();
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
                updateStatus("Kontak '" + kontak.getNama() + "' berhasil diperbarui");
            } else {
                kontakManager.tambahKontak(kontak);
                JOptionPane.showMessageDialog(this,
                    "Kontak berhasil ditambahkan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                updateStatus("Kontak '" + kontak.getNama() + "' berhasil ditambahkan");
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
                    updateStatus("Kontak '" + kontak.getNama() + "' berhasil dihapus");
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
            updateStatus("Hasil pencarian: " + hasil.size() + " kontak ditemukan");
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
        updateStatus("Total kontak: " + kontakManager.getDaftarKontak().size());
        btnEdit.setEnabled(true);
        btnHapus.setEnabled(true);
        btnTambah.setEnabled(true);
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
        updateStatus("Siap");
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(true);
        btnHapus.setEnabled(true);
    }
    
    private void updateStatus(String message) {
        // Update status di toolbar
        for (Component comp : ((JToolBar)getContentPane().getComponent(0)).getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel)comp).setText(message);
                break;
            }
        }
    }
    
    private void loadSampleData() {
        try {
            KontakBisnis bisnis1 = new KontakBisnis("PT ABC", "081234567890", "PT ABC Corp");
            bisnis1.setEmail("info@abc.com");
            bisnis1.setGrup(new GrupBisnis());
            kontakManager.tambahKontak(bisnis1);
            
            KontakKeluarga keluarga1 = new KontakKeluarga("Budi", "081298765432", "Ayah");
            keluarga1.setAsFavorite(true);
            keluarga1.getFavoritInfo().setRating(5);
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
    
    // ============ FITUR TAMBAHAN ============
    
    private void showAboutDialog() {
        String aboutText = 
            "<html><center>" +
            "<h2>Aplikasi Manajemen Kontak</h2>" +
            "<p>Versi 2.0</p>" +
            "<p>Dibuat oleh: Kelompok 10</p>" +
            "<p>Fitur:</p>" +
            "<ul style='text-align:left'>" +
            "<li>Manajemen Kontak (Tambah, Edit, Hapus)</li>" +
            "<li>Kategori Kontak (Bisnis, Keluarga, Teman)</li>" +
            "<li>Kontak Favorite dengan rating</li>" +
            "<li>Ekspor/Import CSV dan Excel</li>" +
            "<li>Backup data otomatis</li>" +
            "<li>Pencarian kontak</li>" +
            "</ul>" +
            "</center></html>";
        
        JOptionPane.showMessageDialog(this, aboutText, 
            "Tentang Aplikasi", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printData() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        
        StringBuilder sb = new StringBuilder();
        sb.append("DAFTAR KONTAK\n");
        sb.append("========================================\n");
        sb.append(String.format("%-20s %-15s %-10s\n", "Nama", "Telepon", "Grup"));
        sb.append("----------------------------------------\n");
        
        for (Kontak kontak : kontakManager.getDaftarKontak()) {
            sb.append(String.format("%-20s %-15s %-10s\n",
                kontak.getNama().length() > 20 ? kontak.getNama().substring(0, 17) + "..." : kontak.getNama(),
                kontak.getNomorTelepon(),
                kontak.getGrup() != null ? kontak.getGrup().getNamaGrup() : "-"
            ));
        }
        
        sb.append("========================================\n");
        sb.append("Total: " + kontakManager.getDaftarKontak().size() + " kontak\n");
        
        textArea.setText(sb.toString());
        
        try {
            textArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Tidak dapat mencetak: " + e.getMessage(),
                "Error Cetak", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ============ SAVE/LOAD FILE ============
    
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
            exportController = new Export(kontakManager);
            
            for (Kontak kontak : kontakList) {
                try {
                    kontakManager.tambahKontak(kontak);
                } catch (DuplicateContactException e) {
                    System.err.println("Duplikat kontak: " + e.getMessage());
                }
            }
            
            formPanel.setDaftarGrup(kontakManager.getDaftarGrup());
            refreshData();
            System.out.println("Data berhasil dimuat dari file");
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error saat membaca data: " + e.getMessage());
            loadSampleData();
        }
    }
}