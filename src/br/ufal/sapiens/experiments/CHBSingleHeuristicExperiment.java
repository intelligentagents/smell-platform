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

public class CHBSingleHeuristicExperiment {

	private Map<Developer, SimpleSniffer> snifferMap;
	private Map<Integer, Developer> developers;
	private String project;

	public CHBSingleHeuristicExperiment(String smell, String project) throws IOException {
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
	
	public static int countRules(SimpleHeuristicSniffer sniffer, String initialRules) throws IOException {
		int result = 0;
		List<String[]> data = FileUtil.getCSVData(initialRules);
		for (int i = 0; i < data.size(); i++) {
			String slug = data.get(i)[0];
			if (!sniffer.getSmell().getShortName().equalsIgnoreCase(slug)) continue;
			result++;
		}
		return result;
	}
	
	public static void loadRule(SimpleHeuristicSniffer sniffer, String initialRules, int _ruleId) throws IOException {
		List<String[]> data = FileUtil.getCSVData(initialRules);
		for (int i = 0; i < data.size(); i++) {
			String slug = data.get(i)[0];
			if (!sniffer.getSmell().getShortName().equalsIgnoreCase(slug)) continue;
			
			Integer ruleId = new Integer(data.get(i)[1]);
			String rawRule = data.get(i)[2];
			if (ruleId.equals(new Integer(_ruleId))) {
				Rule rule = Rule.fromString(sniffer.getSmell(), rawRule);
				sniffer.addRule(rule, ruleId);
				sniffer.setBestClassifier(rule);
			}
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
	
	public String train(Class<? extends SimpleHeuristicSniffer> snifferClass, String rulesSource, Map<Node,NodeAnalysis> analysisToTrain, Developer developer, int folds) throws IOException, InstantiationException,
			IllegalAccessException {
		int nRules = countRules(snifferClass.newInstance(), rulesSource);
		List<List<NodeAnalysis>> foldList = this.createFolds(new ArrayList<NodeAnalysis>(analysisToTrain.values()), folds);
		EvaluationList accuracy = new EvaluationList();
		
		List<EvaluationList> accuracy1 = new ArrayList<EvaluationList>();
		for (int i = 0; i < nRules; i++) {
			accuracy1.add(new EvaluationList());
		}
		
		
		for (int testFold = 0; testFold < folds; testFold++) {
			List<NodeAnalysis> trainAnalysis = new ArrayList<NodeAnalysis>();
			for (int trainFold = 0; trainFold < folds; trainFold++) {
				if (trainFold == testFold) {
					continue;
				}
				trainAnalysis.addAll(foldList.get(trainFold));
			}
			
			List<NodeAnalysis> analysisToTest = new ArrayList<NodeAnalysis>(foldList.get(testFold));
			
			SimpleHeuristicSniffer sniffer = snifferClass.newInstance();
			sniffer.clearRules();
			loadRules(sniffer, rulesSource);
			
			for (int i = 0; i < sniffer.getRuleMap().size(); i++) {
				List<NodeAnalysis> trainAnalysis1 = new ArrayList<NodeAnalysis>(trainAnalysis);
				SimpleHeuristicSniffer sniffer1 = snifferClass.newInstance();
				sniffer1.clearRules();
				loadRule(sniffer1, rulesSource, i+1);
				while (trainAnalysis1.size() > 0) {
					NodeAnalysis nodeAnl = sniffer1.getDirectionNeighbourNodesFromAnalysis(trainAnalysis1, 1).get(0);
					trainAnalysis1.remove(nodeAnl);
					sniffer1.addAnalysis(nodeAnl);
				}
				accuracy1.get(i).addMatrix(ClassifierEvaluator.getConfusionMatrix(sniffer1.getBestClassifier(), analysisToTest));
			}
			
			while (trainAnalysis.size() > 0) {
				NodeAnalysis nodeAnl = sniffer.getDirectionNeighbourNodesFromAnalysis(trainAnalysis, 1).get(0);
				trainAnalysis.remove(nodeAnl);
				SimpleLogger.log("Adicionando Analise: "+ sniffer.verify(nodeAnl.getNode()) + "-" + nodeAnl.isVerify() + " - " + nodeAnl.getNode().getMetricValues());
				sniffer.addAnalysis(nodeAnl);
			}
			
			accuracy.addMatrix(ClassifierEvaluator.getConfusionMatrix(sniffer.getBestClassifier(), analysisToTest));
		}
		
		String result = developer.getId() + "\t" + accuracy.printSumMatrix() + "\t";
		for (int i = 0; i < nRules; i++) {
			result += accuracy1.get(i).printSumMatrix() +"\t";
		}
		result += "\tFolds: "+ folds + " Smell: "+ snifferClass.newInstance().getSmell().getShortName().toUpperCase();
		
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
		Class<? extends SimpleHeuristicSniffer> sniffer = GodClassHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = DataClassHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = FeatureEnvyHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = LongMethodHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = LongParameterListHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = SwitchStatementHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = MessageChainsHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = MiddleManHeuristicSniffer.class;
//		Class<? extends SimpleHeuristicSniffer> sniffer = PrimitiveObsessionHeuristicSniffer.class;
		
		String smell = sniffer.newInstance().getSmell().getShortName().toLowerCase();
		String projectName = "custom";
		CHBSingleHeuristicExperiment experiment = new CHBSingleHeuristicExperiment(smell, projectName);
		Project project = getProject(projectName, sniffer.newInstance().getSmell());
		String analysisSource = "data/chb2017/"+projectName+"/an-all.csv";
		String rulesSource = "data/chb2017/"+projectName+"/rules.txt";
		
		String result = "";
		int repetition = 10;
		
		for (int i = 0; i < repetition; i++) {
			for (Developer developer : experiment.developers.values()) {
				Map<Node,NodeAnalysis> trainAnalysis = experiment.loadAnalysis(sniffer.newInstance(), analysisSource, project, developer);
				result += experiment.train(sniffer, rulesSource, trainAnalysis, developer, 5);
				result += "\n";
			}
			result += "\n\n";
		}

		System.out.println(result);
	}

}
