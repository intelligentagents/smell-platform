package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class PrimitiveObsessionHeuristicSniffer extends SimpleHeuristicSniffer {

	public static int MLOC_HIGH = 100;
	public static int CC_HIGH = 3;

	public PrimitiveObsessionHeuristicSniffer() {
		super("PrimitiveObsessionSniffer", Smell.PrimitiveObsession());
	}
	
	public void loadRules() {
		Rule rule1 = new Rule("PO - Primitives", Smell.PrimitiveObsession());
		Expression exp1 = new Expression("Primitives", Operator.GreaterEqualThan(),
				20 * 1.0f);
		rule1.getExpressions().add(exp1);

		Rule rule2 = new Rule("PO - Primitives, Constants", Smell.PrimitiveObsession());
		Expression exp21 = new Expression("Primitives", Operator.GreaterEqualThan(),
				20 * 1.0f);
		Expression exp22 = new Expression("Constants", Operator.LessEqualThan(),
				5 * 1.0f);
		rule2.getExpressions().add(exp21);
		rule2.getExpressions().add(exp22);

		Rule rule3 = new Rule("PO - Primitives,PercentLackOfCohesion(LCOM)", Smell.PrimitiveObsession());
		Expression exp31 = new Expression("Primitives", Operator.GreaterEqualThan(),
				20 * 1.0f);
		Expression exp32 = new Expression("LCOM", Operator.GreaterEqualThan(),
				20 * 1.0f);
		rule3.getExpressions().add(exp31);
		rule3.getExpressions().add(exp32);
		
		Rule rule4 = new Rule("PO - GroupedVariables", Smell.PrimitiveObsession());
		Expression exp4 = new Expression("GroupedVariables", Operator.GreaterEqualThan(),
				1 * 1.0f);
		rule4.getExpressions().add(exp4);
		
		this.addRule(rule1, 2);
		this.addRule(rule2, 1);
		this.addRule(rule3, 3);
		this.addRule(rule4, 4);
		this.setBestClassifier(rule4);
	}


}
