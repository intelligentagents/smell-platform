package br.ufal.sapiens.refactoring.classifier.sniffer.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public abstract class WekaClassifier extends Classifier {
	
	public WekaClassifier(String name, Smell smell) {
		super(name, smell);
	}

	private weka.classifiers.Classifier classifier;
	
	private List<String> metricNames;
	
	
	public weka.classifiers.Classifier getClassifier() {
		return this.classifier;
	}

	public void setClassifier(weka.classifiers.Classifier classifier) {
		this.classifier = classifier;
	}

	@Override
	public Boolean verify(Node node) {
		List<Node> nodes = new ArrayList<Node>(); // TODO: Necessario para classificar a instancia no J48
		nodes.add(node);
		Instances instances = WekaUtil.createWekaInstance(node);
		String result = "";
		try {
			double resultDouble = this.classifier.classifyInstance(instances.firstInstance());
			result = instances.classAttribute().value((int) resultDouble);
		} catch (Exception e) {
			return null;
		}
		if (result == "1") return Boolean.TRUE;
		else if (result == "0") return Boolean.FALSE;
		return null;
	}
	
	public boolean verify(NodeAnalysis node) {
		List<NodeAnalysis> nodes = new ArrayList<NodeAnalysis>(); // TODO: Necessario para classificar a instancia no J48
		nodes.add(node);
		Instances instances = WekaUtil.createWekaInstancesFromAnalysis(nodes);
		String result = "0";
		try {
			double resultDouble = this.classifier.classifyInstance(instances.instance(0));
			result = instances.classAttribute().value((int) resultDouble);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Boolean.parseBoolean(result.toString());
	}	

	@Override
	public List<String> getMetricNames() {
		return this.metricNames;
	}
	
	@Override
	public String toString() {
		return this.classifier.toString();
	}
}
