package moa.classifiers.meta;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.DoubleVector;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.options.ClassOption;
import moa.options.FlagOption;
import moa.options.FloatOption;
import moa.options.ListOption;
import moa.options.Option;
import moa.tasks.TaskMonitor;
import weka.core.Instance;
import weka.core.Utils;

public class PairwiseVoting extends AbstractClassifier
{
	@Override
	public String getPurposeString()
	{
		return "Pairwise voting algorithm for data streams.";
	}
	
	
}