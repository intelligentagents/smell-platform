package br.ufal.sapiens.refactoring.metrics.pmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.util.StringUtil;

public class IVMCMetricCalculator extends AbstractJavaRule {

	private String currentClassName = "";
	private Map<String,Integer> fields = new HashMap<String,Integer>();
	
	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		this.currentClassName = node.getImage();
		return super.visit(node, data);
	}
	
	public Object visit(ASTClassOrInterfaceBody node, Object data) {
		Object obj = super.visit(node, data);
		for (String field : this.fields.keySet()) {
			if (this.fields.get(field) <= 0)
				System.out.println(field + "::" + this.fields.get(field));
		}
		return obj;
	}
	
	public Object visit(ASTFieldDeclaration node, Object data) {
		String field = this.currentClassName + "::" + node.getVariableName();
		this.fields.put(field, 0);
		return super.visit(node, data);
	}
	
	private boolean addCallForField(String field) {
		String key = this.currentClassName + "::" + field;
		if (this.fields.containsKey(key)) {
			this.fields.put(key, this.fields.get(key) + 1);
			return true;
		}
		this.fields.put(key, 1);
		return false;
	}
	
	/**
     * The primary expression node is used to detect access to attributes and method calls.
     * If the access is not for a foreign class, then the {@link #methodAttributeAccess} map is
     * updated for the current method.
     */
    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (!isForeignAttributeOrMethod(node)) {
            if (isAttributeAccess(node)) {
                String field = this.getNameImage(node);
                this.addCallForField(field);
            } else if ((isMethodCall(node)) && (isAccessorCall(node))) {
            	String methodName = getMethodOrAttributeName(node);
            	if (StringUtil.startsWithAny(methodName, "is")) {
            		methodName = methodName.substring(2);
            	} else {
            		methodName = methodName.substring(3);
            	}
            	for (String fieldName : this.fields.keySet()) {
					if (fieldName.split("::")[1].equalsIgnoreCase(methodName)) {
						this.addCallForField(fieldName);
					}
				}
            }
        }
        
        return super.visit(node, data);
    }
    
    private boolean isAccessorCall(ASTPrimaryExpression node) {
        String methodOrAttributeName = getMethodOrAttributeName(node);
        return methodOrAttributeName != null && StringUtil.startsWithAny(methodOrAttributeName, "get","is","set");
    }


    private boolean isMethodCall(ASTPrimaryExpression node) {
        boolean result = false;
        List<ASTPrimarySuffix> suffixes = node.findDescendantsOfType(ASTPrimarySuffix.class);
        if (suffixes.size() == 1) {
            result = suffixes.get(0).isArguments();
        }
        return result;
    }

    private boolean isForeignAttributeOrMethod(ASTPrimaryExpression node) {
        boolean result = false;
        String nameImage = getNameImage(node);
        
        if (nameImage != null && (!nameImage.contains(".") || nameImage.startsWith("this."))) {
            result = false;
        } else if (nameImage == null && node.getFirstDescendantOfType(ASTPrimaryPrefix.class).usesThisModifier()) {
            result = false;
        } else if (nameImage == null && node.hasDecendantOfAnyType(ASTLiteral.class, ASTAllocationExpression.class)) {
            result = false;
        } else {
            result = true;
        }
        
        return result;
    }
    
    private String getNameImage(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String image = null;
        if (name != null) {
            image = name.getImage();
        }
        return image;
    }

//    private String getVariableName(ASTPrimaryExpression node) {
//        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
//        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);
//
//        String variableName = null;
//        
//        if (name != null) {
//            int dotIndex = name.getImage().indexOf(".");
//            if (dotIndex == -1) {
//                variableName = name.getImage();
//            } else {
//                variableName = name.getImage().substring(0, dotIndex);
//            }
//        }
//        
//        return variableName;
//    }
    
    private String getMethodOrAttributeName(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String methodOrAttributeName = null;
        
        if (name != null) {
            int dotIndex = name.getImage().indexOf(".");
            if (dotIndex > -1) {
                methodOrAttributeName = name.getImage().substring(dotIndex + 1);
            }
        }
        
        return methodOrAttributeName;
    }

//    private VariableNameDeclaration findVariableDeclaration(String variableName, Scope scope) {
//        VariableNameDeclaration result = null;
//        
//        for (VariableNameDeclaration declaration : scope.getDeclarations(VariableNameDeclaration.class).keySet()) {
//            if (declaration.getImage().equals(variableName)) {
//                result = declaration;
//                break;
//            }
//        }
//        
//        if (result == null && scope.getParent() != null && !(scope.getParent() instanceof SourceFileScope)) {
//            result = findVariableDeclaration(variableName, scope.getParent());
//        }
//        
//        return result;
//    }

    private boolean isAttributeAccess(ASTPrimaryExpression node) {
        return node.findDescendantsOfType(ASTPrimarySuffix.class).isEmpty();
    }
	
	
}
