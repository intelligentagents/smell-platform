package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.pr.Node;

public class Rule extends Classifier {
	private List<Expression> expressions = new ArrayList<Expression>();
	private int iterations = 0;
	
	public Rule(String name, Smell smell) {
		super(name, smell);
	}
	
	public Rule(Rule rule) {
		this(rule.getName(), rule.getSmell());
		this.expressions = new ArrayList<Expression>();
		for (Expression expression : rule.getExpressions()) {
			this.expressions.add(new Expression(expression.getMetricName(), expression.getOperator(), expression.getValue()));
		}
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}
	
	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public boolean verify(Node node) {
		for (Expression expression : this.getExpressions()) {
			if (!expression.verify(node)) {
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
		String result = this.getName() + ":";
		int i = 0;
		for (Expression expression : this.getExpressions()) {
			if (i != 0)
				result += " ^ "; 
			result += expression.toString();
			i++;
		}
		return result;
	}
	
	public Rule update(NodeAnalysis analysis) {
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

	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		Rule rule = (Rule)obj;
		for (int i = 0; i < this.getExpressions().size(); i++) {
			if (!this.getExpressions().get(i).equals(rule.getExpressions().get(i))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.getExpressions().toString().hashCode();
	}
	
}
