package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn;

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
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.RuleEvaluator;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.util.SimpleLogger;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public abstract class SimpleKNNSniffer extends Sniffer {
	
	private int direction = 0;
	
	public SimpleKNNSniffer(String name, Smell smell) {
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
	
	private void changeDirection(boolean verify) {
		this.direction = verify ? -1 : 1;
	}
	
	public void addAnalysis(NodeAnalysis analysis) {
		SimpleLogger.log("- Adicionando Analises: ");
		this.getAnalysis().add(analysis);
		if (analysis.isVerify() != this.getBestClassifier().verify(analysis.getNode())) {
			SimpleLogger.log("-- Diferenca Encontrada: ");
			Rule rule = ((Rule)this.getBestClassifier()).update(analysis);
			rule.setIterations(this.getAnalysis().size());
			rule.setName("#" + this.getAnalysis().size());
			this.getClassifiers().add(rule);
			SimpleLogger.log("---  Adicionando Regra: " + rule);
		} else {
			this.changeDirection(analysis.isVerify());
		}
		this.updateClassifier();
	}
	
	public void updateClassifier() {
		if (this.getClassifiers() == null || this.getClassifiers().size() == 0) {
			return;
		}
		
		Classifier newBestClassifier = this.getClassifiers().get(0);
		Float bestValue = ClassifierEvaluator.getEvaluation(newBestClassifier, this.getAnalysis());
		if (bestValue.isNaN()) bestValue = 0f;
				
		for (Classifier classifier : this.getClassifiers()) {
			Float evaluation = ClassifierEvaluator.getEvaluation(classifier, this.getAnalysis());
			if (evaluation.isNaN()) evaluation = 0f;
			if (evaluation > bestValue) {
				newBestClassifier = classifier;
				bestValue = evaluation;
			}	
		}
		
		if (!newBestClassifier.equals(this.getBestClassifier())) {
			this.setBestClassifier(newBestClassifier);
			SimpleLogger.log("-------   Melhor Regra: " + this.getBestClassifier());

		}
	}
	
	public static SimpleKNNSniffer fromSmell(Smell smell) {
		if (smell.equals(Smell.LongParameterList()))
			return new LongParameterListKNNSniffer();
		else if (smell.equals(Smell.LongMethod()))
			return new LongMethodKNNSniffer();
		else if (smell.equals(Smell.GodClass()))
			return new GodClassKNNSniffer();
		else if (smell.equals(Smell.FeatureEnvy()))
			return new FeatureEnvyKNNSniffer();
		return null;
	}
	
	public List<Node> getNeighbourNodes(List<Node> nodes, int kNeighbours) {
		List<Node> result = new ArrayList<Node>();
		try {
			List<NeighbourNode> nns = WekaUtil.getNeighbourNodes(nodes, (Rule)this.getBestClassifier(), kNeighbours);
			for (NeighbourNode nn : nns) {
				for (Node node : nodes) {
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

}
