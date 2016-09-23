package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.pr.Node;

public class Expression {
	private String metricName;
	private Operator operator;
	private Float value;
	
	public Expression(String metricName, Operator operator, Float value) {
		this.metricName = metricName;
		this.operator = operator;
		this.value = value;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}
	
	public boolean verify(Node node) {
		Float value1 = node.getMetricValues().get(this.metricName);
		return this.operator.verify(value1, this.value);
	}
	
	@Override
	public String toString() {
		return this.metricName + " " + this.operator.toString() + " " + this.value.toString();
	}
	
	public void updateExpression(NodeAnalysis analysis) {
		if (analysis.isVerify() != this.verify(analysis.getNode())) {
			this.setValue(analysis.getNode().getMetricValues().get(this.getMetricName()));
			if (analysis.isVerify()) {
				if (Operator.GreaterThan().equals(this.operator)) {
					this.operator = Operator.GreaterEqualThan();
				}
				if (Operator.LessThan().equals(this.operator)) {
					this.operator = Operator.LessEqualThan();
				}
			} else {
				if (Operator.GreaterEqualThan().equals(this.operator)) {
					this.operator = Operator.GreaterThan();
				}
				if (Operator.LessEqualThan().equals(this.operator)) {
					this.operator = Operator.LessThan();
				}
			}
			
		}
	}
	
	public static Expression fromString(String rawExpression) {
		String op = "";
		if (rawExpression.contains(">="))
			op = ">=";
		else if (rawExpression.contains("<="))
			op = "<=";
		else if (rawExpression.contains(">"))
			op = ">";
		else if (rawExpression.contains("<"))
			op = "<";
		if (!"".equals(op)) {
			String[] parts = rawExpression.split(op);
			return new Expression(parts[0].trim(), new Operator(op), new Float(parts[1]));
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		Expression expression = (Expression)obj;
		return this.getMetricName().equals(expression.getMetricName()) &&
				this.getOperator().equals(expression.getOperator()) &&
				this.getValue().equals(expression.getValue());
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

}
