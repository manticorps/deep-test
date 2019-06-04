package deep.img;

import deep.entity.Node;
import com.google.common.collect.Maps;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.mutNode;

public class GraphPrinter {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(GraphPrinter.class);

    private MutableNode rootNode;

    private final String targetImg;
    private final Map<Integer, MutableNode> nodeMap = Maps.newHashMap();
    private final List<Node> nodes;

    public GraphPrinter(final String targetImg, final List<Node> nodes) {
        this.targetImg = targetImg;
        this.nodes = nodes;
    }

    public void draw() {
        nodeMap.clear();
        nodes.forEach(this::addNode);

        MutableGraph graph = Factory.mutGraph("example1")
            .setDirected(true);

        for (MutableNode value : nodeMap.values()) {
            graph.add(value);
        }

        try {
            Graphviz
                .fromGraph(graph)
                .render(Format.SVG)
                .toFile(new File(targetImg));

            // TO json.
//            Graphviz viz = Graphviz.fromGraph(graph);
//            String json = viz.engine(Engine.NEATO).render(Format.JSON).toString();
//            System.out.println(json);
        } catch (Throwable t) {
//            Graphviz viz = Graphviz.fromGraph(graph);
//            String json = viz.engine(Engine.NEATO).render(Format.JSON).toString();
//            System.out.println(json);
        }

    }

    private void addNode(final Node node) {

        Color color = node.isUp() ? Color.BLACK : Color.RED;

        if (rootNode == null) {
            this.rootNode = mutNode("" + node.getId()).add(color);
            this.nodeMap.put(node.getId(), rootNode);

        } else {
            MutableNode childNode = createChildNode(node).add(color);
            this.nodeMap.put(node.getId(), childNode);
        }
    }

    private MutableNode createChildNode(final Node child) {
        MutableNode childNode = mutNode("" + child.getId());
        List<Node> linkedNodes = child.getParents();

        linkedNodes.forEach(parent -> linkToChild(childNode, parent));
        return childNode;
    }

    private void linkToChild(final MutableNode childNode, final Node parentNode) {
        MutableNode parentMutable = nodeMap.get(parentNode.getId());
        childNode.addLink(parentMutable);
    }
}
