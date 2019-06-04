package deep.generator;

import deep.entity.FindNode;
import deep.entity.Node;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

public class OutageSimulator {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(OutageSimulator.class);

    private static final SecureRandom RANDOM = new SecureRandom();
    private final List<Node> nodes;
    private final List<Integer> outages = Lists.newArrayList();
    private final List<OutageListener> listeners = Lists.newArrayList();

    public OutageSimulator(final List<Node> nodes) {
        this.nodes = nodes;
    }

    public void addListener(final OutageListener listener) {
        this.listeners.add(listener);
    }

    public int changeNetwork() {
//        boolean addOutage = RANDOM.nextBoolean();
//        if (addOutage) {


            return addNewOutage();
//        } else {
//            return resolveOutage();
//        }
    }

    private static <T> int getRandom(final List<T> list) {

        if (list.size() == 1) {
            return 0;
        }

        return RANDOM.nextInt(list.size() - 1);
    }

    private int resolveOutage() {
        if (outages.isEmpty()) {
            System.out.println("Resolve outage empty");
            return -1;
        }

        int indexToSolve = getRandom(outages);
        int remove = outages.remove(indexToSolve);

        System.out.println("Resolve outage [" + remove + "]");
        resolveOutageRecursive(FindNode.findNodeExist(nodes, remove));
        return remove;
    }

    private void resolveOutageRecursive(final Node node) {

        if (outages.contains(node.getId())) {
            return;
        }

        listeners.forEach(listener -> listener.onSolve(node.getId()));
        node.setStatus(true);
        node.getChilds().forEach(this::resolveOutageRecursive);
    }

    private int addNewOutage() {
        int indexOutage = RANDOM.nextInt(nodes.size());
        outages.add(indexOutage);

        Node node = nodes.get(indexOutage);
        System.out.println("New outage [" + node.getId() + "]");
        setOutageRecursive(node);
        return node.getId();
    }

    private void setOutageRecursive(final Node node) {
        node.setStatus(false);
        listeners.forEach(listener -> listener.onOutage(node.getId()));
//        node.getChilds().forEach(this::setOutageRecursive);
    }
}
