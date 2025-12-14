package backend;

public class KontakBisnis extends Kontak{
private String namaPerusahaan;
    private String jabatan;
    private String website;
    
    public KontakBisnis(String nama, String nomorTelepon, String namaPerusahaan) 
            throws InvalidPhoneNumberException {
        super(nama, nomorTelepon);
        this.namaPerusahaan = namaPerusahaan;
        this.jabatan = "";
        this.website = "";
    }
    
    @Override
    public boolean cocokDenganKriteria(String keyword) {
        boolean cocokParent = super.cocokDenganKriteria(keyword);
        String lowerKeyword = keyword.toLowerCase();
        
        return cocokParent ||
               namaPerusahaan.toLowerCase().contains(lowerKeyword) ||
               jabatan.toLowerCase().contains(lowerKeyword) ||
               website.toLowerCase().contains(lowerKeyword);
    }
    
    @Override
    public String toString() {
        return super.toString() + 
               String.format("\nPerusahaan: %s\nJabatan: %s\nWebsite: %s",
                           namaPerusahaan, jabatan, website);
    }
    
    // Getter dan Setter
    public String getNamaPerusahaan() { 
        return namaPerusahaan; 
    }
    
    public void setNamaPerusahaan(String namaPerusahaan) { 
        this.namaPerusahaan = namaPerusahaan; 
    }
    
    public String getJabatan() { 
        return jabatan; 
    }
    
    public void setJabatan(String jabatan) { 
        this.jabatan = jabatan; 
    }
    
    public String getWebsite() { 
        return website; 
    }
    
    public void setWebsite(String website) { 
        this.website = website; 
    }
}
