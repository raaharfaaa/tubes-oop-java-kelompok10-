package backend;

public abstract class GrupKontak {
    private String namaGrup;
    private String deskripsi;
    
    public GrupKontak(String namaGrup) {
        this.namaGrup = namaGrup;
        this.deskripsi = "";
    }
    
    public abstract String getKodeWarna();
    public abstract String getIconName();
    
    // Getter dan Setter
    public String getNamaGrup() { 
        return namaGrup; 
    }
    
    public void setNamaGrup(String namaGrup) { 
        this.namaGrup = namaGrup; 
    }
    
    public String getDeskripsi() { 
        return deskripsi; 
    }
    
    public void setDeskripsi(String deskripsi) { 
        this.deskripsi = deskripsi; 
    }
}