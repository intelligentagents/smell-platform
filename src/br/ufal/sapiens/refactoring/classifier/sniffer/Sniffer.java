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
	private List<Rule> rules;
	private List<StatementAnalysis> statementAnalysis;
	private List<SniffedSmell> sniffedSmells;
	
	public Sniffer(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
		this.reset();
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
			if (statement.getMetricNames().containsAll(this.getLastRule().getMetricNames())) {
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
			return WekaUtil.getNeighbourStatements(statements, this.getLastRule(), maxNeighbours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NeighbourStatement>();
	}
	
	public void updateRule() {
		for (StatementAnalysis analysis : this.getStatementAnalysis()) {
			if (analysis.isVerify() != this.getLastRule().verifyRule(analysis.getStatement())) {
				Rule rule = this.getLastRule().update(analysis);
				if (this.getEvaluation(rule) > this.getEvaluation(this.getBestRule())) {
					rule.setName("R"+rules.size());
					this.rules.add(rule);
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

	public List<Rule> getRules() {
		return rules;
	}

	public void setRule(List<Rule> rules) {
		this.rules = rules;
	}
	
	public Rule getLastRule() {
		return this.rules.get(this.rules.size() - 1);
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
		return this.getLastRule().verifyRule(statement);
	}
	
	public boolean verify(Statement statement, Rule rule) {
		return rule.verifyRule(statement);
	}
	
	public List<Statement> getAnalyzedStatements() {
		List<Statement> statements = new ArrayList<Statement>();
		for (StatementAnalysis analysis : this.statementAnalysis) {
			statements.add(analysis.getStatement());
		}
		return statements;
	}
	
	public float getEvaluationFromInitialRule() {
		return this.getEvaluation(this.getInitialRule());
	}
	
	public float getEvaluation(Rule rule) {
		int tp = 0;
		for (StatementAnalysis analysis : this.statementAnalysis) {
			if (this.verify(analysis.getStatement(), rule) == analysis.isVerify()) {
				tp += 1;
			}
		}
		int total = this.statementAnalysis.size();
		float precision = 1.0f * tp / total;
		return precision;
	}
	
	public Rule getBestRule() {
		float precision = getEvaluationFromInitialRule();
		Rule bestRule = null;
		for (Rule rule : this.rules) {
			float rulePrecision = this.getEvaluation(rule);
			if (rulePrecision > precision)
				precision = rulePrecision;
				bestRule = rule;
		}
		return bestRule;
	}
	
	public abstract Rule getInitialRule();
	
	public void reset() {
		this.statementAnalysis = new ArrayList<StatementAnalysis>();
		this.rules = new ArrayList<Rule>(); 
		this.rules.add(this.getInitialRule());
		this.rules.get(0).setName("R0");
	}
	
}
