package gui;

import backend.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class KontakListPanel extends JPanel {
    private JList<Kontak> listKontak;
    private DefaultListModel<Kontak> listModel;
    private JTextField txtCari;
    private JButton btnCari;
    private JLabel lblTotal;
    
    public KontakListPanel() {
        setLayout(new BorderLayout(5, 5));
        
        // Panel pencarian
        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCari.add(new JLabel("Cari:"));
        txtCari = new JTextField(15);
        panelCari.add(txtCari);
        btnCari = new JButton("Cari");
        panelCari.add(btnCari);
        
        add(panelCari, BorderLayout.NORTH);
        
        // List kontak
        listModel = new DefaultListModel<>();
        listKontak = new JList<>(listModel);
        listKontak.setCellRenderer(new KontakListRenderer());
        
        JScrollPane scrollPane = new JScrollPane(listKontak);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Kontak"));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel informasi
        JPanel panelInfo = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total Kontak: 0");
        panelInfo.add(lblTotal, BorderLayout.WEST);
        
        add(panelInfo, BorderLayout.SOUTH);
    }
    
    // Inner class untuk custom renderer
    class KontakListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Kontak) {
                Kontak kontak = (Kontak) value;
                String iconText = kontak.isFavorite() ? "★ " : "  ";
                setText(iconText + kontak.getNama() + " - " + kontak.getNomorTelepon());
                
                if (kontak.getGrup() != null) {
                    if (kontak.getGrup() instanceof backend.GrupBisnis) {
                        setForeground(Color.BLUE);
                    } else if (kontak.getGrup() instanceof backend.GrupKeluarga) {
                        setForeground(Color.RED);
                    } else if (kontak.getGrup() instanceof backend.GrupTeman) {
                        setForeground(Color.GREEN.darker());
                    }
                }
            }
            
            return this;
        }
    }
    
    public void updateKontakList(ArrayList<Kontak> kontakList) {
        listModel.clear();
        for (Kontak kontak : kontakList) {
            listModel.addElement(kontak);
        }
        lblTotal.setText("Total Kontak: " + kontakList.size());
    }
    
    public JList<Kontak> getListKontak() { 
        return listKontak; 
    }
    
    public JTextField getTxtCari() { 
        return txtCari; 
    }
    
    public JButton getBtnCari() { 
        return btnCari; 
    }
    
    public Kontak getKontakTerpilih() { 
        return listKontak.getSelectedValue(); 
    }
    
    public String getKeyword() {
        return txtCari.getText().trim();
    }
}