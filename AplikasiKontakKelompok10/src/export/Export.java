package export;

import backend.Kontak;
import backend.KontakManager;
import backend.KontakBisnis;
import backend.KontakKeluarga;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Export {
    private KontakManager manager;
    
    public Export(KontakManager manager) {
        this.manager = manager;
    }
    
    // ============ EKSPOR KE CSV ============
    public boolean exportToCSV() {
        ArrayList<Kontak> daftarKontak = manager.getDaftarKontak();
        
        if (daftarKontak.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Tidak ada data kontak untuk diekspor!",
                "Data Kosong",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ekspor Data ke CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("kontak_data_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filename = fileToSave.getAbsolutePath();
            
            // Tambahkan extension .csv jika belum ada
            if (!filename.toLowerCase().endsWith(".csv")) {
                filename += ".csv";
            }
            
            boolean success = writeCSV(daftarKontak, filename);
            
            if (success) {
                JOptionPane.showMessageDialog(null,
                    String.format("Data berhasil diekspor ke CSV!\n\n" +
                                 "Lokasi: %s\n" +
                                 "Jumlah Data: %d kontak",
                                 filename, daftarKontak.size()),
                    "Ekspor Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return success;
        }
        return false;
    }
    
    private boolean writeCSV(ArrayList<Kontak> kontakList, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false), true)) {
            // Header CSV
            writer.println("Nama,Nomor Telepon,Email,Alamat,Tipe,Grup,Favorite," +
                          "Detail 1,Detail 2,Detail 3");
            
            // Data kontak
            for (Kontak kontak : kontakList) {
                String[] details = getKontakDetails(kontak);
                
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                    escapeCSV(kontak.getNama()),
                    escapeCSV(kontak.getNomorTelepon()),
                    escapeCSV(kontak.getEmail()),
                    escapeCSV(kontak.getAlamat()),
                    getTipeKontak(kontak),
                    escapeCSV(kontak.getGrup() != null ? kontak.getGrup().getNamaGrup() : ""),
                    kontak.isFavorite() ? "Ya" : "Tidak",
                    escapeCSV(details[0]),
                    escapeCSV(details[1]),
                    escapeCSV(details[2])
                ));
            }
            
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error menulis file CSV: " + e.getMessage(),
                "Ekspor Gagal",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ============ EKSPOR KE EXCEL (CSV dengan format khusus) ============
    public boolean exportToExcel() {
        ArrayList<Kontak> daftarKontak = manager.getDaftarKontak();
        
        if (daftarKontak.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Tidak ada data kontak untuk diekspor!",
                "Data Kosong",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ekspor Data ke Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Excel Files (*.xls)", "xls"));
        fileChooser.setSelectedFile(new File("kontak_data_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xls"));
        
        int userSelection = fileChooser.showSaveDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filename = fileToSave.getAbsolutePath();
            
            // Tambahkan extension .xls jika belum ada
            if (!filename.toLowerCase().endsWith(".xls")) {
                filename += ".xls";
            }
            ArrayList<Kontak> kontakList = null;
            
            boolean success = writeExcelFormat(kontakList, filename);
            
            if (success) {
                JOptionPane.showMessageDialog(null,
                    String.format("Data berhasil diekspor ke format Excel!\n\n" +
                                 "Lokasi: %s\n" +
                                 "Jumlah Data: %d kontak",
                                 filename, daftarKontak.size()),
                    "Ekspor Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return success;
        }
        return false;
    }
    
    private boolean writeExcelFormat(ArrayList<Kontak> kontakList, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false), true)) {
            // Header dengan format TAB untuk Excel
            writer.println("Nama\tNomor Telepon\tEmail\tAlamat\tTipe\tGrup\tFavorite\tDetail 1\tDetail 2\tDetail 3");
            
            // Data kontak dengan TAB separator
            for (Kontak kontak : kontakList) {
                String[] details = getKontakDetails(kontak);
                
                writer.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                    escapeExcel(kontak.getNama()),
                    escapeExcel(kontak.getNomorTelepon()),
                    escapeExcel(kontak.getEmail()),
                    escapeExcel(kontak.getAlamat()),
                    getTipeKontak(kontak),
                    escapeExcel(kontak.getGrup() != null ? kontak.getGrup().getNamaGrup() : ""),
                    kontak.isFavorite() ? "Ya" : "Tidak",
                    escapeExcel(details[0]),
                    escapeExcel(details[1]),
                    escapeExcel(details[2])
                ));
            }
            
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error menulis file Excel: " + e.getMessage(),
                "Ekspor Gagal",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ============ EKSPOR KONTAK FAVORITE ============
    public boolean exportFavorites() {
        ArrayList<Kontak> favorites = manager.getKontakFavorit();
        
        if (favorites.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Tidak ada kontak favorite untuk diekspor!",
                "Tidak Ada Favorite",
                JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ekspor Kontak Favorite");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("kontak_favorite_" + 
            new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filename = fileToSave.getAbsolutePath();
            
            if (!filename.toLowerCase().endsWith(".csv")) {
                filename += ".csv";
            }
            
            boolean success = writeFavoritesCSV(favorites, filename);
            
            if (success) {
                JOptionPane.showMessageDialog(null,
                    String.format("Kontak favorite berhasil diekspor!\n\n" +
                                 "Lokasi: %s\n" +
                                 "Jumlah Favorite: %d kontak",
                                 filename, favorites.size()),
                    "Ekspor Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return success;
        }
        return false;
    }
    
    private boolean writeFavoritesCSV(ArrayList<Kontak> favorites, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false), true)) {
            // Header khusus favorite
            writer.println("Nama,Nomor Telepon,Email,Rating Favorite,Catatan,Grup,Tipe");
            
            for (Kontak kontak : favorites) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\"",
                    escapeCSV(kontak.getNama()),
                    escapeCSV(kontak.getNomorTelepon()),
                    escapeCSV(kontak.getEmail()),
                    kontak.getFavoritInfo().getRating(),
                    escapeCSV(kontak.getFavoritInfo().getCatatanKhusus()),
                    escapeCSV(kontak.getGrup() != null ? kontak.getGrup().getNamaGrup() : ""),
                    getTipeKontak(kontak)
                ));
            }
            
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error menulis file: " + e.getMessage(),
                "Ekspor Gagal",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ============ EKSPOR PER GRUP ============
    public boolean exportByGroup(String namaGrup) {
        ArrayList<Kontak> kontakByGroup = new ArrayList<>();
        
        for (Kontak kontak : manager.getDaftarKontak()) {
            if (kontak.getGrup() != null && 
                kontak.getGrup().getNamaGrup().equalsIgnoreCase(namaGrup)) {
                kontakByGroup.add(kontak);
            }
        }
        
        if (kontakByGroup.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                String.format("Tidak ada kontak dalam grup '%s'!", namaGrup),
                "Grup Kosong",
                JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        String safeFilename = namaGrup.replaceAll("[^a-zA-Z0-9]", "_");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ekspor Kontak Grup: " + namaGrup);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File(safeFilename + "_" + 
            new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filename = fileToSave.getAbsolutePath();
            
            if (!filename.toLowerCase().endsWith(".csv")) {
                filename += ".csv";
            }
            
            boolean success = writeGroupCSV(kontakByGroup, namaGrup, filename);
            
            if (success) {
                JOptionPane.showMessageDialog(null,
                    String.format("Kontak grup '%s' berhasil diekspor!\n\n" +
                                 "Lokasi: %s\n" +
                                 "Jumlah Kontak: %d",
                                 namaGrup, filename, kontakByGroup.size()),
                    "Ekspor Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return success;
        }
        return false;
    }
    
    private boolean writeGroupCSV(ArrayList<Kontak> kontakList, String grupName, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false), true)) {
            // Header dengan informasi grup
            writer.println("Grup: " + grupName);
            writer.println("Tanggal Ekspor: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            writer.println("Jumlah Kontak: " + kontakList.size());
            writer.println(); // Baris kosong
            writer.println("Nama,Nomor Telepon,Email,Alamat,Tipe,Favorite");
            
            for (Kontak kontak : kontakList) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                    escapeCSV(kontak.getNama()),
                    escapeCSV(kontak.getNomorTelepon()),
                    escapeCSV(kontak.getEmail()),
                    escapeCSV(kontak.getAlamat()),
                    getTipeKontak(kontak),
                    kontak.isFavorite() ? "Ya" : "Tidak"
                ));
            }
            
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error menulis file: " + e.getMessage(),
                "Ekspor Gagal",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ============ BACKUP DATA ============
    public String backupData() {
        ArrayList<Kontak> daftarKontak = manager.getDaftarKontak();
        
        if (daftarKontak.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Tidak ada data untuk dibackup!",
                "Data Kosong",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        // Buat folder backup jika belum ada
        String backupDir = "backup/";
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = backupDir + "backup_kontak_" + timestamp + ".csv";
        
        boolean success = writeBackupCSV(daftarKontak, filename);
        
        if (success) {
            return filename;
        }
        return null;
    }
    
    private boolean writeBackupCSV(ArrayList<Kontak> kontakList, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false), true)) {
            // Metadata backup
            writer.println("# BACKUP DATA KONTAK");
            writer.println("# Tanggal: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            writer.println("# Jumlah Data: " + kontakList.size());
            writer.println("# FORMAT: Nama|Telepon|Email|Alamat|Grup|Tipe|Favorite|Detail");
            writer.println();
            
            // Format sederhana untuk backup
            for (Kontak kontak : kontakList) {
                String[] details = getKontakDetails(kontak);
                
                writer.println(String.format("%s|%s|%s|%s|%s|%s|%s|%s",
                    kontak.getNama(),
                    kontak.getNomorTelepon(),
                    kontak.getEmail(),
                    kontak.getAlamat(),
                    kontak.getGrup() != null ? kontak.getGrup().getNamaGrup() : "",
                    getTipeKontak(kontak),
                    kontak.isFavorite() ? "1" : "0",
                    String.join(";", details)
                ));
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error backup data: " + e.getMessage());
            return false;
        }
    }
    
    // ============ IMPORT DARI CSV ============
    public int importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Data dari CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        
        int userSelection = fileChooser.showOpenDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            
            int confirm = JOptionPane.showConfirmDialog(null,
                String.format("Import data dari %s?\n\n" +
                            "Data duplikat akan dilewati.",
                            fileToOpen.getName()),
                "Konfirmasi Import",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ArrayList<Kontak> importedContacts = readCSV(fileToOpen);
                    int successCount = 0;
                    int duplicateCount = 0;
                    
                    for (Kontak kontak : importedContacts) {
                        try {
                            manager.tambahKontak(kontak);
                            successCount++;
                        } catch (backend.DuplicateContactException e) {
                            duplicateCount++;
                        }
                    }
                    
                    showImportResult(successCount, duplicateCount);
                    return successCount;
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                        "Error membaca file CSV: " + e.getMessage(),
                        "Import Gagal",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        return 0;
    }
    
    private ArrayList<Kontak> readCSV(File file) throws Exception {
        ArrayList<Kontak> kontakList = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip komentar atau baris kosong
                }
                
                Kontak kontak = parseCSVLine(line);
                if (kontak != null) {
                    kontakList.add(kontak);
                }
            }
        }
        
        return kontakList;
    }
    
    private Kontak parseCSVLine(String line) throws Exception {
        // Simple CSV parsing (bisa ditingkatkan untuk handle quotes)
        String[] parts = line.split(",", -1); // -1 untuk keep empty strings
        
        if (parts.length < 6) {
            return null;
        }
        
        // Hapus quotes jika ada
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace("\"", "").trim();
        }
        
        String nama = parts[0];
        String telepon = parts[1];
        String email = parts.length > 2 ? parts[2] : "";
        String alamat = parts.length > 3 ? parts[3] : "";
        String grup = parts.length > 4 ? parts[4] : "";
        String tipe = parts.length > 5 ? parts[5] : "Umum";
        boolean favorite = parts.length > 6 ? parts[6].equalsIgnoreCase("Ya") : false;
        
        // Buat kontak berdasarkan tipe
        backend.Kontak kontak;
        
        try {
            if (tipe.equals("Bisnis")) {
                kontak = new KontakBisnis(nama, telepon, "Perusahaan");
            } else if (tipe.equals("Keluarga")) {
                kontak = new KontakKeluarga(nama, telepon, "Keluarga");
            } else {
                kontak = new backend.Kontak(nama, telepon);
            }
            
            kontak.setEmail(email);
            kontak.setAlamat(alamat);
            kontak.setAsFavorite(favorite);
            
            // Set grup jika ada
            if (!grup.isEmpty()) {
                for (backend.GrupKontak g : manager.getDaftarGrup()) {
                    if (g.getNamaGrup().equalsIgnoreCase(grup)) {
                        kontak.setGrup(g);
                        break;
                    }
                }
            }
            
            return kontak;
            
        } catch (backend.InvalidPhoneNumberException e) {
            System.err.println("Nomor telepon invalid: " + telepon);
            return null;
        }
    }
    
    // ============ UTILITY METHODS ============
    private String[] getKontakDetails(Kontak kontak) {
        String[] details = new String[]{"", "", ""};
        
        if (kontak instanceof KontakBisnis) {
            KontakBisnis bisnis = (KontakBisnis) kontak;
            details[0] = bisnis.getNamaPerusahaan();
            details[1] = bisnis.getJabatan();
            details[2] = bisnis.getWebsite();
        } else if (kontak instanceof KontakKeluarga) {
            KontakKeluarga keluarga = (KontakKeluarga) kontak;
            details[0] = keluarga.getHubunganKeluarga();
            details[1] = keluarga.getTanggalLahir();
            details[2] = keluarga.getCatatanKeluarga();
        }
        
        return details;
    }
    
    private String getTipeKontak(Kontak kontak) {
        if (kontak instanceof KontakBisnis) return "Bisnis";
        if (kontak instanceof KontakKeluarga) return "Keluarga";
        return "Umum";
    }
    
    private String escapeCSV(String input) {
        if (input == null) return "";
        // Jika mengandung koma atau quote, wrap dengan quotes
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }
    
    private String escapeExcel(String input) {
        if (input == null) return "";
        // Excel format biasanya aman dengan TAB separator
        return input.replace("\t", " ").replace("\n", " ");
    }
    
    private void showImportResult(int successCount, int duplicateCount) {
        StringBuilder message = new StringBuilder();
        message.append("Hasil Import:\n\n");
        message.append("✓ Berhasil diimport: ").append(successCount).append(" kontak\n");
        
        if (duplicateCount > 0) {
            message.append("⚠ Dilewati (duplikat): ").append(duplicateCount).append(" kontak\n");
        }
        
        if (successCount > 0) {
            message.append("\nData berhasil ditambahkan ke aplikasi!");
        } else {
            message.append("\nTidak ada data baru yang ditambahkan.");
        }
        
        JOptionPane.showMessageDialog(null,
            message.toString(),
            "Import Selesai",
            JOptionPane.INFORMATION_MESSAGE);
    }
}