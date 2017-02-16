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
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.FeatureEnvySniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.SimpleSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.SimpleKNNSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.DataClassHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.FeatureEnvyHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.GodClassHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.LongMethodHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.PrimitiveObsessionHeuristicSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.SimpleHeuristicSniffer;
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

public class ICPCCrossExperiment {

	private Map<Developer, SimpleSniffer> snifferMap;
	private Map<Integer, Developer> developers;
	private String project;

	public ICPCCrossExperiment(String smell, String project) throws IOException {
		this.snifferMap = new HashMap<Developer, SimpleSniffer>();
		this.project = project;
		this.loadDevelopers("data/heuristic1/"+project+"/developers-" + smell + ".txt");
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
			} else {
				for (Rule rule : ((SimpleHeuristicSniffer)sniffer).getInitialRules().values()) {
					if (!node.getMetricNames().containsAll(
							rule.getMetricNames())) {
						System.out.println("Node with insufficient metrics: " + node);
					}
				}
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
	
	public int getHeuristicFromDeveloper(Integer devID, String smell) throws IOException {
		Map<Integer,Integer> data = this.getHeuristicsMap("data/heuristic1/preferences-"+smell+".txt");
		return data.get(devID);
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

	public void testPreferences(Class<? extends Sniffer> SnifferClass,
			String analysisSource) throws IOException, InstantiationException,
			IllegalAccessException {
		Project project = new Project("Xerces", "path");
		project.addNodesFromCSV("data/heuristic1/xerces/xerces-gc.csv",
				NodeType.ClassDefinition);
		project.addNodesFromCSV("data/heuristic1/xerces/xerces-lpl.csv",
				NodeType.MethodDefinition);
		project.addNodesFromCSV("data/heuristic1/xerces/xerces-lm.csv",
				NodeType.MethodDefinition);
		project.addNodesFromCSV("data/heuristic1/xerces/xerces-fe.csv",
				NodeType.MethodDefinition);

		for (Developer developer : this.developers.values()) {
			Sniffer sniffer = SnifferClass.newInstance();
			this.loadPreferences("data/ase2015/xerces/preferences-kappa.txt");
			this.loadAnalysis(sniffer, analysisSource, project, developer);
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
	
	public String train(SimpleHeuristicSniffer sniffer, Map<Node,NodeAnalysis> analysisToTrain, Developer developer, int folds) throws IOException, InstantiationException,
			IllegalAccessException {
		
		SimpleLogger.log("Iniciando análise para developer: " + developer.getId());
		SimpleLogger.log("Regra Inicial: " + sniffer.getBestClassifier());

		List<List<NodeAnalysis>> foldList = this.createFolds(new ArrayList<NodeAnalysis>(analysisToTrain.values()), folds);
		EvaluationList accuracy = new EvaluationList();
		float iterations = 0;
		
		EvaluationList accuracyJ48 = new EvaluationList();
		EvaluationList accuracyJRip = new EvaluationList();
		EvaluationList accuracyRF = new EvaluationList();
		EvaluationList accuracySMO = new EvaluationList();
		EvaluationList accuracyNB = new EvaluationList();
		EvaluationList accuracySVM = new EvaluationList();
		
		for (int testFold = 0; testFold < folds; testFold++) {
			sniffer.initialize();
			List<NodeAnalysis> trainAnalysis = new ArrayList<NodeAnalysis>();
			for (int trainFold = 0; trainFold < folds; trainFold++) {
				if (trainFold == testFold) {
					continue;
				}
				trainAnalysis.addAll(foldList.get(trainFold));
			}
			
			WekaJ48Classifier j48 = new WekaJ48Classifier("J48", sniffer.getSmell()); //Pruned
			WekaJRipClassifier jRip = new WekaJRipClassifier("JRip", sniffer.getSmell());
			WekaRandomForestClassifier rf = new WekaRandomForestClassifier("RF", sniffer.getSmell());
			WekaSMOClassifier smo = new WekaSMOClassifier("SMO", sniffer.getSmell()); //PolyKernel
			WekaNaiveBayesClassifier nb = new WekaNaiveBayesClassifier("NB", sniffer.getSmell());
			WekaLibSVMClassifier svm = new WekaLibSVMClassifier("SVM", sniffer.getSmell()); // C-SVC, Kernel Radial (RBF)
			
			j48.update(trainAnalysis);
			jRip.update(trainAnalysis);
			rf.update(trainAnalysis);
			smo.update(trainAnalysis);
			nb.update(trainAnalysis);
			svm.update(trainAnalysis);
			
			List<NodeAnalysis> analysisToTest = new ArrayList<NodeAnalysis>(foldList.get(testFold));
			
//			accuracyJ48.add(ClassifierEvaluator.getAccuracy(j48, analysisToTest));
//			accuracyJRip.add(ClassifierEvaluator.getAccuracy(jRip, analysisToTest));
//			accuracyRF.add(ClassifierEvaluator.getAccuracy(rf, analysisToTest));
//			accuracySMO.add(ClassifierEvaluator.getAccuracy(smo, analysisToTest));
//			accuracyNB.add(ClassifierEvaluator.getAccuracy(nb, analysisToTest));
//			accuracySVM.add(ClassifierEvaluator.getAccuracy(svm, analysisToTest));
			
			accuracyJ48.add(ClassifierEvaluator.getFMeasure(j48, analysisToTest));
			accuracyJRip.add(ClassifierEvaluator.getFMeasure(jRip, analysisToTest));
			accuracyRF.add(ClassifierEvaluator.getFMeasure(rf, analysisToTest));
			accuracySMO.add(ClassifierEvaluator.getFMeasure(smo, analysisToTest));
			accuracyNB.add(ClassifierEvaluator.getFMeasure(nb, analysisToTest));
			accuracySVM.add(ClassifierEvaluator.getFMeasure(svm, analysisToTest));
			
			accuracyJ48.addMatrix(ClassifierEvaluator.getConfusionMatrix(j48, analysisToTest));
			accuracyJRip.addMatrix(ClassifierEvaluator.getConfusionMatrix(jRip, analysisToTest));
			accuracyRF.addMatrix(ClassifierEvaluator.getConfusionMatrix(rf, analysisToTest));
			accuracySMO.addMatrix(ClassifierEvaluator.getConfusionMatrix(smo, analysisToTest));
			accuracyNB.addMatrix(ClassifierEvaluator.getConfusionMatrix(nb, analysisToTest));
			accuracySVM.addMatrix(ClassifierEvaluator.getConfusionMatrix(svm, analysisToTest));
			
			while (trainAnalysis.size() > 0) {
				NodeAnalysis nodeAnl = sniffer.getDirectionNeighbourNodesFromAnalysis(trainAnalysis, 1).get(0);
				trainAnalysis.remove(nodeAnl);
				SimpleLogger.log("Adicionando Analise: "+ sniffer.verify(nodeAnl.getNode()) + "-" + nodeAnl.isVerify() + " - " + nodeAnl.getNode().getMetricValues());
				sniffer.addAnalysis(nodeAnl);
			}
			
//			accuracy.add(ClassifierEvaluator.getAccuracy(sniffer.getBestClassifier(), analysisToTest));
			accuracy.add(ClassifierEvaluator.getFMeasure(sniffer.getBestClassifier(), analysisToTest));
			accuracy.addMatrix(ClassifierEvaluator.getConfusionMatrix(sniffer.getBestClassifier(), analysisToTest));
			iterations += ((Rule)sniffer.getBestClassifier()).getIterations();
		}
		
		String result = developer.getId() + "\t"
				+ accuracyJ48.getAverage() +"\t"
				+ accuracyJRip.getAverage() +"\t"
				+ accuracyRF.getAverage() +"\t"
				+ accuracySMO.getAverage() +"\t"
				+ accuracyNB.getAverage() +"\t"
				+ accuracySVM.getAverage() +"\t"
				+ accuracy.getAverage() +"\t"
				+ iterations/folds + "\tFolds: "+ folds + " Smell: "+ sniffer.getSmell().getShortName().toUpperCase();
		
		String result2 = developer.getId()
				+ Arrays.toString(accuracyJ48.sumMatrix()) +"\t"
				+ Arrays.toString(accuracyJRip.sumMatrix()) +"\t"
				+ Arrays.toString(accuracyRF.sumMatrix()) +"\t"
				+ Arrays.toString(accuracySMO.sumMatrix()) +"\t"
				+ Arrays.toString(accuracyNB.sumMatrix()) +"\t"
				+ Arrays.toString(accuracySVM.sumMatrix()) +"\t"
				+ Arrays.toString(accuracy.sumMatrix()) +"\t"
				+ iterations/folds + "\tFolds: "+ folds + " Smell: "+ sniffer.getSmell().getShortName().toUpperCase();
		
		return result2;
		
//		System.out.println(developer.getId() + "\t"
//				+ accuracyJ48/folds +"\t"
//				+ accuracyJRip/folds +"\t"
//				+ accuracyRF/folds +"\t"
//				+ accuracySMO/folds +"\t"
//				+ accuracyNB/folds +"\t"
//				+ accuracySVM/folds +"\t"
//				+ accuracy/folds +"\t"
//				+ iterations/folds + "\tFolds: "+ folds + " Smell: "+ sniffer.getSmell().getShortName().toUpperCase());
	}
	
	private static Project getProject(String projectName, Smell smell) throws IOException {
		Project project = new Project(projectName, "path");
		project.addNodesFromCSV("data/heuristic1/"+projectName+"/"+projectName+"-" + smell.getShortName().toLowerCase() + ".csv",
				smell.getType());
		return project;
	}
	
	public static void main(String[] args) throws IOException,
			InstantiationException, IllegalAccessException {
//		SimpleHeuristicSniffer sniffer = new GodClassHeuristicSniffer();
		SimpleHeuristicSniffer sniffer = new LongMethodHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer = new DataClassHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer = new PrimitiveObsessionHeuristicSniffer();
//		SimpleHeuristicSniffer sniffer = new FeatureEnvyHeuristicSniffer();
		String smell = sniffer.getSmell().getShortName().toLowerCase();
		String projectName = "custom";
		ICPCCrossExperiment experiment = new ICPCCrossExperiment(smell, projectName);
		Project project = getProject(projectName, sniffer.getSmell());
		String analysisSource = "data/heuristic1/"+projectName+"/an-"+ smell + ".csv";
		
		String result = "";

		for (Developer developer : experiment.developers.values()) {
//			if (developer.getId() == 9) {
			Map<Node,NodeAnalysis> trainAnalysis = experiment.loadAnalysis(sniffer, analysisSource, project, developer);
//				List<NodeAnalysis> anls = new ArrayList<NodeAnalysis>(trainAnalysis.values());
//				Instances instances = WekaUtil.createWekaInstancesFromAnalysis(anls);
//				System.out.println(instances);
//				WekaUtil.applyMLAlgorithm(instances);
//				break;}
			result += experiment.train(sniffer, trainAnalysis, developer, 5);
			result += "\n";
//			}
		}
		System.out.println(result);
	}

}
