package br.ufal.sapiens.refactoring.classifier.sniffer.rule;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.pr.Statement;

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
	
	public boolean verify(Statement statement) {
		Float value1 = statement.getMetricValues().get(this.metricName);
		return this.operator.verify(value1, this.value);
	}
	
	@Override
	public String toString() {
		return this.metricName + " " + this.operator.toString() + " " + this.value.toString();
	}
	
	public void updateExpression(StatementAnalysis analysis) {
		if (analysis.isVerify() != this.verify(analysis.getStatement())) {
			this.setValue(analysis.getStatement().getMetricValues().get(this.getMetricName()));
			if (analysis.isVerify()) {
				if (Operator.GreaterThan() == this.operator) this.operator = Operator.GreaterEqualThan();
				if (Operator.LessThan() == this.operator) this.operator = Operator.LessEqualThan();
			} else {
				if (Operator.GreaterEqualThan() == this.operator) this.operator = Operator.GreaterThan();
				if (Operator.LessEqualThan() == this.operator) this.operator = Operator.LessThan();
			}
			
		}
	}

}
