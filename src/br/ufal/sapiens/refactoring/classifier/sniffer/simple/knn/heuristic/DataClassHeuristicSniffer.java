package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class DataClassHeuristicSniffer extends SimpleHeuristicSniffer {

	public DataClassHeuristicSniffer() {
		super("GreaterEqualThan", Smell.DataClass());
	}
	
	public void loadRules() {
		Rule rule1 = new Rule("DCl - AccessorsRatio?(NOAM)", this.getSmell());
		Expression exp1 = new Expression("NOAM", Operator.GreaterEqualThan(),
				70 * 1.0f);
		rule1.getExpressions().add(exp1);

		Rule rule2 = new Rule("DCl - IntelligentMethods(NOMNAMM)", this.getSmell());
		Expression exp2 = new Expression("NOMNAMM", Operator.LessEqualThan(),
				2 * 1.0f);
		rule2.getExpressions().add(exp2);

		Rule rule3 = new Rule("DCl - AccessorsRatio?(NOAM) + PublicAttributes(NOPA)", this.getSmell());
		Expression exp31 = new Expression("NOAM", Operator.GreaterEqualThan(),
				50 * 1.0f);
		Expression exp32 = new Expression("NOPA", Operator.GreaterThan(),
				2 * 1.0f);
		rule3.getExpressions().add(exp31);
		rule3.getExpressions().add(exp32);
		
		Rule rule4 = new Rule("DCl - CountClassCoupled(CBO)", this.getSmell());
		Expression exp4 = new Expression("CountClassCoupled", Operator.GreaterEqualThan(),
				7 * 1.0f);
		rule4.getExpressions().add(exp4);
		
		this.addRule(rule1, 1);
		this.addRule(rule2, 2);
		this.addRule(rule3, 3);
		this.addRule(rule4, 4);
		this.setBestClassifier(rule4);
	}

}
