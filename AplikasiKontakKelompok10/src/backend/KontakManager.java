package backend;

import java.util.ArrayList;

public class KontakManager {
private ArrayList<Kontak> daftarKontak;
    private ArrayList<GrupKontak> daftarGrup;
    
    public KontakManager() {
        daftarKontak = new ArrayList<>();
        daftarGrup = new ArrayList<>();
        inisialisasiGrupDefault();
    }
    
    private void inisialisasiGrupDefault() {
        daftarGrup.add(new GrupBisnis());
        daftarGrup.add(new GrupKeluarga());
        daftarGrup.add(new GrupTeman());
    }
    
    public void tambahKontak(Kontak kontak) throws DuplicateContactException {
        for (Kontak k : daftarKontak) {
            if (k.getNomorTelepon().equals(kontak.getNomorTelepon())) {
                throw new DuplicateContactException(
                    "Kontak dengan nomor telepon " + kontak.getNomorTelepon() + " sudah ada"
                );
            }
        }
        daftarKontak.add(kontak);
    }
    
    public boolean hapusKontak(String nomorTelepon) {
        for (int i = 0; i < daftarKontak.size(); i++) {
            if (daftarKontak.get(i).getNomorTelepon().equals(nomorTelepon)) {
                daftarKontak.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<Kontak> cariKontak(String keyword) {
        ArrayList<Kontak> hasil = new ArrayList<>();
        for (Kontak kontak : daftarKontak) {
            if (kontak.cocokDenganKriteria(keyword)) {
                hasil.add(kontak);
            }
        }
        return hasil;
    }
    
    public ArrayList<Kontak> getDaftarKontak() { 
        return daftarKontak; 
    }
    
    public ArrayList<GrupKontak> getDaftarGrup() { 
        return daftarGrup; 
    }
    
    public ArrayList<Kontak> getKontakFavorit() {
        ArrayList<Kontak> favorit = new ArrayList<>();
        for (Kontak kontak : daftarKontak) {
            if (kontak.isFavorite()) {
                favorit.add(kontak);
            }
        }
        return favorit;
    }
    
    public ArrayList<Kontak> getKontakByGrup(String namaGrup) {
        ArrayList<Kontak> hasil = new ArrayList<>();
        for (Kontak kontak : daftarKontak) {
            if (kontak.getGrup() != null && 
                kontak.getGrup().getNamaGrup().equals(namaGrup)) {
                hasil.add(kontak);
            }
        }
        return hasil;
    }
}
