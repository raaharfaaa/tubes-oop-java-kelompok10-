package export;

import backend.Kontak;
import backend.KontakBisnis;
import backend.KontakKeluarga;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExcelExporter {
    
    public static boolean exportToExcelAdvanced(ArrayList<Kontak> kontakList, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false), true)) {
            // Write Excel XML/HTML format (bisa dibuka di Excel)
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<style>");
            writer.println("table { border-collapse: collapse; width: 100%; }");
            writer.println("th { background-color: #4CAF50; color: white; padding: 8px; text-align: left; }");
            writer.println("td { border: 1px solid #ddd; padding: 8px; }");
            writer.println("tr:nth-child(even) { background-color: #f2f2f2; }");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            
            // Judul
            writer.println("<h2>Data Kontak</h2>");
            writer.println("<p>Tanggal Ekspor: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + "</p>");
            writer.println("<p>Jumlah Kontak: " + kontakList.size() + "</p>");
            
            writer.println("<table>");
            writer.println("<tr>");
            writer.println("<th>No</th>");
            writer.println("<th>Nama</th>");
            writer.println("<th>Telepon</th>");
            writer.println("<th>Email</th>");
            writer.println("<th>Alamat</th>");
            writer.println("<th>Tipe</th>");
            writer.println("<th>Grup</th>");
            writer.println("<th>Favorite</th>");
            writer.println("<th>Detail</th>");
            writer.println("</tr>");
            
            int counter = 1;
            for (Kontak kontak : kontakList) {
                writer.println("<tr>");
                writer.println("<td>" + counter++ + "</td>");
                writer.println("<td>" + escapeHTML(kontak.getNama()) + "</td>");
                writer.println("<td>" + kontak.getNomorTelepon() + "</td>");
                writer.println("<td>" + escapeHTML(kontak.getEmail()) + "</td>");
                writer.println("<td>" + escapeHTML(kontak.getAlamat()) + "</td>");
                writer.println("<td>" + getTipeKontak(kontak) + "</td>");
                writer.println("<td>" + escapeHTML(kontak.getGrup() != null ? kontak.getGrup().getNamaGrup() : "") + "</td>");
                writer.println("<td>" + (kontak.isFavorite() ? "★" : "") + "</td>");
                writer.println("<td>" + getKontakDetailHTML(kontak) + "</td>");
                writer.println("</tr>");
            }
            
            writer.println("</table>");
            writer.println("</body>");
            writer.println("</html>");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static String getTipeKontak(Kontak kontak) {
        if (kontak instanceof KontakBisnis) return "Bisnis";
        if (kontak instanceof KontakKeluarga) return "Keluarga";
        return "Umum";
    }
    
    private static String getKontakDetailHTML(Kontak kontak) {
        if (kontak instanceof KontakBisnis) {
            KontakBisnis bisnis = (KontakBisnis) kontak;
            return "Perusahaan: " + escapeHTML(bisnis.getNamaPerusahaan()) + "<br>" +
                   "Jabatan: " + escapeHTML(bisnis.getJabatan()) + "<br>" +
                   "Website: " + escapeHTML(bisnis.getWebsite());
        } else if (kontak instanceof KontakKeluarga) {
            KontakKeluarga keluarga = (KontakKeluarga) kontak;
            return "Hubungan: " + escapeHTML(keluarga.getHubunganKeluarga()) + "<br>" +
                   "Tanggal Lahir: " + escapeHTML(keluarga.getTanggalLahir()) + "<br>" +
                   "Catatan: " + escapeHTML(keluarga.getCatatanKeluarga());
        }
        return "-";
    }
    
    private static String escapeHTML(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}