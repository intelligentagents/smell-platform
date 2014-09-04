package br.ufal.sapiens.refactoring.metrics.pmd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.util.StringUtil;

public class FeatureEnvyMetricsCalculator extends AbstractJavaRule {



	/**
	 * Collects for each method of the current class, which local attributes are
	 * accessed.
	 */
	private List<String> externalAccesses;
	private List<String> internalAccesses;
	/** The name of the current method. */
	private String currentMethodName;
	
	/**
	 * The primary expression node is used to detect access to attributes and
	 * method calls. If the access is not for a foreign class, then the
	 * {@link #externalAccesses} map is updated for the current method.
	 */
	@Override
	public Object visit(ASTPrimaryExpression node, Object data) {
		if ((currentMethodName != null)) {
			List<MethodCall> calls = MethodCall.createMethodCalls(node);
			for (MethodCall methodCall : calls) {
				if (methodCall.isGetterSetter()) {
					if (methodCall.isForeign()) {
						externalAccesses.add("METHOD:"+ methodCall.baseTypeName + "." + methodCall.methodName);
					} else {
						internalAccesses.add("METHOD:"+ methodCall.baseTypeName + "." + methodCall.methodName);
					}
				}
			}
			
			if (isAttributeAccess(node)) {
				if (isForeignAttributeOrMethod(node)) {
					externalAccesses.add("ATTR:" + getNameImage(node));
				} else {
					internalAccesses.add("ATTR:" + getNameImage(node));
				}
			}
		}
		
//		if (currentMethodName != null) {
//			if (isAttributeAccess(node)
//					|| (isMethodCall(node) && isForeignGetterSetterCall(node))) {
//				String variableName = getVariableName(node);
//				VariableNameDeclaration variableDeclaration = findVariableDeclaration(
//						variableName,
//						node.getScope().getEnclosingScope(ClassScope.class));
//				String typeImage = "";
//				if (variableDeclaration != null) {
//					typeImage = variableDeclaration.getTypeImage() + ".";
//				}
//				String accessName = typeImage + getNameImage(node);
//				if (isForeignAttributeOrMethod(node)) {
////					externalAccesses.add(accessName);
//					externalProviders.add(getProviderName(node, accessName));
//
//				} else {
//					internalAccesses.add(accessName);
//				}
//			}
//
//		}
		return super.visit(node, data);
	}

//	private boolean isForeignGetterSetterCall(ASTPrimaryExpression node) {
//
//		String methodOrAttributeName = getMethodOrAttributeName(node);
//
//		return methodOrAttributeName != null
//				&& StringUtil.startsWithAny(methodOrAttributeName, "get", "is",
//						"set");
//	}
	
//	private boolean isMethodCall(ASTPrimaryExpression node) {
//		boolean result = false;
//		List<ASTPrimarySuffix> suffixes = node
//				.findDescendantsOfType(ASTPrimarySuffix.class);
//		if (suffixes.size() == 1) {
//			result = suffixes.get(0).isArguments();
//		}
//		return result;
//	}

	private boolean isForeignAttributeOrMethod(ASTPrimaryExpression node) {
		boolean result = false;
		String nameImage = getNameImage(node);

		if (nameImage != null
				&& (!nameImage.contains(".") || nameImage.startsWith("this."))) {
			result = false;
		} else if (nameImage == null
				&& node.getFirstDescendantOfType(ASTPrimaryPrefix.class)
						.usesThisModifier()) {
			result = false;
		} else if (nameImage == null
				&& node.hasDecendantOfAnyType(ASTLiteral.class,
						ASTAllocationExpression.class)) {
			result = false;
		} else {
			result = true;
		}

		return result;
	}

	private String getNameImage(ASTPrimaryExpression node) {
		ASTPrimaryPrefix prefix = node
				.getFirstDescendantOfType(ASTPrimaryPrefix.class);
		ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

		String image = null;
		if (name != null) {
			image = name.getImage();
		}
		return image;
	}

//	private String getVariableName(ASTPrimaryExpression node) {
//		ASTPrimaryPrefix prefix = node
//				.getFirstDescendantOfType(ASTPrimaryPrefix.class);
//		ASTName name = prefix.getFirstDescendantOfType(ASTName.class);
//
//		String variableName = null;
//
//		if (name != null) {
//			int dotIndex = name.getImage().indexOf(".");
//			if (dotIndex == -1) {
//				variableName = name.getImage();
//			} else {
//				variableName = name.getImage().substring(0, dotIndex);
//			}
//		}
//
//		return variableName;
//	}
	
//	private String getMethodOrAttributeName(ASTPrimaryExpression node) {
//		ASTPrimaryPrefix prefix = node
//				.getFirstDescendantOfType(ASTPrimaryPrefix.class);
//		ASTName name = prefix.getFirstDescendantOfType(ASTName.class);
//
//		String methodOrAttributeName = null;
//
//		if (name != null) {
//			int dotIndex = name.getImage().indexOf(".");
//			if (dotIndex > -1) {
//				methodOrAttributeName = name.getImage().substring(dotIndex + 1);
//			}
//		}
//
//		return methodOrAttributeName;
//	}

//	private VariableNameDeclaration findVariableDeclaration(
//			String variableName, Scope scope) {
//		VariableNameDeclaration result = null;
//
//		for (VariableNameDeclaration declaration : scope.getDeclarations(
//				VariableNameDeclaration.class).keySet()) {
//			if (declaration.getImage().equals(variableName)) {
//				result = declaration;
//				break;
//			}
//		}
//
//		if (result == null && scope.getParent() != null
//				&& !(scope.getParent() instanceof SourceFileScope)) {
//			result = findVariableDeclaration(variableName, scope.getParent());
//		}
//
//		return result;
//	}

	private boolean isAttributeAccess(ASTPrimaryExpression node) {
		return node.findDescendantsOfType(ASTPrimarySuffix.class).isEmpty();
	}
	
	private List<String> getProviders(List<String> accesses) {
		List<String> providers = new ArrayList<String>();
		for (String access : accesses) {
			String part = access;
			if (access != null && access.contains(":")) {
				part = access.split(":")[1];
				if (part.contains(".")) {
					providers.add(part.split("\\.")[0]);
				}
			}
		}
		return providers;
	}
	
	private boolean testFeatureEnvy(int atfd, int fdp, float laa) {
		return ((atfd > 5) && (laa < 0.33) && (fdp <= 3));
	}

	@Override
	public Object visit(ASTMethodDeclaration node, Object data) {
		externalAccesses = new ArrayList<String>();
		internalAccesses = new ArrayList<String>();
		currentMethodName = node.getFirstChildOfType(ASTMethodDeclarator.class)
				.getImage();

		Object result = null;
		if (null != currentMethodName) {
			result = super.visit(node, data);
			List<String> externalProviders = getProviders(externalAccesses);
			int atfd = new HashSet<String>(externalAccesses).size();
			int atid = new HashSet<String>(internalAccesses).size(); // 
			int fdp = new HashSet<String>(externalProviders).size();
			
			float laa = -1;
			if ((atfd + atid) != 0)
				laa = atid * 1.0f	/ (atfd + atid);

//			if ((atfd > 5) && (laa < 0.33) && (fdp <= 3)) {
			if (true) {
				StringBuilder sb = new StringBuilder();
				sb.append(getMessage());
				sb.append("::").append(atfd)
				.append(",").append(laa)
				.append(",").append(fdp);

				RuleContext ctx = (RuleContext) data;
				ctx.getReport().addRuleViolation(
						new JavaRuleViolation(this, ctx, node, sb.toString()));
			}
		}

		currentMethodName = null;

		return result;
	}
}
