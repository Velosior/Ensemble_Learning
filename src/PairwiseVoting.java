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

	public ClassOption activeClassifierOption = new ClassOption("activeClassifier", 'l', "Classifier to train.", Classifier.class, "trees.HoeffdingTree");
	public ClassOption backgroundClassifierOption = new ClassOption("backgroundClassifier", 'l', "Classifier to train.", Classifier.class, "trees.HoeffdingTree");

	public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's', "The number of models to use in voting.", 10, 1, Integer.MAX_VALUE);
	
	public MultiChoiceOption votingAlgorithmOption = new MultiChoiceOption(
		"votingAlgorithm", 'm', "Voting strategy to use.", new String[]{
			"PairwiseAccuracy", "PairwisePatterns"},
		new String[]{"Weights predictions of pairs based on their shared estimated accuracy.",
			"Uses recorded prediction patterns to weight decisions while predicting an unknown instance."
			}, 0);

	public IntOption windowSizeOption = new IntOption("windowSize", 'w', "The window size.", 100, 1, Integer.MAX_VALUE);

	// Shared variables
	protected Classifier[] ensemble;

	// PA variables

	// PP variables
	protected List<Object> patternArray;
	// 1st list = row, 2nd list = column.
	protected List<List<Object>> matrix;

	@Override
	public void resetLearningImpl()
	{
		// Initialize shared variables.
		this.ensemble = new Classifier[2];

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

			this.ensemble[0] = (Classifier) getPreparedClassOption(this.activeClassifierOption);
			this.ensemble[1] = (Classifier) getPreparedClassOption(this.backgroundClassifierOption);

			// LOOP for window size.
			for (int i = 0; i < this.windowSizeOption.getValue(); i++) {

				// Selection of classifiers.
				if (i != 0) {
					// If user defined ensemble size has been reach, classifiers are only replaced with new classifiers.
					if (i / 2 > this.ensembleSizeOption.getValue()) {
						// Both new classifiers.
						this.ensemble[0] = null;
						this.ensemble[1] = null;
					}

					// Otherwise the background becomes the active and a new background is chosen.
					else {
						this.ensemble[0] = this.ensemble[1];
						this.ensemble[1] = null;
					}
				}

				// Now do something with them ROFL!

			}
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