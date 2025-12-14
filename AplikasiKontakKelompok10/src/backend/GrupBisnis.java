package backend;

public class GrupBisnis extends GrupKontak {
 public GrupBisnis() {
        super("Bisnis");
    }
    
    @Override
    public String getKodeWarna() {
        return "#4A90E2";
    }
    
    @Override
    public String getIconName() {
        return "business";
    }
}