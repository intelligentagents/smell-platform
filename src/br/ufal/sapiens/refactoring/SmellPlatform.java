package br.ufal.sapiens.refactoring;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.classifier.ClassifierManager;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.FeatureEnvySniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.GodClassSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.LongMethodSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourStatement;
import br.ufal.sapiens.refactoring.classifier.sniffer.SniffedSmell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Statement;
import br.ufal.sapiens.refactoring.pr.StatementType;
import br.ufal.sapiens.refactoring.util.FileUtil;

public class SmellPlatform {
	
	private Project project;
	private Developer developer;
	
	public SmellPlatform(Project project, Developer developer) {
		this.project = project;
		this.developer = developer;
	}
	
	public List<SniffedSmell> findSmells(Sniffer sniffer) {
		return ClassifierManager.getInstance().findSmells(this.project, this.developer, sniffer);
	}
	
	public List<NeighbourStatement> getNeighbourStatements(Sniffer sniffer, int maxNeighbours) {
		return ClassifierManager.getInstance().getNeighbourStatements(this.project, this.developer, sniffer, maxNeighbours);
	}
	
//	public void run(Sniffer sniffer) {
//		for (int i = 0; i < MAX_ITERACTIONS_COUNT; i++) {
//			List<NeighbourStatement> neighbours = this.getNeighbourStatements(sniffer, MAX_NEIGHBOURS_COUNT);
//			for (NeighbourStatement neighbourStatement : neighbours) {
//				String message = "Rule: " + sniffer.getLastRule().toString() + "\n";
//				message += neighbourStatement.toString();
//				int result = JOptionPane.showConfirmDialog(null, message, null, JOptionPane.YES_NO_OPTION);
//				boolean verified = false;
//				if(result == JOptionPane.YES_OPTION) {
//				    verified = true;
//				} 
//				sniffer.addSingleAnalysis(new StatementAnalysis(neighbourStatement, verified));
//			}
//		}
//	}
	
	public void updateRule(Sniffer sniffer) throws IOException {
		sniffer.updateRule(developer);
	}
	
	public static void loadAnalysis(Sniffer sniffer, String analysisSource, Project project, Developer developer) throws IOException {
		List<String[]> data = FileUtil.getCSVData(analysisSource);
		System.out.println("Initial rule: " + sniffer.getInitialRule().toString());
		for (int i = 0; i < data.size(); i++) {
			Statement statement = project.getStatementFromName(data.get(i)[1]);
			if (statement == null) {
				System.out.println("Statement not found: " + data.get(i)[1]);
			} else if (!statement.getMetricNames().containsAll(developer.getLastRule(sniffer.getSmell()).getMetricNames())) {
				System.out.println("Statement with insufficient metrics: " + statement);
			} else {
				boolean verify = ("1".equals(data.get(i)[2])) ? true : false;
				if (new Integer(data.get(i)[0]) == developer.getId()) {
					StatementAnalysis analysis = new StatementAnalysis(statement, sniffer.getSmell(), verify);
					developer.addAnalysis(analysis);
					if (developer.getId() == 2) System.out.println(analysis);
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
		float precision = developer.getEvaluation(developer.getBestRule(sniffer.getSmell()));
		float oldPrecision = developer.getEvaluation(sniffer.getInitialRule());
		System.out.println("Dev " + developer.getId() + " :" + 
				developer.getBestRule(sniffer.getSmell()).toString() + 
				" - Precision: " + precision + " - Before: " + oldPrecision + 
				" - Analysis: " + developer.getAnalysis().get(sniffer.getSmell()).size());
	}
	
	public static void testPreferences() throws IOException {
		Project project = new Project("Xerces", "path");
		project.addStatementsFromCSV("data/xerces/xerces-gc.csv", StatementType.ClassDefinition);
		project.addStatementsFromCSV("data/xerces/xerces-lpl.csv", StatementType.MethodDefinition);
		project.addStatementsFromCSV("data/xerces/xerces-lm.csv", StatementType.MethodDefinition);
		project.addStatementsFromCSV("data/xerces/xerces-fe.csv", StatementType.MethodDefinition);
		
		Map<Integer,Developer> developers = loadDevelopers("data/xerces/developers.txt");

		for (Developer developer : developers.values()) {
			Sniffer sniffer = new FeatureEnvySniffer();
			developer.addRule(sniffer.getInitialRule());
			loadAnalysis(sniffer, "data/xerces/an-fe.csv", project, developer);
			loadPreferences("data/xerces/preferences.txt", developers);
			testDeveloperPreferences(project, sniffer, developer);
		}
	}
	
	public static void train() throws IOException {
		Project project = new Project("GanttProject", "path");
		project.addStatementsFromCSV("data/gantt/gantt-gc.csv", StatementType.ClassDefinition);
		project.addStatementsFromCSV("data/gantt/gantt-lpl.csv", StatementType.MethodDefinition);
		project.addStatementsFromCSV("data/gantt/gantt-lm.csv", StatementType.MethodDefinition);
		project.addStatementsFromCSV("data/gantt/gantt-fe.csv", StatementType.MethodDefinition);
		Map<Integer,Developer> developers = loadDevelopers("data/gantt/developers.txt");
		
		for (Developer developer : developers.values()) {
			Sniffer sniffer = new FeatureEnvySniffer();
			developer.addRule(sniffer.getInitialRule());
			loadAnalysis(sniffer, "data/gantt/an-fe.csv", project, developer);
			SmellPlatform platform = new SmellPlatform(project, developer);
			platform.updateRule(sniffer);
			System.out.println("Dev " + developer.getId() + 
					" :" + developer.getBestRule(sniffer.getSmell()).toString() + 
					" - Precision: " + developer.getEvaluation(developer.getBestRule(sniffer.getSmell())) + 
					" - Before: " + developer.getEvaluation(sniffer.getInitialRule()) + 
					" - Analysis: " + developer.getAnalysis().get(sniffer.getSmell()).size());
		}
	}
	
	public static void main(String[] args) throws IOException {
//		train();
		testPreferences();
	}
}
