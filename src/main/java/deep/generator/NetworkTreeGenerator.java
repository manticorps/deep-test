package deep.generator;

import deep.entity.Node;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

public class NetworkTreeGenerator {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(NetworkTreeGenerator.class);

    private static final SecureRandom RANDOM = new SecureRandom();

    public static List<Node> createNodes(final int numberOfNodes) {

        List<Node> nodes = Lists.newArrayList();
        nodes.add(new Node(1));
        for (int i = 2; i <= numberOfNodes; i++) {
            addNode(i, nodes);
        }

        return nodes;
    }

    private static void addNode(final int id, final List<Node> nodes) {
        Node child = new Node(id);
        int selectedId = RANDOM.nextInt(Math.abs(nodes.size()));
        Node parent = nodes.get(selectedId);
        child.addParent(parent);
        parent.addChild(child);
        nodes.add(child);
    }
}