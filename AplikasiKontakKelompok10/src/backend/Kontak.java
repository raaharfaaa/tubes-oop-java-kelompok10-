package backend;

public class Kontak implements ICariKontak {
private String nama;
    private String nomorTelepon;
    private String email;
    private String alamat;
    private GrupKontak grup;
    private boolean isFavorite;
    private KontakFavorit favoritInfo;
    
    // Constructor
    public Kontak(String nama, String nomorTelepon) throws InvalidPhoneNumberException {
        validasiNomorTelepon(nomorTelepon);
        this.nama = nama;
        this.nomorTelepon = nomorTelepon;
        this.email = "";
        this.alamat = "";
        this.isFavorite = false;
        this.favoritInfo = new KontakFavorit();
    }
    
    // Validasi nomor telepon
    private void validasiNomorTelepon(String nomor) throws InvalidPhoneNumberException {
        if (nomor == null || nomor.trim().isEmpty()) {
            throw new InvalidPhoneNumberException("Nomor telepon tidak boleh kosong");
        }
        
        String cleaned = nomor.replaceAll("[^0-9]", "");
        
        if (cleaned.length() < 10 || cleaned.length() > 15) {
            throw new InvalidPhoneNumberException("Nomor telepon harus 10-15 digit");
        }
        
        if (!cleaned.matches("[0-9]+")) {
            throw new InvalidPhoneNumberException("Nomor telepon hanya boleh berisi angka");
        }
    }
    
    // Implementasi interface ICariKontak
    @Override
    public boolean cocokDenganKriteria(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        
        String lowerKeyword = keyword.toLowerCase();
        return nama.toLowerCase().contains(lowerKeyword) ||
               nomorTelepon.contains(keyword) ||
               email.toLowerCase().contains(lowerKeyword) ||
               alamat.toLowerCase().contains(lowerKeyword);
    }
    
    @Override
    public String getInformasiPencarian() {
        return String.format("%s - %s [%s]", nama, nomorTelepon, 
                           grup != null ? grup.getNamaGrup() : "Tanpa Grup");
    }
    
    // Getter dan Setter dengan validasi
    public void setNomorTelepon(String nomorTelepon) throws InvalidPhoneNumberException {
        validasiNomorTelepon(nomorTelepon);
        this.nomorTelepon = nomorTelepon;
    }
    
    public void setAsFavorite(boolean favorite) {
        this.isFavorite = favorite;
        if (!favorite) {
            favoritInfo = new KontakFavorit();
        }
    }
    
    // Getter dan Setter lainnya
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNomorTelepon() { return nomorTelepon; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public GrupKontak getGrup() { return grup; }
    public void setGrup(GrupKontak grup) { this.grup = grup; }
    public boolean isFavorite() { return isFavorite; }
    public KontakFavorit getFavoritInfo() { return favoritInfo; }
    
    @Override
    public String toString() {
        return String.format("Nama: %s\nTelepon: %s\nEmail: %s\nAlamat: %s\nGrup: %s\nFavorit: %s",
                nama, nomorTelepon, email, alamat, 
                grup != null ? grup.getNamaGrup() : "Tidak ada",
                isFavorite ? "Ya" : "Tidak");
    }
    
    // ========== INNER CLASS ==========
    public class KontakFavorit {
        private String catatanKhusus;
        private int rating;
        
        public KontakFavorit() {
            this.catatanKhusus = "";
            this.rating = 3;
        }
        
        public void setCatatanKhusus(String catatan) {
            this.catatanKhusus = catatan;
        }
        
        public void setRating(int rating) {
            if (rating >= 1 && rating <= 5) {
                this.rating = rating;
            }
        }
        
        public String getCatatanKhusus() { 
            return catatanKhusus; 
        }
        
        public int getRating() { 
            return rating; 
        }
        
        @Override
        public String toString() {
            return "Rating: " + "★".repeat(rating) + " ".repeat(5-rating) + 
                   "\nCatatan: " + catatanKhusus;
        }
    }
}
