package br.ufal.sapiens.experiments;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.SmellPlatform;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.dt.DTSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.dt.FeatureEnvyDTSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.dt.GodClassDTSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.dt.LongMethodDTSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.dt.LongParameterListDTSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.SimpleSniffer;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.NodeType;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.util.FileUtil;

public class DTExperiment {
	
	private Map<Developer, SimpleSniffer> snifferMap;
	private Map<Integer,Developer> developers;
	
	public DTExperiment() throws IOException { 
		this.snifferMap = new HashMap<Developer,SimpleSniffer>();
		this.loadDevelopers("data/xerces/developers.txt");
	}
	
	public void loadAnalysis(Sniffer sniffer, String analysisSource, Project project, Developer developer) throws IOException {
		List<String[]> data = FileUtil.getCSVData(analysisSource);
//		System.out.println("Initial rule: " + sniffer.getInitialRule().toString());
		for (int i = 0; i < data.size(); i++) {
			Node node = project.getNodeFromName(data.get(i)[1]);
			if (node == null) {
				System.out.println("Node not found: " + data.get(i)[1]);
//			} else if (!node.getMetricNames().containsAll(sniffer.getBestClassifier().getMetricNames())) {
//				System.out.println("Node with insufficient metrics: " + node);
			} else {
				boolean verify = ("1".equals(data.get(i)[2])) ? true : false;
				if (new Integer(data.get(i)[0]) == developer.getId()) {
					NodeAnalysis analysis = new NodeAnalysis(node, sniffer.getSmell(), verify);
					sniffer.getAnalysis().add(analysis);
//					if (true) System.out.println(analysis);
				}
			}
		}
	}
	
	public void loadDevelopers(String developersSource) throws IOException {
		List<String[]> data = FileUtil.getCSVData(developersSource);
		Map<Integer,Developer> developers = new HashMap<Integer,Developer>();
		for (int i = 0; i < data.size(); i++) {
			Integer devId = new Integer(data.get(i)[0]);
			String devName = data.get(i)[1];
			developers.put(devId, new Developer(devId, devName));
		}
		this.developers = developers;
	}
	
//	public void loadPreferences(String developersPrefs) throws IOException {
//		List<String[]> data = FileUtil.getCSVData(developersPrefs);
//		for (int i = 0; i < data.size(); i++) {
//			Integer devId = new Integer(data.get(i)[0]);
//			String smellName = data.get(i)[1];
//			String rawRule = data.get(i)[2];
//			Smell smell = Smell.fromShortName(smellName);
//			Rule rule = Rule.fromString(smell, rawRule);
//			Developer dev = this.developers.get(devId);
//			SimpleSniffer sniffer = SimpleSniffer.fromSmell(smell);
//			sniffer.getClassifiers().add(rule);
//			this.snifferMap.put(dev, sniffer);
//		}
//	}
	
	public void testDeveloperPreferences(Project project, Sniffer sniffer, Developer developer) throws IOException {
		float personalizedPR = ClassifierEvaluator.getEvaluation(sniffer.getBestClassifier(), sniffer.getAnalysis());
		float initialPR = ClassifierEvaluator.getEvaluation(((SimpleSniffer)sniffer).getInitialRule(), sniffer.getAnalysis());
		System.out.println("Dev " + developer.getId() + " :" + 
//				developer.getBestRule(sniffer.getSmell()).toString() + 
				" - Personalized: " + personalizedPR + " - Initial: " + initialPR + 
				" - Analysis: " + sniffer.getAnalysis().size());
	}
	
//	public void testPreferences(Class<? extends Sniffer> SnifferClass, String analysisSource) throws IOException, InstantiationException, IllegalAccessException {
//		Project project = new Project("Xerces", "path");
//		project.addNodesFromCSV("data/xerces/xerces-gc.csv", NodeType.ClassDefinition);
//		project.addNodesFromCSV("data/xerces/xerces-lpl.csv", NodeType.MethodDefinition);
//		project.addNodesFromCSV("data/xerces/xerces-lm.csv", NodeType.MethodDefinition);
//		project.addNodesFromCSV("data/xerces/xerces-fe.csv", NodeType.MethodDefinition);
//		
//
//		for (Developer developer : this.developers.values()) {
//			Sniffer sniffer = SnifferClass.newInstance();
//			this.loadPreferences("data/xerces/preferences-kappa.txt");
//			this.loadAnalysis(sniffer, analysisSource, project, developer);
//			testDeveloperPreferences(project, sniffer, developer);
//		}
//	}
	
	public void train(Class<? extends Sniffer> SnifferClass, String analysisSource) throws IOException, InstantiationException, IllegalAccessException {
		Project project = new Project("GanttProject", "path");
		project.addNodesFromCSV("data/gantt/gantt-gc.csv", NodeType.ClassDefinition);
		project.addNodesFromCSV("data/gantt/gantt-lpl.csv", NodeType.MethodDefinition);
		project.addNodesFromCSV("data/gantt/gantt-lm.csv", NodeType.MethodDefinition);
		project.addNodesFromCSV("data/gantt/gantt-fe.csv", NodeType.MethodDefinition);
		
		for (Developer developer : developers.values()) {
//			if (developer.getId() != 3) continue;
			Sniffer sniffer = SnifferClass.newInstance();			
			loadAnalysis(sniffer, analysisSource, project, developer);
			sniffer.updateClassifier();
			System.out.println(sniffer.getBestClassifier());
//			SmellPlatform platform = new SmellPlatform(project, developer);
//			platform.updateClassifier(sniffer);
			
//			float personalizedPR = ClassifierEvaluator.getEvaluation(sniffer.getBestClassifier(), developer.getAnalysis().get(sniffer.getSmell()));
//			System.out.println("Dev " + developer.getId() + 
//					" :" + sniffer.getBestClassifier().toString() + 
//					" - Personalized: " + personalizedPR + 
//					" - Analysis: " + developer.getAnalysis().get(sniffer.getSmell()).size());
		}
	}
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
		DTExperiment experiment = new DTExperiment();
		
		experiment.train(GodClassDTSniffer.class, "data/gantt/an-blob-ptidej.csv");
//		experiment.testPreferences(FeatureEnvySniffer.class, "data/xerces/an-fe.csv");
	}

}
