package deep.ia;

import deep.generator.OutageListener;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static deep.Constants.*;

public class DeepTest implements OutageListener {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(DeepTest.class);

    public void createModel() throws IOException, InterruptedException {

        DataSet dataSets = loadDataset();


        DataNormalization normalizer = new NormalizerStandardize();
        SplitTestAndTrain testAndTrain = dataSets.splitTestAndTrain(0.5);

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        normalizer.fit(trainingData);
        normalizer.transform(trainingData);


//        int hiddenNodes = MODEL_SIZE / (5 * (NODES + NODES));
        int hiddenNodes = 5;
        System.out.println("Use [" + hiddenNodes + "] hidden nodes");

        int layer = 0;
        MultiLayerConfiguration configuration
            = new NeuralNetConfiguration.Builder()
            .seed(123)
            .weightInit(WeightInit.ZERO)
            .list()
            .layer(layer++, new DenseLayer.Builder()
                .nIn(NODES)
                .nOut(hiddenNodes)
                .build())

            .layer(layer++, new DenseLayer
                .Builder()
//                .activation(Activation.RELU)
                .nIn(hiddenNodes)
                .nOut(NODES)
                .build())

            .layer(layer++, new OutputLayer.Builder(
                LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nIn(NODES)
                .nOut(NODES)
                .build())

            .build();

//        configuration.setIterationCount(10000);
//        configuration.setBackpropType(BackpropType.Standard);

        MultiLayerNetwork network = new MultiLayerNetwork(configuration);

        System.setProperty("org.deeplearning4j.ui.port", Integer.toString(9000));
//        UIServer uiServer = UIServer.getInstance();
//        StatsStorage statsStorage = new InMemoryStatsStorage();
//        uiServer.attach(statsStorage);
//        network.setListeners(new ScoreIterationListener(5), new StatsListener(statsStorage, 1));
        network.init();

        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
        uiServer.attach(statsStorage);

        network.fit(trainingData);

        network.setListeners(new StatsListener( statsStorage), new ScoreIterationListener(1));


        Evaluation eval = new Evaluation(NODES);
        INDArray output = network.output(testData.getFeatures());
        eval.eval(testData.getLabels(), output);

        System.out.println(eval.stats());

    }

    private DataSet loadDataset() throws IOException, InterruptedException {
        try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
            recordReader.initialize(new FileSplit(new File(MODEL_DATASET)));
            DataSetIterator iterator = new RecordReaderDataSetIterator(
                recordReader,
                MODEL_SIZE,
                NODES,
                NODES);

            return iterator.next();
        }
    }

    @Override
    public void onOutage(final int node) {

    }

    @Override
    public void onSolve(final int outage) {

    }
}
