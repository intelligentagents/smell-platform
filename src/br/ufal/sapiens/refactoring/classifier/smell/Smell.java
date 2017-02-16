package br.ufal.sapiens.refactoring.classifier.smell;

import br.ufal.sapiens.refactoring.pr.NodeType;

public class Smell {
	private String name;
	private String shortName;
	private NodeType type;
	
	public Smell(String name, NodeType type, String shortName) {
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

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public static Smell GodClass() {
		return new Smell("God Class", NodeType.ClassDefinition, "GC");
	}
	
	public static Smell LongMethod() {
		return new Smell("Long Method", NodeType.MethodDefinition, "LM");
	}
	
	public static Smell DataClass() {
		return new Smell("Data Class", NodeType.ClassDefinition, "DCl");
	}
	
	public static Smell LongParameterList() {
		return new Smell("Long Parameter List", NodeType.MethodDefinition, "LPL");
	}
	
	public static Smell FeatureEnvy() {
		return new Smell("Feature Envy", NodeType.MethodDefinition, "FE");
	}
	
	public static Smell PrimitiveObsession() {
		return new Smell("Primitive Obsession", NodeType.ClassDefinition, "PO");
	}
	
	public static Smell SwitchStatement() {
		return new Smell("Switch Statement", NodeType.MethodDefinition, "SS");
	}
	
	public static Smell SpeculativeGenerality() {
		return new Smell("Speculative Generality", NodeType.ClassDefinition, "SG");
	}
	
	public static Smell MessageChains() {
		return new Smell("Message Chains", NodeType.MethodDefinition, "MC");
	}
	
	public static Smell MiddleMan() {
		return new Smell("Middle Man", NodeType.ClassDefinition, "MM");
	}
	
	public static Smell InappropriateIntimacy() {
		return new Smell("Inappropriate Intimacy", NodeType.ClassDefinition, "II");
	}
	
	public static Smell RefusedBequest() {
		return new Smell("Refused Bequest", NodeType.ClassDefinition, "RB");
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
		else if ("DCl".equals(shortName))
			return DataClass();
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
