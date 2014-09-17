package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.metrics.WekaUtil;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Statement;

public abstract class Sniffer {
	private String name;
	private Smell smell;
	
	public Sniffer(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
	}

	public List<SniffedSmell> findSmells(Project project, Developer developer, Smell smell) {
		List<SniffedSmell> sniffedSmells = new ArrayList<SniffedSmell>();
		for (Statement statement : project.getStatements(smell.getType()).values()) {
			if (this.verify(developer, statement)) {
				sniffedSmells.add(new SniffedSmell(smell,
						this, statement));
			}
		}

		return sniffedSmells;
	}
	
	public List<Statement> filterStatementsWithMetrics(Project project, Developer developer) {
		List<Statement> statements = new ArrayList<Statement>(project.getStatements(smell.getType()).values());
		List<Statement> filteredStatements = new ArrayList<Statement>();
		for (Statement statement : statements) {
			if (statement.getMetricNames().containsAll(developer.getLastRule(this.smell).getMetricNames())) {
				filteredStatements.add(statement);
			}
		}
		return filteredStatements;
	}
	
	private List<Statement> removeAnalyzedStatements(Developer developer, List<Statement> statements) {
		for (StatementAnalysis analysis : developer.getAnalysis().get(this.smell)) {
			statements.remove(analysis.getStatement());
		}
		return statements;
	}
	
	public List<NeighbourStatement> getNeighbourStatements(Project project, Developer developer, int maxNeighbours) {
		List<Statement> statements = this.filterStatementsWithMetrics(project, developer);
		statements = this.removeAnalyzedStatements(developer, statements);
		
		try {
			return WekaUtil.getNeighbourStatements(statements, developer.getLastRule(smell), maxNeighbours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NeighbourStatement>();
	}
	
	public void updateRule(Developer developer) {
		List<StatementAnalysis> allAnalysis = developer.getAnalysis().get(this.smell);
		for (StatementAnalysis analysis : allAnalysis) {
			if (analysis.isVerify() != developer.getLastRule(this.smell).verify(analysis.getStatement())) {
				Rule rule = developer.getLastRule(this.smell).update(analysis);
				if (developer.getEvaluation(rule) > developer.getEvaluation(developer.getBestRule(this.smell))) {
					rule.setName("R"+developer.getRuleMap().get(this.smell).size());
					developer.getRuleMap().get(this.smell).add(rule);
				}	
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Smell getSmell() {
		return smell;
	}

	public void setSmell(Smell smell) {
		this.smell = smell;
	}

	public boolean verify(Developer developer, Statement statement) {
		return developer.getLastRule(this.smell).verify(statement);
	}
	
	public boolean verify(Statement statement, Rule rule) {
		return rule.verify(statement);
	}
	
	public abstract Rule getInitialRule();
	
	
}
