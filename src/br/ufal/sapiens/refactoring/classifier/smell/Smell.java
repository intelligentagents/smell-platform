package br.ufal.sapiens.refactoring.classifier.smell;

import br.ufal.sapiens.refactoring.pr.StatementType;

public class Smell {
	private String name;
	private String shortName;
	private StatementType type;
	
	public Smell(String name, StatementType type, String shortName) {
		this.name = name;
		this.shortName = shortName;
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
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public static Smell GodClass() {
		return new Smell("God Class", StatementType.ClassDefinition, "GC");
	}
	
	public static Smell LongMethod() {
		return new Smell("Long Method", StatementType.MethodDefinition, "LM");
	}
	
	public static Smell LongParameterList() {
		return new Smell("Long Parameter List", StatementType.MethodDefinition, "LPL");
	}
	
	public static Smell FeatureEnvy() {
		return new Smell("Feature Envy", StatementType.MethodDefinition, "FE");
	}
	
	public static Smell fromShortName(String shortName){
		if ("FE".equals(shortName))
			return FeatureEnvy();
		else if ("GC".equals(shortName))
			return GodClass();
		else if ("LPL".equals(shortName))
			return LongParameterList();
		else if ("LM".equals(shortName))
			return LongMethod();
		else
			return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		Smell aSmell = (Smell)obj;
		if (aSmell.getName().equals(this.name) && aSmell.shortName.equals(this.shortName) && aSmell.type.equals(this.type))
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode() + this.shortName.hashCode() + this.type.hashCode();
	}
	
}
