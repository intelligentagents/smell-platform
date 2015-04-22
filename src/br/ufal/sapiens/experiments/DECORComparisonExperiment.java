package br.ufal.sapiens.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import br.ufal.sapiens.refactoring.SmellPlatform;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.SniffedSmell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.TrueClassifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.SimpleSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.FeatureEnvyKNNSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.GodClassKNNSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.LongMethodKNNSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.LongParameterListKNNSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.SimpleKNNSniffer;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.NodeType;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.util.FileUtil;
import br.ufal.sapiens.refactoring.util.SimpleLogger;

public class DECORComparisonExperiment {

	private Map<Developer, SimpleSniffer> snifferMap;
	private Map<Integer, Developer> developers;
	private String projectPath;

	public DECORComparisonExperiment(String projectPath) throws IOException {
		this.snifferMap = new HashMap<Developer, SimpleSniffer>();
		this.projectPath = projectPath;
		this.loadDevelopers(this.projectPath + "/../developers.txt");
	}

	public Map<Node,NodeAnalysis> loadAnalysis(Sniffer sniffer,
			String analysisSource, Project project, Developer developer)
			throws IOException {
		List<String[]> data = FileUtil.getCSVData(analysisSource);
		// System.out.println("Initial rule: " +
		// sniffer.getInitialRule().toString());
		Map<Node,NodeAnalysis> analysisMap = new HashMap<Node,NodeAnalysis>();
		for (int i = 0; i < data.size(); i++) {
			Node node = project.getNodeFromName(data.get(i)[1]);
			if (node == null) {
				System.out.println("Node not found: " + data.get(i)[1]);
			} else if (!node.getMetricNames().containsAll(
					sniffer.getBestClassifier().getMetricNames())) {
				System.out.println("Node with insufficient metrics: " + node);
			} else {
				boolean verify = ("1".equals(data.get(i)[2])) ? true : false;
				if (new Integer(data.get(i)[0]) == developer.getId()) {
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

	public void loadPreferences(String developersPrefs) throws IOException {
		List<String[]> data = FileUtil.getCSVData(developersPrefs);
		for (int i = 0; i < data.size(); i++) {
			Integer devId = new Integer(data.get(i)[0]);
			String smellName = data.get(i)[1];
			String rawRule = data.get(i)[2];
			Smell smell = Smell.fromShortName(smellName);
			Rule rule = Rule.fromString(smell, rawRule);
			Developer dev = this.developers.get(devId);
			SimpleSniffer sniffer = SimpleSniffer.fromSmell(smell);
			sniffer.getClassifiers().add(rule);
			this.snifferMap.put(dev, sniffer);
		}
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

	public void testPreferences(Class<? extends Sniffer> SnifferClass, Project project,
			String analysisSource) throws IOException, InstantiationException,
			IllegalAccessException {
		for (Developer developer : this.developers.values()) {
			Sniffer sniffer = SnifferClass.newInstance();
//			this.loadPreferences("data/hist-comparison/icpc2015/preferences-kappa.txt");
			if (null != analysisSource) this.loadAnalysis(sniffer, analysisSource, project, developer);
			testDeveloperPreferences(project, sniffer, developer);
		}
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
		int elements = _nodes.size() / folds;
		
		while (nodes.size() > 0) {
			List<NodeAnalysis> foldList = new ArrayList<NodeAnalysis>();
			while (foldList.size() < elements && nodes.size() > 0) {
				Collections.shuffle(nodes);
				foldList.add(nodes.remove(0));
			}
			foldLists.add(foldList);
		}
		
		return foldLists;
	}

	public void train(Class<? extends SimpleKNNSniffer> SnifferClass, Project project,
			String analysisSource, int folds, int devId) throws IOException, InstantiationException,
			IllegalAccessException {
		
		Developer developer = this.developers.get(devId);
		
		SimpleLogger.log("Iniciando análise para developer: " + developer.getId());
		SimpleKNNSniffer sniffer = SnifferClass.newInstance();
		SimpleLogger.log("Regra Inicial: " + sniffer.getBestClassifier());
		Map<Node,NodeAnalysis> analysis = loadAnalysis(sniffer, analysisSource,	project, developer);
		
		List<List<NodeAnalysis>> foldList = this.createFolds(new ArrayList<NodeAnalysis>(analysis.values()), folds);
		float recall = 0;
		float precision = 0;
		float fmeasure = 0;
		
		Classifier decor = new TrueClassifier("DECOR", sniffer.getSmell());
		float recall2 = 0;
		float precision2 = 0;
		float fmeasure2 = 0;
		
		float iterations = 0;
		
		for (int testFold = 0; testFold < folds; testFold++) {
			sniffer = SnifferClass.newInstance();
			List<NodeAnalysis> trainAnalysis = new ArrayList<NodeAnalysis>();
			for (int trainFold = 0; trainFold < folds; trainFold++) {
				if (trainFold == testFold) {
					continue;
				}
				trainAnalysis.addAll(foldList.get(trainFold));
			}
			
			while (trainAnalysis.size() > 0) {
				NodeAnalysis nodeAnl = sniffer.getDirectionNeighbourNodesFromAnalysis(trainAnalysis, 1).get(0);
				trainAnalysis.remove(nodeAnl);
				SimpleLogger.log("Adicionando Analise: "+ sniffer.verify(nodeAnl.getNode()) + "-" + nodeAnl.isVerify() + " - " + nodeAnl.getNode().getMetricValues());
				sniffer.addAnalysis(nodeAnl);
			}
			
			List<NodeAnalysis> analysisToTest = new ArrayList<NodeAnalysis>(foldList.get(testFold));
			
			recall += ClassifierEvaluator.getRecall(((SimpleKNNSniffer) sniffer).getInitialRule(), analysisToTest);
			precision += ClassifierEvaluator.getPrecision(((SimpleKNNSniffer) sniffer).getInitialRule(), analysisToTest);
			fmeasure += ClassifierEvaluator.getFMeasure(((SimpleKNNSniffer) sniffer).getInitialRule(), analysisToTest);
			
			recall2 += ClassifierEvaluator.getRecall(decor, analysisToTest);
			precision2 += ClassifierEvaluator.getPrecision(decor, analysisToTest);
			fmeasure2 += ClassifierEvaluator.getFMeasure(decor, analysisToTest);
			
			
			iterations += ((Rule)sniffer.getBestClassifier()).getIterations(); 
		}
		
		System.out.println(developer.getId() + "\t"
				+ recall2/folds + " / " + recall/folds +"\t"
				+ precision2/folds + " / " + precision/folds +"\t"
				+ fmeasure2/folds + " / " + fmeasure/folds +"\t"
				+ iterations/folds + "\tFolds: "+ folds + " Analysis: "+ analysis.size());
		
	}
	
	private Node getNodeTest() {
		Node node = new Node("TESTE", NodeType.MethodDefinition);
		node.addMetricValue("mloc", 102f);
		return node;
	}
	
	private static Project getProject(String projectPath, String projectName) throws IOException {
		Project project = new Project(projectName, "path");
		String token = projectPath + "/" + projectName;
		project.addNodesFromCSV(token+"-gc-metrics.csv",
				NodeType.ClassDefinition);
//		project.addNodesFromCSV(token+"-lpl-metrics.csv",
//				NodeType.MethodDefinition);
//		project.addNodesFromCSV(token+"-lm-metrics.csv",
//				NodeType.MethodDefinition);
//		project.addNodesFromCSV(token+"-fe-metrics.csv",
//				NodeType.MethodDefinition);
		
		return project;
		
	}
	
	public static void main(String[] args) throws IOException,
			InstantiationException, IllegalAccessException {
		String projectName = "xerces";
		String smellName = "gc";
		String projectPath = "data/decor-comparison/" + projectName;
		DECORComparisonExperiment experiment = new DECORComparisonExperiment(projectPath);
		Project project = getProject(projectPath, projectName);
		
		String analysisSource = "data/decor-comparison/"+projectName+"/an-"+smellName+".csv";
		int folds = 6;
		for (int i = 1; i <= 1; i++) {
			experiment.train(GodClassKNNSniffer.class, project, analysisSource, folds, i);
		}
	}
	
}
