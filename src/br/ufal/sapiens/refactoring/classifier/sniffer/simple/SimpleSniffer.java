package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.metrics.WekaUtil;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.Project;

public abstract class SimpleSniffer extends Sniffer {

	public SimpleSniffer(String name, Smell smell) {
		super(name,smell);
		this.setClassifier(this.getInitialRule());
	}
	
	public abstract Rule getInitialRule();
	
	
	public List<Node> filterNodesWithMetrics(Project project, Developer developer) {
		List<Node> nodes = new ArrayList<Node>(project.getNodes(this.getSmell().getType()).values());
		List<Node> filteredNodes = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.getMetricNames().containsAll(developer.getLastClassifier(this.getSmell()).getMetricNames())) {
				filteredNodes.add(node);
			}
		}
		return filteredNodes;
	}
	
	private List<Node> removeAnalyzedNodes(Developer developer, List<Node> nodes) {
		for (NodeAnalysis analysis : developer.getAnalysis().get(this.getSmell())) {
			nodes.remove(analysis.getNode());
		}
		return nodes;
	}
	
	public List<NeighbourNode> getNeighbourNodes(Project project, Developer developer, int maxNeighbours) {
		List<Node> nodes = this.filterNodesWithMetrics(project, developer);
		nodes = this.removeAnalyzedNodes(developer, nodes);
		
		try {
			return WekaUtil.getNeighbourNodes(nodes, (Rule)developer.getLastClassifier(this.getSmell()), maxNeighbours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NeighbourNode>();
	}
	
	public void updateClassifier(Developer developer) {
		List<NodeAnalysis> allAnalysis = developer.getAnalysis().get(this.getSmell());
		for (NodeAnalysis analysis : allAnalysis) {
			if (analysis.isVerify() != developer.getLastClassifier(this.getSmell()).verify(analysis.getNode())) {
				Rule rule = developer.getLastClassifier(this.getSmell()).update(analysis);
				if (ClassifierEvaluator.getEvaluation(rule, allAnalysis) > RuleEvaluator.getEvaluation(developer.getBestClassifier(this.getSmell()), allAnalysis)) {
					rule.setName("R"+developer.getClassifierMap().get(this.getSmell()).size());
					developer.getClassifierMap().get(this.getSmell()).add(rule);
				}	
			}
		}
	}

}
