package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.util.SimpleLogger;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public abstract class SimpleHeuristicSniffer extends Sniffer {
	
	private int direction = 0;
	private Map<Integer,List<Rule>> ruleMap;
	private Map<Integer,Rule> initialRules;
	
	public SimpleHeuristicSniffer(String name, Smell smell) {
		super(name,smell);
		this.ruleMap = new HashMap<Integer,List<Rule>>();
		this.initialRules = new HashMap<Integer,Rule>();
//		this.loadRules();
	}
	
	public abstract void loadRules();
	
	public Map<Integer,Rule> getInitialRules() {
		return this.initialRules;
	}
	
	public void loadOneRule(int heuristicID) {
		Rule rule = this.initialRules.get(heuristicID);
		this.ruleMap = new HashMap<Integer,List<Rule>>();
		this.setClassifier(new ArrayList<Classifier>());
		this.setAnalysis(new ArrayList<NodeAnalysis>());
		this.bestClassifier = null;
		this.addRule(rule, heuristicID);
		this.setBestClassifier(rule);
	}
	
	public void initialize() {
		this.initialRules = new HashMap<Integer,Rule>();
		this.ruleMap = new HashMap<Integer,List<Rule>>();
		this.setClassifier(new ArrayList<Classifier>());
		this.setAnalysis(new ArrayList<NodeAnalysis>());
		this.bestClassifier = null;
		this.loadRules();
	}
	
	public void clearRules() {
		this.initialRules = new HashMap<Integer,Rule>();
		this.ruleMap = new HashMap<Integer,List<Rule>>();
		this.setClassifier(new ArrayList<Classifier>());
		this.setAnalysis(new ArrayList<NodeAnalysis>());
		this.bestClassifier = null;
	}
	
	public int getHeuristicCount() {
		return this.ruleMap.size();
	}
	
//	public void createRules() {
//		this.setClassifier(new ArrayList<Classifier>());
//		for (int i = 1; i <= this.getHeuristicCount(); i++) {
//			this.getClassifiers().add(this.getInitialRule(i));
//		}
//		this.setBestClassifier(this.getInitialRule(1));
//	}
	
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
	
	private void changeDirection(boolean verify) {
		this.direction = verify ? -1 : 1;
	}
	
	private Rule getBestRuleFromList(List<Rule> cList) {
		Float bestValue = -1f;
		Rule bestClassifier = null;
		for (Rule classifier : cList) {
			Float newValue = ClassifierEvaluator.getEvaluation(classifier, this.getAnalysis());
			if (newValue.isNaN()) newValue = 0f;
			if (newValue > bestValue) {
				bestClassifier = classifier;
				bestValue = newValue;
			}	
		}
		return bestClassifier;
	}
	
	public void addAnalysis(NodeAnalysis analysis) {
		this.getAnalysis().add(analysis);
		for (Integer i : this.ruleMap.keySet()) {
			List<Rule> classifierList = this.ruleMap.get(i);
			Rule bestClassifier = this.getBestRuleFromList(classifierList);
			if (analysis.isVerify() != bestClassifier.verify(analysis.getNode())) {
				Rule rule = ((Rule)bestClassifier).update(analysis);
				rule.setIterations(this.getAnalysis().size());
				rule.setName("#" + this.getAnalysis().size());
				classifierList.add(rule);
				this.addRule(rule, i);
			} else {
				this.changeDirection(analysis.isVerify());
			}
		}
		this.updateClassifier();
	}
	
//	public void addAnalysis(NodeAnalysis analysis) {
//		SimpleLogger.log("- Adicionando Analises: ");
//		this.getAnalysis().add(analysis);
//		if (analysis.isVerify() != this.getBestClassifier().verify(analysis.getNode())) {
//			SimpleLogger.log("-- Diferenca Encontrada: ");
//			Rule rule = ((Rule)this.getBestClassifier()).update(analysis);
//			rule.setIterations(this.getAnalysis().size());
//			rule.setName("#" + this.getAnalysis().size());
//			this.getClassifiers().add(rule);
//			SimpleLogger.log("---  Adicionando Regra: " + rule);
//		} else {
//			this.changeDirection(analysis.isVerify());
//		}
//		this.updateClassifier();
//	}
	
//	public void updateClassifier() {
//		if (this.getClassifiers() == null || this.getClassifiers().size() == 0) {
//			return;
//		}
//		
//		Classifier newBestClassifier = this.getClassifiers().get(0);
//		Float bestValue = ClassifierEvaluator.getEvaluation(newBestClassifier, this.getAnalysis());
//		if (bestValue.isNaN()) bestValue = 0f;
//				
//		for (Classifier classifier : this.getClassifiers()) {
//			Float evaluation = ClassifierEvaluator.getEvaluation(classifier, this.getAnalysis());
//			if (evaluation.isNaN()) evaluation = 0f;
//			if (evaluation > bestValue) {
//				newBestClassifier = classifier;
//				bestValue = evaluation;
//			}	
//		}
//		
//		if (!newBestClassifier.equals(this.getBestClassifier())) {
//			this.setBestClassifier(newBestClassifier);
//			SimpleLogger.log("-------   Melhor Regra: " + this.getBestClassifier());
//
//		}
//	}
	
	public void updateClassifier() {
		Rule oldBestClassifier = (Rule)super.getBestClassifier();
		Rule newBestClassifier = null;
		List<Rule> bestClassifiers = new ArrayList<Rule>();
		
		for (Integer key : this.ruleMap.keySet()) {
			bestClassifiers.add(this.getBestRuleFromList(this.ruleMap.get(key)));
		}
//		if (this.getAnalysis().size() < this.getHeuristicCount()) {
//			int count = this.getAnalysis().size() % this.getHeuristicCount();
//			this.setBestClassifier(bestClassifiers.get(count));
//		} else {
			newBestClassifier = this.getBestRuleFromList(bestClassifiers);
			if ((oldBestClassifier == null) || (!newBestClassifier.equals(oldBestClassifier))) {
				newBestClassifier.setIterations(this.getAnalysis().size());
				this.setBestClassifier(newBestClassifier);
			}
			
//		}
	}
	
	public List<Node> getNeighbourNodes(List<Node> nodes, int kNeighbours) {
		List<Node> result = new ArrayList<Node>();
		try {
			List<NeighbourNode> nns = WekaUtil.getNeighbourNodes(nodes, (Rule)this.getBestClassifier(), kNeighbours);
			for (NeighbourNode nn : nns) {
				for (Node node : nodes) {
					try {
						nn.getNode().equals(node);
					} catch (Exception e) {
						System.out.println("mario");
						System.out.println(nn.getNode());
						System.out.println(nn);
						System.out.println(nn.getNode());
						e.printStackTrace();
					}
					if (nn.getNode().equals(node)) {
						result.add(node);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
//	public List<NodeAnalysis> getNeighbourAnalysis(List<NodeAnalysis> analysis, int kNeighbours) {
//		List<NodeAnalysis> result = new ArrayList<NodeAnalysis>();
//		try {
//			List<NeighbourNode> nns = WekaUtil.getNeighbourNodesFromAnalysis(analysis, (Rule)this.getBestClassifier(), kNeighbours);
//			for (NeighbourNode nn : nns) {
//				for (NodeAnalysis anl : analysis) {
//					if (nn.getNode().equals(anl.getNode())) {
//						result.add(anl);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
	
	public List<Node> getDirectionNeighbourNodes(List<Node> nodes, int kNeighbours) {
		boolean verify = (this.direction == 1 ? true : false);
		List<Node> filteredNodes = new ArrayList<Node>();
		for (Node node : nodes) {
			if (this.getBestClassifier().verify(node) == verify) {
				filteredNodes.add(node);
			}
		}
		
		if (filteredNodes.size() == 0) {
			return nodes;
		}
		
		return getNeighbourNodes(filteredNodes, kNeighbours);
	}
	
	public List<NodeAnalysis> getDirectionNeighbourNodesFromAnalysis(List<NodeAnalysis> analysis, int kNeighbours) {
		Map<Node,NodeAnalysis> nodesMap = new HashMap<Node,NodeAnalysis>();
		for (NodeAnalysis anl : analysis) {
			nodesMap.put(anl.getNode(), anl);
		}
		List<Node> neighboursNodes = this.getDirectionNeighbourNodes(new ArrayList<Node>(nodesMap.keySet()), kNeighbours);
		List<NodeAnalysis> result = new ArrayList<NodeAnalysis>();
		for (Node resultNode : neighboursNodes) {
			result.add(nodesMap.get(resultNode));
		}
		return result;
	}
	
	public Map<Integer, List<Rule>> getRuleMap() {
		return ruleMap;
	}

	public void setRuleMap(Map<Integer, List<Rule>> ruleMap) {
		this.ruleMap = ruleMap;
	}
	
	public void addRule(Rule rule, int heuristicID) {
		if (!this.getRuleMap().keySet().contains(heuristicID)) {
			this.getRuleMap().put(heuristicID, new ArrayList<Rule>());
		}
		this.getRuleMap().get(heuristicID).add(rule);
		
//		if (!this.initialRules.keySet().contains(heuristicID)) {
//			this.initialRules.put(heuristicID, rule);
//		}
		
	}
	
	public Classifier getBestClassifier() {
		return super.getBestClassifier();
	}
}
