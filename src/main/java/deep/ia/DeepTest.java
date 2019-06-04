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
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.Sgd;
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

        SplitTestAndTrain testAndTrain = dataSets.splitTestAndTrain(0.5);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        normalize(trainingData, testData);
        
//        int hiddenNodes = MODEL_SIZE / (5 * (NODES + NODES));
        int hiddenNodes = 5;
        System.out.println("Usage of [" + hiddenNodes + "] hiddens nodes");

        int layer = 0;
        double learningRate = 0.001;

        MultiLayerConfiguration configuration
            = new NeuralNetConfiguration.Builder()
            .seed(123)
            .activation(Activation.TANH)
            .weightInit(WeightInit.ZERO)
            .updater(new Sgd(0.1))
            .l2(1e-4)
            .updater(new AdaGrad(learningRate, 0.9))
            .list()
            .layer(layer++, new DenseLayer.Builder()
                .nIn(NODES)
                .nOut(hiddenNodes)
                .build())

            .layer(layer++, new DenseLayer
                .Builder()
                .nIn(hiddenNodes)
                .nOut(NODES)
                .build())

            .layer(layer++, new OutputLayer.Builder(
                LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .activation(Activation.SOFTMAX)
                .nIn(NODES)
                .nOut(NODES)
                .build())
            .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        addUiServer(model);
        model.init();

        for (int i = 0; i < 50; i++) {
            System.out.println("Fit in progress -> iteration [" + i + "]");
            model.fit(trainingData);
        }

        evaluate(model, testData);
    }

    private void evaluate(final MultiLayerNetwork model, final DataSet testData) {

        Evaluation eval = new Evaluation(NODES);
        INDArray output = model.output(testData.getFeatures());
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());
    }

    private void normalize(final DataSet trainingData, final DataSet testData) {
        DataNormalization normalizer = new NormalizerMinMaxScaler();
        normalizer.fit(trainingData);
        normalizer.transform(trainingData);
        normalizer.transform(testData);
    }

    private void addUiServer(final MultiLayerNetwork model) {
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        model.setListeners(new ScoreIterationListener(50), new StatsListener(statsStorage, 1));
    }

    private DataSet loadDataset() throws IOException, InterruptedException {
        try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
            recordReader.initialize(new FileSplit(new File(MODEL_DATASET)));
            DataSetIterator iterator = new RecordReaderDataSetIterator(
                recordReader,
                MODEL_SIZE,
                NODES,
                NODES
            );

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
