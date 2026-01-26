import controller.GraphController;
import model.Graph;
import model.GraphLoader;
import view.MainFrame;

public class Main {

    private static final String FILE = "resources/graph.txt";

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                Graph<String> graph = GraphLoader.load(FILE);
                GraphController controller = new GraphController(graph);
                new MainFrame(controller).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
