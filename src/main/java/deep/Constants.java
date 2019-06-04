package deep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(Constants.class);

    public static final String WORKSPACE = "/home/manticore/workspace/tuto/deeptest/dev-env/";
    public static final String MODEL_DATASET = WORKSPACE + "/dataset.csv";
    public static final String REAL_STATUS_IMAGE = WORKSPACE + "/img/status-real.svg";
    public static final String IA_STATUS_IMAGE = WORKSPACE + "/img/status-ia.svg";
    
    public static final int MODEL_SIZE = 10000;
    public static final int NODES = 20;
    public static final int CLASS = NODES + 1;
}