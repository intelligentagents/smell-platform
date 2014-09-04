package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;

public class GodClassSniffer extends Sniffer {

	private int WMC_VERY_HIGH = 47;
	private int ATFD_FEW_THRESHOLD = 5;
	private float TCC_ONE_THIRD_THRESHOLD = 1.0f / 3.0f;

	public GodClassSniffer() {
		super("GodClassSniffer", Smell.GodClass());
		this.reset();
	}
	
	public void reset() {
		Rule rule = new Rule("God Class - Lanza & Marinecu", Smell.GodClass());
		Expression exp1 = new Expression("wmc", Operator.GreaterEqualThan(), WMC_VERY_HIGH*1.0f);
		Expression exp2 = new Expression("atfd", Operator.GreaterThan(), ATFD_FEW_THRESHOLD*1.0f);
		Expression exp3 = new Expression("tcc", Operator.LessThan(), TCC_ONE_THIRD_THRESHOLD);
		rule.getExpressions().add(exp1);
		rule.getExpressions().add(exp2);
		rule.getExpressions().add(exp3);
		this.setRule(rule);
	}

}
