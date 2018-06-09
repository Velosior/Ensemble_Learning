/**
 * 09/06/2018
 * -------------------------------------
 * Example code based of MOA tutorial 2 (https://moa.cms.waikato.ac.nz/tutorial-2-introduction-to-the-api-of-moa/).
 */

import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.classifiers.meta.OzaBoost;
import moa.core.TimingUtils;
import moa.streams.generators.RandomTreeGenerator;

public class Main {

    public static void main(String[] args) {
        runExperiment(10000, true);
    }

    private static void runExperiment(int numInstances, boolean isTesting) {

        //Classifier classifierLearner = new OzaBoost();
        Classifier classifierLearner = new PairwiseVoting();

        RandomTreeGenerator randomTreeGeneratorStream = new RandomTreeGenerator();
        randomTreeGeneratorStream.prepareForUse();

        classifierLearner.setModelContext(randomTreeGeneratorStream.getHeader());
        classifierLearner.prepareForUse();

        int numberSamplesCorrect = 0;
        int numberSamples = 0;
        long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

        while (randomTreeGeneratorStream.hasMoreInstances() && numberSamples < numInstances) {

            Instance instanceTrain = randomTreeGeneratorStream.nextInstance().getData();

            if (isTesting) {
                if (classifierLearner.correctlyClassifies(instanceTrain)) {
                    numberSamplesCorrect++;
                }
            }

            numberSamples++;
            classifierLearner.trainOnInstance(instanceTrain);

            double accuracy = 100.0 * (double) numberSamplesCorrect/ (double) numberSamples;
            double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()- evaluateStartTime);

            System.out.println(numberSamples + " instances processed with " + accuracy + "% accuracy in "+ time +" seconds.");
        }
    }
}
