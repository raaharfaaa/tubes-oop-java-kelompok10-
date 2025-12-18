package gui;

import backend.GrupKontak;
import backend.Kontak;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            
            if (userObject instanceof GrupKontak) {
                GrupKontak grup = (GrupKontak) userObject;
                setIcon(createIcon(grup.getKodeWarna()));
                setText(grup.getNamaGrup());
            } else if (userObject instanceof Kontak) {
                Kontak kontak = (Kontak) userObject;
                setIcon(UIManager.getIcon("Tree.leafIcon"));
                setText(kontak.getNama() + " - " + kontak.getNomorTelepon());
                if (kontak.isFavorite()) {
                    // Untuk icon bintang, bisa diganti dengan path icon yang ada
                    setIcon(new ImageIcon("src/resources/star.png"));
                }
            }
        }
        
        return this;
    }
    
    private Icon createIcon(String color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(Color.decode(color));
                g.fillOval(x, y, 12, 12);
                g.setColor(Color.BLACK);
                g.drawOval(x, y, 12, 12);
            }
            
            @Override
            public int getIconWidth() { 
                return 16; 
            }
            
            @Override
            public int getIconHeight() { 
                return 16; 
            }
        };
    }
}