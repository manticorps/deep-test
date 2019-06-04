package deep.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FindNode {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(FindNode.class);

    public static Node findNodeExist(final List<Node> nodes, final int nodeId) {
        return nodes.stream().filter(node -> node.getId() == nodeId).findFirst().get();
    }

}
