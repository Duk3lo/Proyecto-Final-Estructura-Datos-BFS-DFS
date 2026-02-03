import controller.GraphController;
import model.Graph;
import model.GraphLoader;
import util.ExecutionTimeStore;
import view.MainFrame;

public class Main {

    private static final String FILE = "input/graph.txt";

    public static void main(String[] args) {
        ExecutionTimeStore.init();

        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                Graph<String> graph = GraphLoader.load(FILE);
                GraphController controller = new GraphController(graph);
                new MainFrame(controller, FILE).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
