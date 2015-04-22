package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

public class Operator {
	
	private static final String GREATER_THAN = ">";
	private static final String GREATER_EQUAL_THAN = ">=";
	private static final String LESS_THAN = "<";
	private static final String LESS_EQUAL_THAN = "<=";
	
	private String type;

	public Operator(String type) {
		this.type = type;
	}
	
	public Boolean verify(Float value1, Float value2) {
		if (this.type == Operator.GREATER_THAN) return value1 > value2;
		else if (this.type == Operator.GREATER_EQUAL_THAN) return value1 >= value2;
		else if (this.type == Operator.LESS_THAN) return value1 < value2;
		else if (this.type == Operator.LESS_EQUAL_THAN) return value1 <= value2;
		return null;
	}
	
	public static Operator GreaterThan() {
		return new Operator(Operator.GREATER_THAN);
	}
	
	public static Operator GreaterEqualThan() {
		return new Operator(Operator.GREATER_EQUAL_THAN);
	}
	
	public static Operator LessThan() {
		return new Operator(Operator.LESS_THAN);
	}
	
	public static Operator LessEqualThan() {
		return new Operator(Operator.LESS_EQUAL_THAN);
	}
	
	@Override
	public String toString() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object obj) {
		Operator operator = (Operator)obj;
		return this.toString().equals(operator.toString());
	}
	
	@Override
	public int hashCode() {
		return this.type.hashCode();
	}
	
}
