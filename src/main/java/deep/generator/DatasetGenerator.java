package deep.generator;

import deep.Constants;
import deep.entity.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DatasetGenerator {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(DatasetGenerator.class);

    private FileWriter fileOutputStream;

    public void start() throws IOException {
        fileOutputStream = new FileWriter(Constants.MODEL_DATASET);
    }

    public void writeStatus(final List<Node> nodes, final int problem) throws IOException {

        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        boolean hasOutage = nodes.stream().anyMatch(node -> !node.isUp());

        for (Node node : nodes) {
            if (firstLine) {
                firstLine = false;
            } else {
                sb.append(",");
            }

            String value = node.isUp() ? "0" : "1";
            sb.append(value);
        }

        sb.append(",");

        if (hasOutage) {
            sb.append(problem);
        } else {
            sb.append("0");
        }

        fileOutputStream.write(sb.toString());
        fileOutputStream.write("\n");
    }

    public void save() throws IOException {
        fileOutputStream.close();
    }
}