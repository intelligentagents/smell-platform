package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public abstract class SimpleSniffer extends Sniffer {

	public SimpleSniffer(String name, Smell smell) {
		super(name,smell);
		Rule initialRule = this.getInitialRule();
		this.getClassifiers().add(initialRule);
		this.setBestClassifier(initialRule);
	}
	
	public abstract Rule getInitialRule();
	
	public List<Node> filterNodesWithMetrics(Project project) {
		List<Node> nodes = new ArrayList<Node>(project.getNodes(this.getSmell().getType()).values());
		List<Node> filteredNodes = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.getMetricNames().containsAll(this.getBestClassifier().getMetricNames())) {
				filteredNodes.add(node);
			}
		}
		return filteredNodes;
	}
	
	private List<Node> removeAnalyzedNodes(List<Node> nodes) {
		for (NodeAnalysis analysis : this.getAnalysis()) {
			nodes.remove(analysis.getNode());
		}
		return nodes;
	}
	
	public List<NeighbourNode> getNeighbourNodes(Project project, Developer developer, int maxNeighbours) {
		List<Node> nodes = this.filterNodesWithMetrics(project);
		nodes = this.removeAnalyzedNodes(nodes);
		
		try {
			return WekaUtil.getNeighbourNodes(nodes, (Rule)this.getBestClassifier(), maxNeighbours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NeighbourNode>();
	}
	
	public void updateClassifier() {
		List<NodeAnalysis> allAnalysis = this.getAnalysis();
		for (NodeAnalysis analysis : allAnalysis) {
			if (analysis.isVerify() != this.getBestClassifier().verify(analysis.getNode())) {
				Rule rule = ((Rule)this.getBestClassifier()).update(analysis);
				if (ClassifierEvaluator.getEvaluation(rule, allAnalysis) > RuleEvaluator.getEvaluation(this.getBestClassifier(), allAnalysis)) {
					rule.setName("R" + this.getClassifiers().size());
					this.getClassifiers().add(rule);
				}	
			}
		}
	}
	
	public static SimpleSniffer fromSmell(Smell smell) {
		if (smell.equals(Smell.LongParameterList()))
			return new LongParameterListSniffer();
		else if (smell.equals(Smell.LongMethod()))
			return new LongMethodSniffer();
		else if (smell.equals(Smell.GodClass()))
			return new GodClassSniffer();
		else if (smell.equals(Smell.FeatureEnvy()))
			return new FeatureEnvySniffer();
		System.out.println("deu merda");
		return null;
	}

}
