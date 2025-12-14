package backend;

public class GrupTeman extends GrupKontak {
    public GrupTeman() {
        super("Teman");
    }
    
    @Override
    public String getKodeWarna() {
        return "#50E3C2";
    }
    
    @Override
    public String getIconName() {
        return "friends";
    }

}
