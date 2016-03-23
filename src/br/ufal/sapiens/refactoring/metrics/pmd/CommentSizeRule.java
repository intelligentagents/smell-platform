package br.ufal.sapiens.refactoring.metrics.pmd;

import java.beans.MethodDescriptor;
import java.lang.instrument.ClassDefinition;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.rule.comments.AbstractCommentRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.util.StringUtil;

public class CommentSizeRule extends AbstractCommentRule {

	 public static final IntegerProperty MIN_LINES = new IntegerProperty("minLines", "Minimum lines", 2, 200, 6, 2.0f);
	 public static final IntegerProperty MAX_LINES = new IntegerProperty("maxLines", "Maximum lines", 2, 200, 6, 2.0f);
	  
	 private static final String CR = "\n";
	 
	public CommentSizeRule() {
		definePropertyDescriptor(MIN_LINES);
		definePropertyDescriptor(MAX_LINES);
	}	
	
	private static boolean hasRealText(String line) {
		
		if (StringUtil.isEmpty(line)) return false;
		
		return ! StringUtil.isAnyOf(line.trim(), "//", "/*", "/**", "*", "*/");
	}
	 
	private boolean hasMuchLines(Comment comment) {

		 String[] lines = comment.getImage().split(CR);
		 
		 int start = 0;	// start from top
		 for (; start<lines.length; start++ ) {
			 if (hasRealText(lines[start])) break;
		 }
		 
		  int end = lines.length - 1;	// go up from bottom
		 for (; end>0; end-- ) {
			 if (hasRealText(lines[end])) break;
		 }
		 
		 int lineCount = end - start + 1;
		 
		 return (lineCount > getProperty(MIN_LINES)) && (lineCount < getProperty(MAX_LINES));
	 }
	 
	private String withoutCommentMarkup(String text) {
				
		return StringUtil.withoutPrefixes(text.trim(), "//", "*", "/**");
	}
	
	private List<Integer> overLengthLineIndicesIn(Comment comment) {
		List<Integer> indicies = new ArrayList<Integer>();
		String[] lines = comment.getImage().split(CR);
		
		int offset = comment.getBeginLine();
		
		for (int i=0; i<lines.length; i++) {
			String cleaned = withoutCommentMarkup(lines[i]);
		}
		
		return indicies;
	}
	
	@Override
   public Object visit(ASTCompilationUnit cUnit, Object data) {
 
		for (Comment comment : cUnit.getComments()) {
			if (hasMuchLines(comment)) {
				MethodDescriptor methodParent = comment.getFirstParentOfType(MethodDescriptor.class);
				if (methodParent == null) {
					addViolationWithMessage(data, cUnit,
							this.getMessage() + ": Much lines",
							comment.getBeginLine(), comment.getEndLine());
				}
			}
			
			List<Integer> lineNumbers = overLengthLineIndicesIn(comment);
			if (lineNumbers.isEmpty()) continue;
				
//			for (Integer lineNum : lineNumbers) {
//				addViolationWithMessage(data, cUnit,
//					this.getMessage() + ": Line too long",
//					lineNum, lineNum);
//			}
		}

       return super.visit(cUnit, data);
   }
}
