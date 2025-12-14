package backend;

public class KontakKeluarga extends Kontak {
private String hubunganKeluarga;
    private String tanggalLahir;
    private String catatanKeluarga;
    
    public KontakKeluarga(String nama, String nomorTelepon, String hubunganKeluarga) 
            throws InvalidPhoneNumberException {
        super(nama, nomorTelepon);
        this.hubunganKeluarga = hubunganKeluarga;
        this.tanggalLahir = "";
        this.catatanKeluarga = "";
    }
    
    @Override
    public boolean cocokDenganKriteria(String keyword) {
        boolean cocokParent = super.cocokDenganKriteria(keyword);
        String lowerKeyword = keyword.toLowerCase();
        
        return cocokParent ||
               hubunganKeluarga.toLowerCase().contains(lowerKeyword) ||
               catatanKeluarga.toLowerCase().contains(lowerKeyword);
    }
    
    @Override
    public String toString() {
        return super.toString() + 
               String.format("\nHubungan: %s\nTanggal Lahir: %s\nCatatan: %s",
                           hubunganKeluarga, tanggalLahir, catatanKeluarga);
    }
    
    // Getter dan Setter
    public String getHubunganKeluarga() { 
        return hubunganKeluarga; 
    }
    
    public void setHubunganKeluarga(String hubunganKeluarga) { 
        this.hubunganKeluarga = hubunganKeluarga; 
    }
    
    public String getTanggalLahir() { 
        return tanggalLahir; 
    }
    
    public void setTanggalLahir(String tanggalLahir) { 
        this.tanggalLahir = tanggalLahir; 
    }
    
    public String getCatatanKeluarga() { 
        return catatanKeluarga; 
    }
    
    public void setCatatanKeluarga(String catatanKeluarga) { 
        this.catatanKeluarga = catatanKeluarga; 
    }
}
