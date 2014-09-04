package br.ufal.sapiens.refactoring.metrics.pmd;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule;
import net.sourceforge.pmd.stat.DataPoint;

public class MLOCCalculator extends AbstractStatisticalJavaRule {
    private Class<?> nodeClass;
    
    public MLOCCalculator() {
        this(ASTMethodDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 0d);
    }

    public MLOCCalculator(Class<?> nodeClass) {
    	this.nodeClass = nodeClass;
    }

    @Override
    public Object visit(JavaNode node, Object data) {
	if (nodeClass.isInstance(node)) {
	    DataPoint point = new DataPoint();
	    point.setNode(node);
	    ASTMethodDeclaration method = (ASTMethodDeclaration)node;
	    double score = 1.0 * (node.getEndLine() - node.getBeginLine());
	    point.setScore(score);
	    point.setMessage(" MLOC="+(int)score);
	    addDataPoint(point);
	}

	return node.childrenAccept(this, data);
    }
    
}
