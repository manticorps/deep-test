package deep;

import deep.entity.FindNode;
import deep.entity.Node;
import deep.generator.DatasetGenerator;
import deep.generator.NetworkTreeGenerator;
import deep.generator.OutageSimulator;
import deep.ia.DeepTest;
import deep.img.GraphPrinter;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Main {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws Exception {
        System.out.println("Creating system");
        List<Node> nodes = NetworkTreeGenerator.createNodes(Constants.NODES);

        DatasetGenerator datasetGenerator = new DatasetGenerator();
        datasetGenerator.start();

        System.out.println("Creating dataset with [" + Constants.MODEL_SIZE + "] iterations");

        for (int i = 0; i < Constants.MODEL_SIZE; i++) {
            List<Node> copyNodes = copyNode(nodes);

            OutageSimulator outageSimulator = new OutageSimulator(copyNodes);
            int changedNode = outageSimulator.changeNetwork();
            if (changedNode == -1) {
                continue;
            }

            datasetGenerator.writeStatus(copyNodes, changedNode - 1);
        }

        datasetGenerator.save();

        System.out.println("Initialize UI");


//
//        GraphPrinter realStatus = new GraphPrinter(Constants.REAL_STATUS_IMAGE, nodes);
//        refreshGraph(realStatus);
////
//        GraphPrinter iaStatus = new GraphPrinter(Constants.IA_STATUS_IMAGE, copyNodes);
//        refreshGraph(iaStatus);
//
//
//
//        new DeepWebServer();


        System.out.println("Create model");
        DeepTest deepTest = new DeepTest();
        deepTest.createModel();
//
//        LockSupport.parkNanos(TimeUnit.HOURS.toNanos(1));
    }

    private static void refreshGraph(final GraphPrinter graphPrinter) {
        ScheduledExecutorService graphDraw = Executors.newSingleThreadScheduledExecutor();
        graphDraw.scheduleAtFixedRate(graphPrinter::draw, 0, 5, TimeUnit.SECONDS);
    }

    private static void scheduleOutage(final OutageSimulator outageSimulator) {
        ScheduledExecutorService graphDraw = Executors.newSingleThreadScheduledExecutor();
        graphDraw.scheduleAtFixedRate(outageSimulator::changeNetwork, 0, 5, TimeUnit.SECONDS);
    }

    private static List<Node> copyNode(final List<Node> inputs) {

        List<Node> copyNodes = Lists.newArrayList();

        for (Node input : inputs) {
            Node copy = new Node(input.getId());
            copyNodes.add(copy);

            for (Node inputParent : input.getParents()) {
                Node copyParent = FindNode.findNodeExist(copyNodes, inputParent.getId());
                copyParent.addChild(copy);
                copy.addParent(copyParent);
            }
        }

        return copyNodes;
    }
}