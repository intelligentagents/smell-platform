package br.ufal.sapiens.refactoring.metrics.pmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTAttributeDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.util.StringUtil;

public class ClassMetricsCalculator extends AbstractJavaRule {

    /** Collects for each method of the current class, which local attributes are accessed. */
    private String className = "";
    private int NOAM = 0;
    private int NONAM = 0;
    private int NOPA = 0;
    private int NOPA_ALL = 0;
    
    /**
     * Base entry point for the visitor - the compilation unit (everything within one file).
     * The metrics are initialized. Then the other nodes are visited. Afterwards
     * the metrics are evaluated against fixed thresholds.
     */
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
    	if (!node.isInterface()) {
            // not a top level class
            this.className = node.getImage();
            this.NOAM = 0;
            this.NONAM = 0;
            this.NOPA_ALL = 0;
            this.NOPA = 0;
            Object result = super.visit(node, data);
            StringBuilder sb = new StringBuilder();
            sb.append(this.className).append("::DCl::")
            	.append("NOAM:").append(this.NOAM)
            	.append(", NONAM:").append(this.NONAM)
            	.append(", NOPA_ALL:").append(this.NOPA_ALL)
            	.append(", NOPA:").append(this.NOPA);
            
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(new JavaRuleViolation(this, ctx, node, sb.toString()));
            return result;
    	}
    	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        String currentMethodName = node.getFirstChildOfType(ASTMethodDeclarator.class).getImage();
        if (currentMethodName.startsWith("get") || currentMethodName.startsWith("set") || currentMethodName.startsWith("is")) {
        	this.NOAM++;
        } else {
        	this.NONAM++;
        }
        return super.visit(node, data);
    }
    
    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
    	if (node.isPublic()) {
    		if (!(node.isFinal() || node.isStatic())) {
    			this.NOPA++;
    		} else {
    			this.NOPA_ALL++;
    		}
			
    	}
    	return super.visit(node, data);
    }


}
