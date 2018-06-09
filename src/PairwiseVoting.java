import com.github.javacliparser.IntOption;
import com.github.javacliparser.MultiChoiceOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.DoubleVector;
import moa.core.Measurement;
import moa.options.ClassOption;

public class PairwiseVoting extends AbstractClassifier
{
	@Override
	public String getPurposeString()
	{
		return "Pairwise voting algorithm for data streams.";
	}

	public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'l', "Classifier to train.", Classifier.class, "trees.HoeffdingTree");

	public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's', "The number of models to use in voting.", 10, 1, Integer.MAX_VALUE);
	
	public MultiChoiceOption votingAlgorithmOption = new MultiChoiceOption(
		"votingAlgorithm", 'm', "Voting strategy to use.", new String[]{
			"PairwiseAccuracy", "PairwisePatterns"},
		new String[]{"Weights predictions of pairs based on their shared estimated accuracy.",
			"Uses recorded prediction patterns to weight decisions while predicting an unknown instance."
			}, 0);
	
	protected Classifier[] ensemble;
	
	@Override
	public void resetLearningImpl()
	{
		
	}

	@Override
	public void trainOnInstanceImpl(Instance inst)
	{
		
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		return new Measurement[0];
	}

	@Override
	public void getModelDescription(StringBuilder stringBuilder, int i) {

	}

	@Override
	public double[] getVotesForInstance(Instance inst)
	{
		DoubleVector combinedVote = new DoubleVector();
		return combinedVote.getArrayRef();
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}
}