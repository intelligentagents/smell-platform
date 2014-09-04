package br.ufal.sapiens.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.classifier.ClassifierManager;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.FeatureEnvySniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.LongMethodSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourStatement;
import br.ufal.sapiens.refactoring.classifier.sniffer.SniffedSmell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Statement;
import br.ufal.sapiens.refactoring.pr.StatementType;
import br.ufal.sapiens.refactoring.util.FileUtil;

public class SmellPlatform {
	
	private Project project;
	private int MAX_ITERACTIONS_COUNT = 6;
	private int MAX_NEIGHBOURS_COUNT = 1;
	
	public SmellPlatform(Project project) {
		this.project = project;
	}
	
	public List<SniffedSmell> findSmells(Sniffer sniffer) {
		return ClassifierManager.getInstance().findSmells(this.project, sniffer);
	}
	
	public List<NeighbourStatement> getNeighbourStatements(Sniffer sniffer, int maxNeighbours) {
		return ClassifierManager.getInstance().getNeighbourStatements(this.project, sniffer, maxNeighbours);
	}
	
	public void run(Sniffer sniffer) {
		for (int i = 0; i < MAX_ITERACTIONS_COUNT; i++) {
			List<NeighbourStatement> neighbours = this.getNeighbourStatements(sniffer, MAX_NEIGHBOURS_COUNT);
			for (NeighbourStatement neighbourStatement : neighbours) {
				String message = "Rule: " + sniffer.getRule().toString() + "\n";
				message += neighbourStatement.toString();
				int result = JOptionPane.showConfirmDialog(null, message, null, JOptionPane.YES_NO_OPTION);
				boolean verified = false;
				if(result == JOptionPane.YES_OPTION) {
				    verified = true;
				} 
				sniffer.addSingleAnalysis(new StatementAnalysis(neighbourStatement, verified));
			}
		}
	}
	
	public void getRulesFromAnalysis(Sniffer sniffer, String analysisSource) throws IOException {
		Map<String,List<StatementAnalysis>> rules = new HashMap<String,List<StatementAnalysis>>();
		List<String[]> data = FileUtil.getCSVData(analysisSource);
		System.out.println("Initial rule: " + sniffer.getRule().toString());
		for (int i = 0; i < data.size(); i++) {
			String user = data.get(i)[0];
			if (!rules.containsKey(user))
				rules.put(user, new ArrayList<StatementAnalysis>());
			Statement statement = project.getStatementFromName(data.get(i)[1]);
			if (statement == null) {
				System.out.println("Statement not found: " + data.get(i)[1]);
			} else {
				boolean verify = new Boolean(data.get(i)[2]);
				rules.get(user).add(new StatementAnalysis(statement, sniffer.getSmell(), verify));
				
			}
		}
		for (String user : rules.keySet()) {
			sniffer.reset();
			sniffer.setStatementAnalysis(rules.get(user));
			sniffer.updateRule();
			System.out.println("Rule for User " + user + " :" + sniffer.getRule().toString());
		}
	}
	
	public static void main(String[] args) throws IOException {
		Project project = new Project("GanttProject", "path");
		project.addStatementsFromCSV("gantt-gc.csv", StatementType.ClassDefinition);
		project.addStatementsFromCSV("gantt-lm.csv", StatementType.MethodDefinition);
		project.addStatementsFromCSV("gantt-fe.csv", StatementType.MethodDefinition);
		project.addStatementsFromCSV("gantt-lpl.csv", StatementType.MethodDefinition);
		
		SmellPlatform platform = new SmellPlatform(project);
		Sniffer sniffer = new FeatureEnvySniffer();
//		platform.run(sniffer);
		platform.getRulesFromAnalysis(sniffer, "an-fe.csv");
		
	}
}
