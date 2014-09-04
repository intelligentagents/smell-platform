package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.metrics.WekaUtil;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Statement;

public abstract class Sniffer {
	private String name;
	private Smell smell;
	private Rule rule;
	private List<StatementAnalysis> statementAnalysis = new ArrayList<StatementAnalysis>();
	private List<SniffedSmell> sniffedSmells;
	
	public Sniffer(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
	}

	public List<SniffedSmell> findSmells(Project project, Smell smell) {
		this.sniffedSmells = new ArrayList<SniffedSmell>();
		for (Statement statement : project.getStatements(smell.getType()).values()) {
			if (this.verify(statement)) {
				this.sniffedSmells.add(new SniffedSmell(smell,
						this, statement));
			}
		}

		return this.sniffedSmells;
	}
	
	public List<Statement> filterStatementsWithMetrics(Project project) {
		List<Statement> statements = new ArrayList<Statement>(project.getStatements(smell.getType()).values());
		List<Statement> filteredStatements = new ArrayList<Statement>();
		for (Statement statement : statements) {
			if (statement.getMetricNames().containsAll(this.rule.getMetricNames())) {
				filteredStatements.add(statement);
			}
		}
		return filteredStatements;
	}
	
	private List<Statement> removeAnalyzedStatements(List<Statement> statements) {
		for (StatementAnalysis analysis : this.statementAnalysis) {
			statements.remove(analysis.getStatement());
		}
		return statements;
	}
	
	public List<NeighbourStatement> getNeighbourStatements(Project project, int maxNeighbours) {
		List<Statement> statements = this.filterStatementsWithMetrics(project);
		statements = this.removeAnalyzedStatements(statements);
		
		try {
			return WekaUtil.getNeighbourStatements(statements, this.getRule(), maxNeighbours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NeighbourStatement>();
	}
	
	public boolean updateRule() {
		return this.getRule().updateRule(this.getStatementAnalysis());
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

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}
	
	public List<StatementAnalysis> getStatementAnalysis() {
		return statementAnalysis;
	}

	public void setStatementAnalysis(List<StatementAnalysis> statementAnalysis) {
		this.statementAnalysis = statementAnalysis;
	}

	public void addAnalysis(List<StatementAnalysis> sAnalysis) {
		this.statementAnalysis.addAll(sAnalysis);
	}
	
	public void addSingleAnalysis(StatementAnalysis analysis) {
		this.statementAnalysis.add(analysis);
	}

	public boolean verify(Statement statement) {
		return this.getRule().verifyRule(statement);
	}
	
	public List<Statement> getAnalyzedStatements() {
		List<Statement> statements = new ArrayList<Statement>();
		for (StatementAnalysis analysis : this.statementAnalysis) {
			statements.add(analysis.getStatement());
		}
		return statements;
	}
	
	public abstract void reset();
	
}
