package br.ufal.sapiens.refactoring.classifier.sniffer.dt;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public class J48Classifier extends Classifier {
	
	private J48 j48;
	
	private List<String> metricNames;
	
	public J48Classifier(String name, Smell smell, List<String> metricNames) {
		super(name, smell);
		this.j48 = new J48();
		this.metricNames = metricNames;
	}
	
	public J48 getJ48() {
		return j48;
	}

	public void setJ48(J48 j48) {
		this.j48 = j48;
	}

	@Override
	public boolean verify(Node node) {
		List<Node> nodes = new ArrayList<Node>(); // TODO: Necessario para classificar a instancia no J48
		nodes.add(node);
		Instances instances = WekaUtil.createWekaInstances(nodes, this.getMetricNames());
		String result = "0";
		try {
			double resultDouble = this.j48.classifyInstance(instances.instance(0));
			result = instances.classAttribute().value((int) resultDouble);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Boolean.parseBoolean(result.toString());
	}

	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		Instances instances = WekaUtil.createWekaInstancesFromAnalysis(analysis, metricNames); //TODO; adicionar a class
		J48 j48 = new J48();
		try {
			j48.buildClassifier(instances);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		J48Classifier newClassifier = new J48Classifier(this.getName(), this.getSmell(), this.metricNames);
		newClassifier.setJ48(j48);
		return newClassifier;
	}

	
	@Override
	public List<String> getMetricNames() {
		return this.metricNames;
	}
	
	@Override
	public String toString() {
		return this.j48.toString();
	}

}
