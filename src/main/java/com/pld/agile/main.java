import com.pld.agile.model.Graph;
import com.pld.agile.model.Intersection;
import com.pld.agile.model.Section;


public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        model.loadXML("petitPlan.xml"); // Remplacez par le chemin de votre fichier XML
        model.displayGraph();
    }
}
