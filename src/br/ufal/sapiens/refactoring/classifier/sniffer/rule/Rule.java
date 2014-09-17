package br.ufal.sapiens.refactoring.classifier.sniffer.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.pr.Statement;

public class Rule {
	private String name;
	private List<Expression> expressions = new ArrayList<Expression>();
	private Smell smell;
	
	public Rule(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
	}
	
	public Rule(Rule rule) {
		this.expressions = new ArrayList<Expression>();
		this.name = rule.name;
		this.smell = rule.smell;
		for (Expression expression : rule.getExpressions()) {
			this.expressions.add(new Expression(expression.getMetricName(), expression.getOperator(), expression.getValue()));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}
	
	public Smell getSmell() {
		return smell;
	}

	public void setSmell(Smell smell) {
		this.smell = smell;
	}
	
	public boolean verify(Statement statement) {
		for (Expression expression : this.getExpressions()) {
			if (!expression.verify(statement)) {
				return false;
			}
		}
		return true;
	}
	
	public List<String> getMetricNames() {
		List<String> metricNames = new ArrayList<String>();
		for (Expression expression : this.getExpressions()) {
			metricNames.add(expression.getMetricName());
		}
		return metricNames;
	}
	
	public Map<String,Float> getMetricThresholds() {
		Map<String,Float> metricThresholds = new HashMap<String,Float>();
		for (Expression expression : this.getExpressions()) {
			metricThresholds.put(expression.getMetricName(), expression.getValue());
		}
		return metricThresholds;
	}
	
	@Override
	public String toString() {
		String result = this.name + ":";
		int i = 0;
		for (Expression expression : this.getExpressions()) {
			if (i != 0)
				result += " ^ "; 
			result += expression.toString();
			i++;
		}
		return result;
	}
	
	public Rule update(StatementAnalysis analysis) {
		Rule newRule = new Rule(this);
		for (Expression expression : newRule.getExpressions()) {
			expression.updateExpression(analysis);
		}
		return newRule;
	}
	
	public static Rule fromString(Smell smell, String rawRule) {
		Rule rule = new Rule("R0", smell);
		String[] parts = rawRule.split("\\^");
		for (String part : parts) {
			rule.getExpressions().add(Expression.fromString(part));
		}
		return rule;
	}
	
}
