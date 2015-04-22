package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class GodClassKNNSniffer extends SimpleKNNSniffer {

	public static int WMC_VERY_HIGH = 47;
	public static int ATFD_FEW_THRESHOLD = 5;
	public static float TCC_ONE_THIRD_THRESHOLD = 1.0f/3.0f;
	
	public GodClassKNNSniffer() {
		super("GodClassSniffer", Smell.GodClass());
	}
	
	public Rule getInitialRule() {
		Rule rule = new Rule("God Class - Lanza & Marinecu", Smell.GodClass());
		Expression exp1 = new Expression("wmc", Operator.GreaterEqualThan(), WMC_VERY_HIGH*1.0f);
		Expression exp2 = new Expression("atfd", Operator.GreaterThan(), ATFD_FEW_THRESHOLD*1.0f);
		Expression exp3 = new Expression("tcc", Operator.LessThan(), TCC_ONE_THIRD_THRESHOLD);
		rule.getExpressions().add(exp1);
		rule.getExpressions().add(exp2);
		rule.getExpressions().add(exp3);
		return rule;
	}

}
