package aplikasikontak;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Untuk testing ekspor langsung
        if (args.length > 0 && args[0].equals("--test-export")) {
            testExport();
            return;
        }
        
        if (args.length > 0 && args[0].equals("--test-gui")) {
            testGUI();
            return;
        }
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run application
        SwingUtilities.invokeLater(() -> {
            AplikasiKontakGUI app = new AplikasiKontakGUI();
            app.setVisible(true);
        });
    }
    
    private static void testExport() {
        System.out.println("=== Testing Export Module ===");
        
        try {
            backend.KontakManager manager = new backend.KontakManager();
            
            // Tambah sample data
            backend.KontakBisnis bisnis = new backend.KontakBisnis("PT ABC", "08123456789", "PT ABC Corp");
            bisnis.setEmail("info@abc.com");
            manager.tambahKontak(bisnis);
            
            backend.KontakKeluarga keluarga = new backend.KontakKeluarga("Budi", "08129876543", "Ayah");
            keluarga.setAsFavorite(true);
            manager.tambahKontak(keluarga);
            
            backend.Kontak teman = new backend.Kontak("Sari", "08131234567");
            teman.setEmail("sari@gmail.com");
            manager.tambahKontak(teman);
            
            // Test ekspor
            export.Export exporter = new export.Export(manager);
            
            System.out.println("1. Testing ekspor CSV...");
            boolean csvSuccess = exporter.exportToCSV();
            System.out.println("   CSV Export: " + (csvSuccess ? "SUCCESS" : "FAILED"));
            
            System.out.println("2. Testing ekspor Excel...");
            boolean excelSuccess = exporter.exportToExcel();
            System.out.println("   Excel Export: " + (excelSuccess ? "SUCCESS" : "FAILED"));
            
            System.out.println("3. Testing ekspor Favorite...");
            boolean favSuccess = exporter.exportFavorites();
            System.out.println("   Favorite Export: " + (favSuccess ? "SUCCESS" : "FAILED"));
            
            System.out.println("4. Testing backup...");
            String backupFile = exporter.backupData();
            System.out.println("   Backup: " + (backupFile != null ? "SUCCESS (" + backupFile + ")" : "FAILED"));
            
            System.out.println("=== Testing Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testGUI() {
        System.out.println("=== Testing GUI ===");
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run minimal GUI test
        SwingUtilities.invokeLater(() -> {
            AplikasiKontakGUI app = new AplikasiKontakGUI();
            app.setVisible(true);
            
            // Auto-test setelah GUI muncul
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("GUI Test: Application started successfully");
                    System.out.println("GUI Test: Window title: " + app.getTitle());
                    System.out.println("GUI Test: Testing export menu...");
                    
                    // Simulate export action
                    app.exportToCSV();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}