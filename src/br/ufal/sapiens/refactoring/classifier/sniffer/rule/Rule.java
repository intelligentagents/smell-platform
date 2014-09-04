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

	public boolean verifyRule(Statement statement) {
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
		String result = "";
		for (Expression expression : this.getExpressions()) {
			if (!"".equals(result))
				result += " ^ "; 
			result += expression.toString();
		}
		return result;
	}
	
	private void updateRule(StatementAnalysis anl) {
		if (anl.isVerify()) {
			System.out.println("updateExpression");
			for (Expression expression : this.getExpressions()) {
				expression.updateExpression(anl);
			}
		} else {
			for (Expression expression : this.getExpressions()) {
				expression.updateExpression(anl);
			}
		}
	}
	
	public boolean updateRule(List<StatementAnalysis> analysis) {
		boolean updated = false; 
		for (StatementAnalysis anl : analysis) {
			if (this.verifyRule(anl.getStatement()) != anl.isVerify()) {
				this.updateRule(anl);
				updated = true;
			}
		}
		return updated;
	}
	
}
