package br.ufal.sapiens.experiments;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.SmellPlatform;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.RuleEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.FeatureEnvySniffer;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.NodeType;
import br.ufal.sapiens.refactoring.util.FileUtil;

public class GanttAndXercesSimple {
	
	public static void loadAnalysis(Sniffer sniffer, String analysisSource, Project project, Developer developer) throws IOException {
		List<String[]> data = FileUtil.getCSVData(analysisSource);
//		System.out.println("Initial rule: " + sniffer.getInitialRule().toString());
		for (int i = 0; i < data.size(); i++) {
			Node node = project.getNodeFromName(data.get(i)[1]);
			if (node == null) {
				System.out.println("Node not found: " + data.get(i)[1]);
			} else if (!node.getMetricNames().containsAll(developer.getLastRule(sniffer.getSmell()).getMetricNames())) {
				System.out.println("Node with insufficient metrics: " + node);
			} else {
				boolean verify = ("1".equals(data.get(i)[2])) ? true : false;
				if (new Integer(data.get(i)[0]) == developer.getId()) {
					NodeAnalysis analysis = new NodeAnalysis(node, sniffer.getSmell(), verify);
					developer.addAnalysis(analysis);
					if (true) System.out.println(analysis);
				}
			}
		}
	}
	
	public static Map<Integer,Developer> loadDevelopers(String developersSource) throws IOException {
		List<String[]> data = FileUtil.getCSVData(developersSource);
		Map<Integer,Developer> developers = new HashMap<Integer,Developer>();
		for (int i = 0; i < data.size(); i++) {
			Integer devId = new Integer(data.get(i)[0]);
			String devName = data.get(i)[1];
			developers.put(devId, new Developer(devId, devName));
		}
		return developers;
	}
	
	public static void loadPreferences(String developersPrefs, Map<Integer,Developer> developers) throws IOException {
		List<String[]> data = FileUtil.getCSVData(developersPrefs);
		for (int i = 0; i < data.size(); i++) {
			Integer devId = new Integer(data.get(i)[0]);
			String smellName = data.get(i)[1];
			String rawRule = data.get(i)[2];
			Smell smell = Smell.fromShortName(smellName);
			Rule rule = Rule.fromString(smell, rawRule);
			Developer dev = developers.get(devId);
			dev.addRule(rule);
		}
	}
	
	public static void testDeveloperPreferences(Project project, Sniffer sniffer, Developer developer) throws IOException {
		float personalizedPR = RuleEvaluator.getEvaluation(developer.getBestRule(sniffer.getSmell()), developer.getAnalysis().get(sniffer.getSmell()));
		float initialPR = RuleEvaluator.getEvaluation(sniffer.getInitialRule(), developer.getAnalysis().get(sniffer.getSmell()));
		System.out.println("Dev " + developer.getId() + " :" + 
//				developer.getBestRule(sniffer.getSmell()).toString() + 
				" - Personalized: " + personalizedPR + " - Initial: " + initialPR + 
				" - Analysis: " + developer.getAnalysis().get(sniffer.getSmell()).size());
	}
	
	public static void testPreferences(Class<? extends Sniffer> SnifferClass, String analysisSource) throws IOException, InstantiationException, IllegalAccessException {
		Project project = new Project("Xerces", "path");
		project.addNodesFromCSV("data/xerces/xerces-gc.csv", NodeType.ClassDefinition);
		project.addNodesFromCSV("data/xerces/xerces-lpl.csv", NodeType.MethodDefinition);
		project.addNodesFromCSV("data/xerces/xerces-lm.csv", NodeType.MethodDefinition);
		project.addNodesFromCSV("data/xerces/xerces-fe.csv", NodeType.MethodDefinition);
		
		Map<Integer,Developer> developers = loadDevelopers("data/xerces/developers.txt");

		for (Developer developer : developers.values()) {
			Sniffer sniffer = SnifferClass.newInstance();
			developer.addRule(sniffer.getInitialRule());
			loadAnalysis(sniffer, analysisSource, project, developer);
			loadPreferences("data/xerces/preferences-kappa.txt", developers);
			testDeveloperPreferences(project, sniffer, developer);
		}
	}
	
	public static void train(Class<? extends Sniffer> SnifferClass, String analysisSource) throws IOException, InstantiationException, IllegalAccessException {
		Project project = new Project("GanttProject", "path");
		project.addNodesFromCSV("data/gantt/gantt-gc.csv", NodeType.ClassDefinition);
		project.addNodesFromCSV("data/gantt/gantt-lpl.csv", NodeType.MethodDefinition);
		project.addNodesFromCSV("data/gantt/gantt-lm.csv", NodeType.MethodDefinition);
		project.addNodesFromCSV("data/gantt/gantt-fe.csv", NodeType.MethodDefinition);
		Map<Integer,Developer> developers = loadDevelopers("data/gantt/developers.txt");
		
		for (Developer developer : developers.values()) {
			Sniffer sniffer = SnifferClass.newInstance();
			developer.addRule(sniffer.getInitialRule());
			loadAnalysis(sniffer, analysisSource, project, developer);
			SmellPlatform platform = new SmellPlatform(project, developer);
			platform.updateRule(sniffer);
			
			float personalizedPR = RuleEvaluator.getEvaluation(developer.getBestRule(sniffer.getSmell()), developer.getAnalysis().get(sniffer.getSmell()));
			float initialPR = RuleEvaluator.getEvaluation(sniffer.getInitialRule(), developer.getAnalysis().get(sniffer.getSmell()));
			System.out.println("Dev " + developer.getId() + 
					" :" + developer.getBestRule(sniffer.getSmell()).toString() + 
					" - Personalized: " + personalizedPR + 
					" - Initial: " + initialPR + 
					" - Analysis: " + developer.getAnalysis().get(sniffer.getSmell()).size());
		}
	}
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
		train(FeatureEnvySniffer.class, "data/gantt/an-fe.csv");
//		testPreferences(FeatureEnvySniffer.class, "data/xerces/an-fe.csv");
	}

}
