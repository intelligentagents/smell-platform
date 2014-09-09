package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;

public class FeatureEnvySniffer extends Sniffer {

	private float ATFD_FEW = 5f;
	private float LAA_ONE_THIRD = 1.0f/3f;
	private float FDP_FEW = 5;

	public FeatureEnvySniffer() {
		super("FeatureEnvySniffer", Smell.FeatureEnvy());
	}
	
	public Rule getInitialRule() {
		Rule rule = new Rule("Feature Envy Rule", Smell.FeatureEnvy());
		Expression exp1 = new Expression("atfd", Operator.GreaterThan(), ATFD_FEW);
		Expression exp2 = new Expression("laa", Operator.LessThan(), LAA_ONE_THIRD);
		Expression exp3 = new Expression("fdp", Operator.LessEqualThan(), FDP_FEW);
		rule.getExpressions().add(exp1);
		rule.getExpressions().add(exp2);
		rule.getExpressions().add(exp3);
		return rule;
	}

}
