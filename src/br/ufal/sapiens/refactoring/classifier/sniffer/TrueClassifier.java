package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.pr.Node;

public class TrueClassifier extends Classifier {
	
	public TrueClassifier(String name, Smell smell) {
		super(name, smell);
	}

	@Override
	public boolean verify(Node node) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getMetricNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
