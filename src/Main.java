import controller.GraphController;
import model.Graph;
import model.GraphLoader;
import view.MainFrame;

public class Main {

    public static void main(String[] args) throws Exception {
        Graph g = GraphLoader.load("resources/graph.txt");
        GraphController controller = new GraphController(g);

        javax.swing.SwingUtilities.invokeLater(() ->
            new MainFrame(controller).setVisible(true)
        );
    }
}
