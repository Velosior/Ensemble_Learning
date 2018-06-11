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

	// PP variables
	protected List<Object> patternArray;
	// 1st list = column (label), 2nd list = row (pattern).
	protected List<List<Object>> matrix;
	protected int oldestClassifier;

	@Override
	public void resetLearningImpl()
	{
		// Initialize shared variables.
		this.ensemble = new ArrayList<>();
		this.instanceCounter = 0;

		this.oldestClassifier = 1;

		if (this.votingAlgorithmOption.getChosenIndex() == 0) {
			// Initialize PA variables.
		}

		else {
			this.patternArray = new ArrayList<>();
			this.matrix = new ArrayList<>();
		}
	}

	@Override
	public void trainOnInstanceImpl(Instance inst)
	{
		if (this.votingAlgorithmOption.getChosenIndex() == 0) {
			// PA stuff...
		}

		else {
			
			if (instanceCounter == 0) {
				// Background classifier.
				this.ensemble.add((Classifier) getPreparedClassOption(this.baseLearnerOption));
				// Active classifier.
				this.ensemble.add((Classifier) getPreparedClassOption(this.baseLearnerOption));
			}

			else {

				if (instanceCounter % 1000 == 0) {

					// If user defined ensemble size has been reach, classifiers are only replaced with new classifiers.
					if (ensemble.size() >= ensembleSizeOption.getValue() + 1) {
						ensemble.set(oldestClassifier, (Classifier) getPreparedClassOption(this.baseLearnerOption));
						ensemble.set(0, (Classifier) getPreparedClassOption(this.baseLearnerOption));

						if (oldestClassifier >= ensembleSizeOption.getValue()) {
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
			}

			// Both predictions are combined into a pattern to calculate the row.
			ensemble.get(0).trainOnInstance(inst);
			ensemble.get(ensemble.size() - 1).trainOnInstance(inst);

			// The correct label determines the column.
			// Finally finding the correct position in the matrix to increment.

			/*
			The overall ensemble prediction is the label
			that receives more votes based on the observed
			patterns from all pairs of classifiers.
			*/

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

		// Grab votes based on voting selection.

		return combinedVote.getArrayCopy();
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}
}