package br.ufal.sapiens.refactoring;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.ClassifierManager;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.classifier.sniffer.SniffedSmell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.FeatureEnvySniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.GodClassSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.LongMethodSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.LongParameterListSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.RuleEvaluator;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.NodeType;
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
	
	public List<NeighbourNode> getNeighbourNodes(Sniffer sniffer, int maxNeighbours) {
		return ClassifierManager.getInstance().getNeighbourNodes(this.project, this.developer, sniffer, maxNeighbours);
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
	
	public void updateClassifier(Sniffer sniffer) throws IOException {
		sniffer.updateClassifier(developer);
	}
	
}
