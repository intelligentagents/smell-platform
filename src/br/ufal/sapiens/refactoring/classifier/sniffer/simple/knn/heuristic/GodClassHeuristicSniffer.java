package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class GodClassHeuristicSniffer extends SimpleHeuristicSniffer {

	public GodClassHeuristicSniffer() {
		super("GreaterEqualThan", Smell.GodClass());
	}
	
	public void loadRules() {
		Rule rule1 = new Rule("GC - InnerClass", this.getSmell());
		Expression exp1 = new Expression("InnerClass", Operator.GreaterEqualThan(), 3*1.0f);
		rule1.getExpressions().add(exp1);

		Rule rule2 = new Rule("GC - CountDeclMethodAll(NOM), CountLine(LOC)", this.getSmell());
		Expression exp21 = new Expression("NOM", Operator.GreaterEqualThan(), 20*1.0f);
		Expression exp22 = new Expression("LOC", Operator.GreaterEqualThan(), 500*1.0f);
		rule2.getExpressions().add(exp21);
		rule2.getExpressions().add(exp22);

		Rule rule3 = new Rule("GC - wmc,CountClassCoupled(CBO)", this.getSmell());
		Expression exp31 = new Expression("WMC", Operator.GreaterEqualThan(), 47*1.0f);
		Expression exp32 = new Expression("CBO", Operator.GreaterEqualThan(), 20*1.0f);
		rule3.getExpressions().add(exp31);
		rule3.getExpressions().add(exp32);
		
		Rule rule4 = new Rule("GC - WMC, ATFD, TCC", this.getSmell());
		Expression exp41 = new Expression("WMC", Operator.GreaterEqualThan(), 47*1.0f);
		Expression exp42 = new Expression("ATFD", Operator.GreaterThan(), 5*1.0f);
		Expression exp43 = new Expression("TCC", Operator.LessThan(), (1.0f/3.0f));
		rule4.getExpressions().add(exp41);
		rule4.getExpressions().add(exp42);
		rule4.getExpressions().add(exp43);
		
		this.addRule(rule1, 1);
		this.addRule(rule2, 2);
		this.addRule(rule3, 3);
		this.addRule(rule4, 4);
		this.setBestClassifier(rule3);
	}

}
