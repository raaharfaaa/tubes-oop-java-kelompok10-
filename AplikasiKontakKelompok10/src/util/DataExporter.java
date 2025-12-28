package util;

import backend.Kontak;
import java.io.*;
import java.util.ArrayList;

<<<<<<< HEAD
=======
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

>>>>>>> ebdc74dfb8274cf07e1f0ec9c466b3277594ad09
public class DataExporter {
    public static void exportToCSV(ArrayList<Kontak> kontakList, String filename) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("Nama,Telepon,Email,Alamat,Grup,Tipe,Favorite");
            
            for (Kontak kontak : kontakList) {
                String tipe = kontak.getClass().getSimpleName();
                String grup = kontak.getGrup() != null ? kontak.getGrup().getNamaGrup() : "";
                
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                    kontak.getNama(),
                    kontak.getNomorTelepon(),
                    kontak.getEmail(),
                    kontak.getAlamat(),
                    grup,
                    tipe,
                    kontak.isFavorite()
                ));
            }
            
            System.out.println("Data berhasil diexport ke " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting data: " + e.getMessage());
        }
    }
<<<<<<< HEAD
    
=======
>>>>>>> ebdc74dfb8274cf07e1f0ec9c466b3277594ad09
    public static ArrayList<Kontak> importFromCSV(String filename) {
        ArrayList<Kontak> kontakList = new ArrayList<>();
        // Implementasi import dari CSV (bisa dikembangkan)
        return kontakList;
    }
<<<<<<< HEAD
}
=======
}

>>>>>>> ebdc74dfb8274cf07e1f0ec9c466b3277594ad09
