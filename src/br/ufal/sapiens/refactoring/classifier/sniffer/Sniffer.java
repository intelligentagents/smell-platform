package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.RuleEvaluator;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.metrics.WekaUtil;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Node;

public abstract class Sniffer {
	private String name;
	private Smell smell;
	
	public Sniffer(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
	}

	public List<SniffedSmell> findSmells(Project project, Developer developer, Smell smell) {
		List<SniffedSmell> sniffedSmells = new ArrayList<SniffedSmell>();
		for (Node node : project.getNodes(smell.getType()).values()) {
			if (this.verify(developer, node)) {
				sniffedSmells.add(new SniffedSmell(smell,
						this, node));
			}
		}

		return sniffedSmells;
	}
	
	public List<Node> filterNodesWithMetrics(Project project, Developer developer) {
		List<Node> nodes = new ArrayList<Node>(project.getNodes(smell.getType()).values());
		List<Node> filteredNodes = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.getMetricNames().containsAll(developer.getLastRule(this.smell).getMetricNames())) {
				filteredNodes.add(node);
			}
		}
		return filteredNodes;
	}
	
	private List<Node> removeAnalyzedNodes(Developer developer, List<Node> nodes) {
		for (NodeAnalysis analysis : developer.getAnalysis().get(this.smell)) {
			nodes.remove(analysis.getNode());
		}
		return nodes;
	}
	
	public List<NeighbourNode> getNeighbourNodes(Project project, Developer developer, int maxNeighbours) {
		List<Node> nodes = this.filterNodesWithMetrics(project, developer);
		nodes = this.removeAnalyzedNodes(developer, nodes);
		
		try {
			return WekaUtil.getNeighbourNodes(nodes, developer.getLastRule(smell), maxNeighbours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NeighbourNode>();
	}
	
	public void updateRule(Developer developer) {
		List<NodeAnalysis> allAnalysis = developer.getAnalysis().get(this.smell);
		for (NodeAnalysis analysis : allAnalysis) {
			if (analysis.isVerify() != developer.getLastRule(this.smell).verify(analysis.getNode())) {
				Rule rule = developer.getLastRule(this.smell).update(analysis);
				if (RuleEvaluator.getEvaluation(rule, allAnalysis) > RuleEvaluator.getEvaluation(developer.getBestRule(this.smell), allAnalysis)) {
					rule.setName("R"+developer.getRuleMap().get(this.smell).size());
					developer.getRuleMap().get(this.smell).add(rule);
				}	
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Smell getSmell() {
		return smell;
	}

	public void setSmell(Smell smell) {
		this.smell = smell;
	}

	public boolean verify(Developer developer, Node node) {
		return developer.getLastRule(this.smell).verify(node);
	}
	
	public boolean verify(Node node, Rule rule) {
		return rule.verify(node);
	}
	
	public abstract Rule getInitialRule();
	
	
}
