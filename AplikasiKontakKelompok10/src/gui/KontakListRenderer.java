package gui;

import backend.*;
import java.awt.*;
import javax.swing.*;

// File terpisah untuk renderer jika ingin modular
public class KontakListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof Kontak) {
            Kontak kontak = (Kontak) value;
            String iconText = kontak.isFavorite() ? "★ " : "  ";
            setText(iconText + kontak.getNama() + " - " + kontak.getNomorTelepon());
            
            // Warna berdasarkan grup
            if (kontak.getGrup() != null) {
                String grupName = kontak.getGrup().getNamaGrup();
                switch (grupName) {
                    case "Bisnis":
                        setForeground(Color.BLUE);
                        break;
                    case "Keluarga":
                        setForeground(Color.RED);
                        break;
                    case "Teman":
                        setForeground(Color.GREEN.darker());
                        break;
                }
            }
        }
        
        return this;
    }
}