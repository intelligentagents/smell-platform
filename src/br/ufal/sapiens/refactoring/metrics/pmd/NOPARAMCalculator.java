package br.ufal.sapiens.refactoring.metrics.pmd;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

public class NOPARAMCalculator extends AbstractStatisticalJavaRule {
    private Class<?> nodeClass;
    
    public NOPARAMCalculator() {
        this(ASTFormalParameters.class);
        setProperty(MINIMUM_DESCRIPTOR, 0d);
    }

    public NOPARAMCalculator(Class<?> nodeClass) {
    	this.nodeClass = nodeClass;
    }

    @Override
    public Object visit(JavaNode node, Object data) {
	if (nodeClass.isInstance(node)) {
	    DataPoint point = new DataPoint();
	    point.setNode(node);
	    ASTFormalParameters method = (ASTFormalParameters)node;
	    double score = 1.0 * (node.getEndLine() - node.getBeginLine());
	    point.setScore(score);
	    point.setMessage(" NOPARAM="+(int)score);
	    addDataPoint(point);
	}

	return node.childrenAccept(this, data);
    }
    
} 

