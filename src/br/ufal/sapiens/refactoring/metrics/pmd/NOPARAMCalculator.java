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
		int numNodes = 0;

		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Integer treeSize = (Integer) ((JavaNode) node.jjtGetChild(i))
					.jjtAccept(this, data);
			numNodes += treeSize;
		}

		if (nodeClass.isInstance(node)) {
			DataPoint point = new DataPoint();
			point.setNode(node);
			double score = 1.0 * numNodes;
			point.setScore(score);
			point.setMessage(" NOPARAM="+(int)score);
			addDataPoint(point);
		}

		return Integer.valueOf(numNodes);
	}



    
 // Count these nodes, but no others.
    public Object visit(ASTFormalParameter node, Object data) {
        return NumericConstants.ONE;
    }
    
} 

