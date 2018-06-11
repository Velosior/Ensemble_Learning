//package moa.classifiers.meta;

import com.github.javacliparser.IntOption;
import com.github.javacliparser.MultiChoiceOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.classifiers.MultiClassClassifier;
import moa.core.DoubleVector;
import moa.core.Measurement;
import moa.options.ClassOption;

import java.util.ArrayList;
import java.util.List;

public class PairwiseVoting extends AbstractClassifier implements MultiClassClassifier
{
	@Override
	public String getPurposeString()
	{
		return "Pairwise voting algorithm for data streams.";
	}

	public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'a', "Classifier to train.", Classifier.class, "meta.OzaBoost");

	public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's', "The number of models to use in voting.", 10, 1, Integer.MAX_VALUE);
	
	public MultiChoiceOption votingAlgorithmOption = new MultiChoiceOption(
		"votingAlgorithm", 'm', "Voting strategy to use.", new String[]{
			"PairwiseAccuracy", "PairwisePatterns"},
		new String[]{"Weights predictions of pairs based on their shared estimated accuracy.",
			"Uses recorded prediction patterns to weight decisions while predicting an unknown instance."
			}, 0);

	// Shared variables
	// ensemble[0] contains the backgroundClassifier
	protected List<Classifier> ensemble;
	protected int instanceCounter;

	// PA variables
	// Pair of classifiers
	public class Pair {
		private final Classifier c1;
		private final Classifier c2;

		public Pair(Classifier c1, Classifier c2) {
			this.c1 = c1;
			this.c2 = c2;
		}
	}

	public class WeightVector{
		private final String label;
		private final double weight;

		public WeightVector(String label) {
			this.label = label;
			this.weight = 0.0;
		}
	}

	//List of classifier pairs
	protected List<Pair> classifierPairs;
	protected List<WeightVector> labelWeights;

	// PP variables
	protected List<Object> patternArray;
	// 1st list = column (label), 2nd list = row (pattern).
	protected List<List<Object>> matrix;
	protected int oldestClassifier;
	protected int newestClassifier;

	@Override
	public void resetLearningImpl()
	{
		// Initialize shared variables.
		this.ensemble = new ArrayList<>();
		this.instanceCounter = 0;

		this.oldestClassifier = 1;
		this.newestClassifier = 1;

		if (this.votingAlgorithmOption.getChosenIndex() == 0) {
			// Initialize PA variables.
			this.classifierPairs = new ArrayList<>();
			this.labelWeights = new ArrayList<>();

			//Fill up ensemble
			Classifier baseLearner = (Classifier) getPreparedClassOption(this.baseLearnerOption);
			baseLearner.resetLearning();
			for (int i = 0; i < this.ensembleSizeOption.getValue(); i++) {
				this.ensemble.add(baseLearner.copy());
			}
		}

		else {
			this.patternArray = new ArrayList<>();
			this.matrix = new ArrayList<>();
		}
	}

	@Override
	public void trainOnInstanceImpl(Instance inst)
	{
		// If PA is selected
		if (this.votingAlgorithmOption.getChosenIndex() == 0) {

			//For each pair of classifiers
			for (int i = 0; i < this.ensembleSizeOption.getValue(); i++)
			{
				for (int j = 0; j < this.ensembleSizeOption.getValue(); j++)
				{
					//First check that they are not the same classifier
					if(this.ensemble.get(i).equals(this.ensemble.get(j)))
					{
						//Calculate estimated shared accuracy and estimated error rate
						//This would be done using the window size (unsure where to get window size)

						//Check if they both correctly or incorrectly classify the instance
						if((this.ensemble.get(i).correctlyClassifies(inst) && this.ensemble.get(j).correctlyClassifies(inst)) || (!this.ensemble.get(i).correctlyClassifies(inst) && !this.ensemble.get(j).correctlyClassifies(inst)))
						{

							//Get the prediction of the instance and update the vectors using the estimated accuracy and error rate
							//This would be done by getting the prediction label to update that label's position in the WeightVector (unsure where to access the predicted label)

						}
						//Otherwise the predictions diverge
						else
						{
							//Get the prediction of each of the classifiers and update the vectors using the estimated accuracy and error rate
							//This would be done by getting the prediction label to update that label's position in the WeightVector (unsure where to access the predicted label)
						}
					}
				}
			}
		}

		// Otherwise PP
		else {

			if (instanceCounter == 0) {
				// Background classifier.
				this.ensemble.add((Classifier) getPreparedClassOption(this.baseLearnerOption));
				// Active classifier.
				this.ensemble.add((Classifier) getPreparedClassOption(this.baseLearnerOption));
			}

			if (instanceCounter % 1000 == 0) {

				// If user defined ensemble size has been reach, classifiers are only replaced with new classifiers.
				if (ensemble.size() == ensembleSizeOption.getValue() + 1) {

					ensemble.set(oldestClassifier, (Classifier) getPreparedClassOption(this.baseLearnerOption));

					newestClassifier = oldestClassifier;

					if (oldestClassifier == ensemble.size() - 1) {
						oldestClassifier = 1;
					} else {
						oldestClassifier++;
					}
				}

				// Otherwise the background becomes the active and a new background is chosen.
				else {
					ensemble.add(ensemble.get(0));
					ensemble.set(0, (Classifier) getPreparedClassOption(this.baseLearnerOption));
				}
			}

			if (ensemble.size() == ensembleSizeOption.getValue() + 1) {
				ensemble.get(newestClassifier).trainOnInstance(inst);
			}

			else {
				ensemble.get(0).trainOnInstance(inst);
				ensemble.get(ensemble.size() - 1).trainOnInstance(inst);
			}

			// Both predictions are combined into a pattern to calculate the row.
			// All patterns are then added to patternArray.

			// The correct label then determines the column.

			// Finally finding the correct position in matrix to increment / add.
			// A matrix is unique to a classifier and is reset appropriately.

			instanceCounter++;
		}
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		return new Measurement[0];
	}

	@Override
	public void getModelDescription(StringBuilder stringBuilder, int i) {  }

	@Override
	public double[] getVotesForInstance(Instance inst)
	{
		DoubleVector combinedVote = new DoubleVector();

		// If PA is selected
		if (this.votingAlgorithmOption.getChosenIndex() == 0) {

		}

		// Otherwise PP
		else {
			/*
			The overall ensemble prediction for PP is the label
			that receives more votes based on the observed
			patterns from all pairs of classifiers.
			*/
		}

		return combinedVote.getArrayCopy();
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}
}