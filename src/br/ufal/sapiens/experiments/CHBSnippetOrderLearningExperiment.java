package br.ufal.sapiens.experiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.EvaluationList;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.SimpleSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.SimpleKNNSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.DataClassHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.FeatureEnvyHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.GodClassHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.LongMethodHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.LongParameterListHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.MessageChainsHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.MiddleManHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.PrimitiveObsessionHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.SimpleHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.SpeculativeGeneralityHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.SwitchStatementHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.weka.WekaJ48Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.weka.WekaJRipClassifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.weka.WekaLibSVMClassifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.weka.WekaNaiveBayesClassifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.weka.WekaRandomForestClassifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.weka.WekaSMOClassifier;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.NodeType;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.util.FileUtil;
import br.ufal.sapiens.refactoring.util.SimpleLogger;

public class CHBSnippetOrderLearningExperiment {

	private Map<Developer, SimpleSniffer> snifferMap;
	private Map<Integer, Developer> developers;
	private String project;

	public CHBSnippetOrderLearningExperiment(String smell, String project) throws IOException {
		this.snifferMap = new HashMap<Developer, SimpleSniffer>();
		this.project = project;
		this.loadDevelopers("data/chb2017/"+project+"/developers-" + smell + ".txt");
	}

	public Map<Node,NodeAnalysis> loadAnalysis(Sniffer sniffer,
			String analysisSource, Project project, Developer developer)
			throws IOException {
		List<String[]> data = FileUtil.getCSVData(analysisSource);
		// System.out.println("Initial rule: " +
		// sniffer.getInitialRule().toString());
		Map<Node,NodeAnalysis> analysisMap = new HashMap<Node,NodeAnalysis>();
		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i)[0].equalsIgnoreCase(sniffer.getSmell().getShortName())) continue;
			Node node = project.getNodeFromName(data.get(i)[2]);
			if (node == null) {
				System.out.println("Node not found: " + data.get(i)[2]);
			} else {
				boolean verify = ("1".equals(data.get(i)[3])) ? true : false;
				if (new Integer(data.get(i)[1]) == developer.getId()) {
					NodeAnalysis analysis = new NodeAnalysis(node,
							sniffer.getSmell(), verify);
					analysisMap.put(analysis.getNode(), analysis);
				}
			}
		}
		return analysisMap;
	}

	public void loadDevelopers(String developersSource) throws IOException {
		List<String[]> data = FileUtil.getCSVData(developersSource);
		Map<Integer, Developer> developers = new HashMap<Integer, Developer>();
		for (int i = 0; i < data.size(); i++) {
			Integer devId = new Integer(data.get(i)[0]);
			String devName = data.get(i)[1];
			developers.put(devId, new Developer(devId, devName));
		}
		this.developers = developers;
	}

	public static void loadRules(SimpleHeuristicSniffer sniffer, String initialRules) throws IOException {
		List<String[]> data = FileUtil.getCSVData(initialRules);
		for (int i = 0; i < data.size(); i++) {
			String slug = data.get(i)[0];
			if (!sniffer.getSmell().getShortName().equalsIgnoreCase(slug)) continue;
			
			Integer ruleId = new Integer(data.get(i)[1]);
			String rawRule = data.get(i)[2];
			Rule rule = Rule.fromString(sniffer.getSmell(), rawRule);
			sniffer.addRule(rule, ruleId);
			if (ruleId == 1) sniffer.setBestClassifier(rule);
		}
	}
	
	public Map<Integer,Integer> getHeuristicsMap(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		while ((line = br.readLine()) != null) {
			String[] cols = line.split(",");
			if (cols.length <= 1) cols = line.split(";");
			map.put(new Integer(cols[0]), new Integer(cols[1]));
		}
		br.close();
		return map;
	}
	public void testDeveloperPreferences(Project project, Sniffer sniffer,
			Developer developer) throws IOException {
		float personalizedPR = ClassifierEvaluator.getEvaluation(
				sniffer.getBestClassifier(), sniffer.getAnalysis());
		float initialPR = ClassifierEvaluator.getEvaluation(
				((SimpleKNNSniffer) sniffer).getInitialRule(), sniffer.getAnalysis());
		System.out.println("Dev " + developer.getId()
				+ " :"
				+
				// developer.getBestRule(sniffer.getSmell()).toString() +
				" - Personalized: " + personalizedPR + " - Initial: "
				+ initialPR + " - Analysis: "
				+ sniffer.getAnalysis().size());
	}

	public void test(int devId, Sniffer sniffer, List<Node> nodes, Map<Node,NodeAnalysis> analysis) {
		float personalizedPR = ClassifierEvaluator.getEvaluation(
				sniffer.getBestClassifier(), new ArrayList<NodeAnalysis>(analysis.values()));
		float initialPR = ClassifierEvaluator.getEvaluation(
				((SimpleKNNSniffer) sniffer).getInitialRule(), new ArrayList<NodeAnalysis>(analysis.values()));
		System.out.println("Dev " + devId
				+ " :"
				+
				// developer.getBestRule(sniffer.getSmell()).toString() +
				" - Personalized: " + personalizedPR + " - Initial: "
				+ initialPR + " - Analysis: "
				+ sniffer.getAnalysis().size());
	}
	
	public List<NodeAnalysis> createLearningList(List<NodeAnalysis> _nodes) {
		List<NodeAnalysis> nodes = new ArrayList<NodeAnalysis>(_nodes);
		Collections.shuffle(nodes);
		
		boolean next = true;
		if (nodes.get(0).isVerify() == true) next=false;
		for (int i = 1; i < nodes.size(); i++) {
			if (nodes.get(i).isVerify() == next) {
				nodes.set(1, nodes.get(i)); // Garantindo que o segundo node seja diferente (true/false) do primeiro
				break;
			}
		}
		
//		next = true;
//		if (nodes.get(nodes.size() - 1).isVerify() == true) next=false;
//		for (int i = 13; i > 1; i--) {
//			if (nodes.get(i).isVerify() == next) {
//				nodes.set(13, nodes.get(i)); // Garantindo que o penultimo node seja diferente (true/false) do ultimo
//				break;
//			}
//		}
		
		return nodes;
	}
	
	public List<List<NodeAnalysis>> createFolds(List<NodeAnalysis> _nodes, int folds) {
		List<NodeAnalysis> nodes = new ArrayList<NodeAnalysis>(_nodes);
		List<List<NodeAnalysis>> foldLists = new ArrayList<List<NodeAnalysis>>();
		
		List<NodeAnalysis> trueNodes = new ArrayList<NodeAnalysis>();
		List<NodeAnalysis> falseNodes = new ArrayList<NodeAnalysis>();
		
		for (NodeAnalysis node : nodes) {
			if (node.isVerify()) 
				trueNodes.add(node);
			else
				falseNodes.add(node);
		}
		
		for (int i = 0; i < folds; i++) {
			foldLists.add(new ArrayList<NodeAnalysis>());
		}
		
		int count = 0;
		while (falseNodes.size() > 0) {
			int index = (count%folds);
			Collections.shuffle(falseNodes);
			foldLists.get(index).add(falseNodes.remove(0));
			count += 1;
		}

		count = 0;
		while (trueNodes.size() > 0) {
			int index = folds -1 -(count%folds);
			Collections.shuffle(trueNodes);
			foldLists.get(index).add(trueNodes.remove(0));
			count += 1;
		}
		
		return foldLists;
	}
	
	public int countAnalysis(Collection<NodeAnalysis> analysis, boolean verified) {
		int count = 0;
		for (NodeAnalysis nodeAnalysis : analysis) {
			if (nodeAnalysis.isVerify() == verified)
				count += 1;
		}
		return count;
	}
	
	public List<List<Integer>> train(SimpleHeuristicSniffer sniffer1, SimpleHeuristicSniffer sniffer2, List<NodeAnalysis> trainAnalysis, List<NodeAnalysis> testAnalysis) throws IOException, InstantiationException,
			IllegalAccessException {
		
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		List<NodeAnalysis> aList1 = new ArrayList<NodeAnalysis>(trainAnalysis);
		List<NodeAnalysis> aList2 = new ArrayList<NodeAnalysis>(trainAnalysis);
		Collections.shuffle(aList1);
		Collections.shuffle(aList2);
		
		while (aList1.size() > 0) {
			NodeAnalysis nodeAnl = sniffer1.getDirectionNeighbourNodesFromAnalysis(aList1, 1).get(0);
			aList1.remove(nodeAnl);
			sniffer1.addAnalysis(nodeAnl);
		}
		result.add(ClassifierEvaluator.getConfusionMatrix(sniffer1.getBestClassifier(), testAnalysis));
		
		while (aList2.size() > 0) {
			NodeAnalysis nodeAnl = aList2.get(0);
			aList2.remove(nodeAnl);
			sniffer2.addAnalysis(nodeAnl);
		}
		result.add(ClassifierEvaluator.getConfusionMatrix(sniffer2.getBestClassifier(), testAnalysis));
		
		return result;
		
	}
	
	private static Project getProject(String projectName, Smell smell) throws IOException {
		Project project = new Project(projectName, "path");
		project.addNodesFromCSV("data/chb2017/"+projectName+"/"+projectName+"-" + smell.getShortName().toLowerCase() + ".csv",
				smell.getType());
		return project;
	}
	
	
	
	public static void main(String[] args) throws IOException,
			InstantiationException, IllegalAccessException {
		SimpleHeuristicSniffer sniffer1 = new GodClassHeuristicSniffer();
		SimpleHeuristicSniffer sniffer2 = new GodClassHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer1 = new DataClassHeuristicSniffer();		
//		SimpleHeuristicSniffer sniffer2 = new DataClassHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer1 = new FeatureEnvyHeuristicSniffer();		
//		SimpleHeuristicSniffer sniffer2 = new FeatureEnvyHeuristicSniffer();		
//		SimpleHeuristicSniffer sniffer1 = new LongMethodHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer2 = new LongMethodHeuristicSniffer();		
//		SimpleHeuristicSniffer sniffer = new LongParameterListHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer = new SwitchStatementHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer = new MiddleManHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer = new PrimitiveObsessionHeuristicSniffer();		
//		SimpleHeuristicSniffer sniffer = new MessageChainsHeuristicSniffer();
		

		
		List<List<EvaluationList>> result = new ArrayList<List<EvaluationList>>();
		for (int i = 0; i < 14; i++) {
			List<EvaluationList> aList = new ArrayList<EvaluationList>(); 
			for (int j = 0; j < 2; j++) {
				aList.add(new EvaluationList());
			}
			result.add(aList);
		}
		
		String smell = sniffer1.getSmell().getShortName().toLowerCase();
		String projectName = "custom";
		CHBSnippetOrderLearningExperiment experiment = new CHBSnippetOrderLearningExperiment(smell, projectName);
		Project project = getProject(projectName, sniffer1.getSmell());
		String analysisSource = "data/chb2017/"+projectName+"/an-all.csv";
		
		for (int repetitions = 0; repetitions < 10; repetitions++) {
			for (Developer developer : experiment.developers.values()) {
				Map<Node,NodeAnalysis> allAnalysis = experiment.loadAnalysis(sniffer1, analysisSource, project, developer);
				List<NodeAnalysis> testAnalysis = experiment.createLearningList(new ArrayList<NodeAnalysis>(allAnalysis.values()));
				
				List<NodeAnalysis> trainAnalysis = new ArrayList<NodeAnalysis>();
				trainAnalysis.add(testAnalysis.remove(0)); //adicionando o primeiro item da lista
				
	//			while (testAnalysis.size() >= 2) {
				while (testAnalysis.size()>0) {
					trainAnalysis.add(testAnalysis.remove(0));
					sniffer1.clearRules();
					sniffer2.clearRules();
					loadRules(sniffer1, "data/chb2017/"+projectName+"/rules.txt");
					loadRules(sniffer2, "data/chb2017/"+projectName+"/rules.txt");
					List<List<Integer>> result0 = experiment.train(sniffer1, sniffer2, trainAnalysis, new ArrayList<NodeAnalysis>(allAnalysis.values()));
					for (int i = 0; i < result0.size(); i++) {
						result.get(trainAnalysis.size()-2).get(i).addMatrix(result0.get(i));
					}
				}
			}
		}
			
		String output = "";
		for (int i = 0; i < 14; i++) {
			List<EvaluationList> aList = result.get(i);
			output += sniffer1.getSmell().getShortName() + ":" + (i+2) + "\t";
			for (int j = 0; j < aList.size(); j++) {
				output += aList.get(j).printSumMatrix() + "\t";
			}
			output += "\n";
		}
			
		System.out.println(output);
		
	}

}
