package aplikasikontak;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
 public static void main(String[] args) {
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
}
