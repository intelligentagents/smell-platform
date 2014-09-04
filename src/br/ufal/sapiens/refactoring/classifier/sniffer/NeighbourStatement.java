package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.pr.Statement;

public class NeighbourStatement {
	private Statement statement;
	private Rule rule;
	private double distance;

	public NeighbourStatement(Statement statement, Rule rule, double distance) {
		this.statement = statement;
		this.rule = rule;
		this.distance = distance;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}
	
	@Override
	public String toString() {
		return this.statement.toString() + " - Distance: " + this.distance;
	}

}
