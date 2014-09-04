package br.ufal.sapiens.refactoring.classifier.smell;

import br.ufal.sapiens.refactoring.pr.StatementType;

public class Smell {
	private String name;
	private StatementType type;
	
	public Smell(String name, StatementType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StatementType getType() {
		return type;
	}

	public void setType(StatementType type) {
		this.type = type;
	}
	
	public static Smell GodClass() {
		return new Smell("God Class", StatementType.ClassDefinition);
	}
	
	public static Smell LongMethod() {
		return new Smell("Long Method", StatementType.MethodDefinition);
	}
	
	public static Smell LongParameterList() {
		return new Smell("Long Parameter List", StatementType.MethodDefinition);
	}
	
	public static Smell FeatureEnvy() {
		return new Smell("Feature Envy", StatementType.MethodDefinition);
	}
	
}
