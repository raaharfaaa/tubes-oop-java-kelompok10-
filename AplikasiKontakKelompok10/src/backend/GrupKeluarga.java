package backend;

public class GrupKeluarga extends GrupKontak {
public GrupKeluarga() {
        super("Keluarga");
    }
    
    @Override
    public String getKodeWarna() {
        return "#E24A4A";
    }
    
    @Override
    public String getIconName() {
        return "family";
    }
}
