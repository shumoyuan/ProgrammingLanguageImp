/**
* Name£ºJue Yuan
* Assignment number£ºProject Final
* Date Due£ºDecbember 4, 2018
*/

package cop5556fa18;

import cop5556fa18.PLPScanner.Token;
import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPAST.*;
import cop5556fa18.PLPScanner.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PLPParser {
	
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}
	
	PLPScanner scanner;
	Token t;

	PLPParser(PLPScanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}
	
	public Program parse() throws SyntaxException {
		Program prog = program();
		matchEOF();
		return prog;
	}
	
	/*
	 * Program -> Identifier Block
	 */
	public Program program() throws SyntaxException {
		Token first = t;
		String str = first.toString();
		int index = str.indexOf(",");
		String name = str.substring(index+1, index+1+t.length);
		//String[] strArray = str.split(",");
		//String name = strArray[1].trim();
		match(Kind.IDENTIFIER);
		Block blok = block();
		return new Program(first, name, blok);
	}
	
	/*
	 * Block ->  { (  (Declaration | Statement) ; )* }
	 */
	
	Kind[] firstDec = { Kind.KW_int, Kind.KW_boolean, Kind.KW_float, Kind.KW_char, Kind.KW_string /* Complete this */ };
	Kind[] firstStatement = { Kind.IDENTIFIER, Kind.KW_if, Kind.KW_while, Kind.KW_sleep, Kind.KW_print /* Complete this */  };

	public Block block() throws SyntaxException {
		Token first = t;
		match(Kind.LBRACE);
		ArrayList<PLPASTNode> inner = new ArrayList<PLPASTNode>();
		while (checkKind(firstDec) | checkKind(firstStatement)) {
			if (checkKind(firstDec)) {
				Declaration decl = declaration();
				inner.add(decl);
			} else if (checkKind(firstStatement)) {
				Statement stmt = statement();
				inner.add(stmt);
			}
	     	match(Kind.SEMI);
		}
		match(Kind.RBRACE);
		return new Block(first,inner);
	}
	
	public Declaration declaration() throws SyntaxException {
		Token first = t;
		consume();
		Token second = t;
		String str = second.toString();
		int index = str.indexOf(",");
		String name = str.substring(index+1, index+1+t.length);
		//String[] strArray = str.split(",");
		//String name = strArray[1].trim();
		match(Kind.IDENTIFIER);
		if(checkKind(Kind.OP_ASSIGN)) {
			consume();
			Expression e1 = expression();
			return new VariableDeclaration(first,first.kind,name,e1);
		}
		else if(checkKind(Kind.COMMA)) {
			consume();
			ArrayList<String> names = new ArrayList<String>();
			names.add(name);
			while (checkKind(Kind.IDENTIFIER)) {
				Token nam = t;
				String str2 = nam.toString();
				int index1 = str2.indexOf(",");
				String name2 = str2.substring(index1+1, index1+1+t.length);
				names.add(name2);
				consume();
				if(checkKind(Kind.IDENTIFIER)) {
					throw new SyntaxException(t,"Expected a comma at"+ t.line() + ":" + t.posInLine() + "\n");
				}
				if(checkKind(Kind.COMMA)) {
					match(Kind.COMMA);
				}
			}
			return new VariableListDeclaration(first,first.kind,names);
		}else if(!checkKind(Kind.OP_ASSIGN)||!checkKind(Kind.IDENTIFIER)){
			Expression e1 = null;
			return new VariableDeclaration(first,first.kind,name,e1);
		}	
		throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
	}
	
	/*public void identifier() throws SyntaxException {
		if(checkKind(Kind.IDENTIFIER)){
			match(Kind.IDENTIFIER);	
		} else {
			throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
		}
	}*/

	public Statement statement() throws SyntaxException{
		Token first = t;
		if(checkKind(Kind.KW_if)) {
			consume();
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			Block blok = block();
			return new IfStatement(first,e,blok);
		}
		if(checkKind(Kind.KW_while)) {
			consume();
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			Block blok = block();
			return new WhileStatement(first,e,blok);
		}
		if(checkKind(Kind.KW_sleep)) {
			consume();
			Expression e = expression();
			return new SleepStatement(first,e);
		}
		if(checkKind(Kind.KW_print)) {
			consume();
			Expression e = expression();
			return new PrintStatement(first,e);
		}
		if(checkKind(Kind.IDENTIFIER)) {
			LHS lhs = lhs();
			match(Kind.OP_ASSIGN);
			Expression e = expression();
			return new AssignmentStatement(first,lhs,e);
		}
		throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
	}
	
	public LHS lhs() throws SyntaxException {
		Token first = t;
		String str = first.toString();
		int index = str.indexOf(",");
		String name = str.substring(index+1, index+1+t.length);
		consume();		
		return new LHS(first,name);
	}
	
	public Expression expression() throws SyntaxException {
		Token first = t;
		Expression e1 = OrExpression();
		if(checkKind(Kind.OP_QUESTION)) {
			match(Kind.OP_QUESTION);
			Expression e2 = expression();
			if(checkKind(Kind.OP_COLON)) {
				match(Kind.OP_COLON);
				Expression e3 = expression();
				e1 = new ExpressionConditional(first, e1, e2, e3);
			}
			else
			{
				throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
			}
		}
		
		return e1;
	}
	
	public Expression OrExpression() throws SyntaxException {
		Token first = t;
		Expression e1 = AndExpression();
		while(checkKind(Kind.OP_OR)) {
			if(checkKind(Kind.OP_OR)) {	
				Token op = t;
				match(Kind.OP_OR);
				Expression e2 = AndExpression();
				System.out.println();
				e1 = new ExpressionBinary(first, e1, op.kind, e2);
			}
			else {
				throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
			}
		}	
		return e1;
	}

	public Expression AndExpression() throws SyntaxException {
		Token first = t;
		Expression e1 = EqExpression();
		while(checkKind(Kind.OP_AND)) {
			if(checkKind(Kind.OP_AND)) {			
				Token op = t;
				match(Kind.OP_AND);
				Expression e2 = EqExpression();
				e1 = new ExpressionBinary(first, e1, op.kind, e2);
			}
			else {
				throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
			}	
		}
		return e1;
	}	
	
	public Expression EqExpression() throws SyntaxException {
		Token first = t;
		Expression e1 = RelExpression();
		while(checkKind(Kind.OP_EQ)||checkKind(Kind.OP_NEQ)) {
			if(checkKind(Kind.OP_EQ)||checkKind(Kind.OP_NEQ)) {			
				Token op = t;
				consume();
				Expression e2 = RelExpression();
				e1 = new ExpressionBinary(first, e1, op.kind, e2);
			}
			else {
				throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
			}
		}	
		return e1;
	}	
	
	public Expression RelExpression() throws SyntaxException {
		Token first = t;
		Expression e1 = AddExpression();
		while(checkKind(Kind.OP_GT)||checkKind(Kind.OP_LT)||checkKind(Kind.OP_GE)||checkKind(Kind.OP_LE)) {
			if(checkKind(Kind.OP_GT)||checkKind(Kind.OP_LT)||checkKind(Kind.OP_GE)||checkKind(Kind.OP_LE)) {			
				Token op = t;
				consume();
				Expression e2 = AddExpression();
				e1 = new ExpressionBinary(first, e1, op.kind, e2);
			}
			else {
				throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
			}
		}	
		return e1;
	}	
	
	public Expression AddExpression() throws SyntaxException {
		Token first = t;
		Expression e1 = MultExpression();
		while(checkKind(Kind.OP_PLUS)||checkKind(Kind.OP_MINUS)) {
			if(checkKind(Kind.OP_PLUS)||checkKind(Kind.OP_MINUS)) {
				Token op = t;
				consume();
				Expression e2 = MultExpression();
				e1 = new ExpressionBinary(first, e1, op.kind, e2);
			}
			else {
				throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
			}
		}
		return e1;
	}	
	
	public Expression MultExpression() throws SyntaxException {
		Token first = t;
		Expression e1 = PowerExpression();
		while(checkKind(Kind.OP_TIMES)||checkKind(Kind.OP_DIV)||checkKind(Kind.OP_MOD)) {
			if(checkKind(Kind.OP_TIMES)||checkKind(Kind.OP_DIV)||checkKind(Kind.OP_MOD)) {			
				Token op = t;
				consume();
				Expression e2 = PowerExpression();
				e1 = new ExpressionBinary(first, e1, op.kind, e2);
			}
			else {
				throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
			}
		}
		return e1;
	}	
	
	public Expression PowerExpression() throws SyntaxException {
		Token first = t;
		Expression e1 = UnaryExpression();
		if(checkKind(Kind.OP_POWER)) {
			Token op = t;
			match(Kind.OP_POWER);
			Expression e2 = PowerExpression();
			e1 = new ExpressionBinary(first, e1, op.kind, e2);
		}
		return e1;
	}
	
	public Expression UnaryExpression() throws SyntaxException {
		Token first = t;
		if(checkKind(Kind.OP_PLUS)||checkKind(Kind.OP_MINUS)||checkKind(Kind.OP_EXCLAMATION)) {
			Token op = t;
			consume();
			Expression e = UnaryExpression();
			return new ExpressionUnary(first, op.kind, e);
		}
		if(checkKind(Kind.BOOLEAN_LITERAL)){
			String fillin = t.toString();			
			int index = fillin.indexOf(",");
			String name = fillin.substring(index+1, index+1+t.length);
			boolean result = name.equals("true");
			consume();
			return new ExpressionBooleanLiteral(first,result);
		}
		if(checkKind(Kind.CHAR_LITERAL)){
			String fillin = t.toString();
			int index = fillin.indexOf(",");
			String name = fillin.substring(index+1, index+1+t.length);
			char result = name.charAt(1);
			consume();
			return new ExpressionCharLiteral(first,result);
		}
		if(checkKind(Kind.FLOAT_LITERAL)){
			String fillin = t.toString();
			//String[] strArray = fillin.split(",");
			//String name = strArray[1].trim();
			int index = fillin.indexOf(",");
			String name = fillin.substring(index+1, index+1+t.length);
			float result = Float.parseFloat(name);
			consume();
			return new ExpressionFloatLiteral(first,result);
		}
		if(checkKind(Kind.IDENTIFIER)){
			String fillin = t.toString();
			//String[] strArray = fillin.split(",");
			//String result = strArray[1].trim();
			int index = fillin.indexOf(",");
			String result = fillin.substring(index+1, index+1+t.length);
			consume();
			return new ExpressionIdentifier(first,result);
		}
		if(checkKind(Kind.INTEGER_LITERAL)){
			String fillin = t.toString();
			//String[] strArray = fillin.split(",");
			//String name = strArray[1].trim();
			int index = fillin.indexOf(",");
			String name = fillin.substring(index+1, index+1+t.length);
			int result = Integer.parseInt(name);
			consume();
			return new ExpressionIntegerLiteral(first,result);
		}
		if(checkKind(Kind.STRING_LITERAL)){
			String fillin = t.toString();
			int index = fillin.indexOf(",");
			String result = fillin.substring(index+2, index+t.length);
			//String[] strArray = fillin.split(",");
			//String result = strArray[1].trim();
			consume();
			return new ExpressionStringLiteral(first,result);
		}
		if(checkKind(Kind.LPAREN)) {
			consume();
			Expression e = expression();
			match(Kind.RPAREN);
			return e;
		}
		if(checkKind(Kind.KW_sin)||checkKind(Kind.KW_cos)||checkKind(Kind.KW_atan)||checkKind(Kind.KW_log)||checkKind(Kind.KW_abs)||checkKind(Kind.KW_int)||checkKind(Kind.KW_float)) {
			Token name = t;
			consume();
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			return new FunctionWithArg(first, name.kind, e);
		}
		throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
	}
	
/*	public Expression Primary() throws SyntaxException {
		Expression prim = Primary();
		switch(t.kind) {
		case INTEGER_LITERAL:
		case BOOLEAN_LITERAL:
		case FLOAT_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENTIFIER:
			consume();
			break;
		case LPAREN:
			consume();
			expression();
			match(Kind.RPAREN);
			break;
		case KW_sin:
		case KW_cos:
		case KW_atan:
		case KW_log:
		case KW_abs:
		case KW_int:
		case KW_float:
			consume();
			match(Kind.LPAREN);
			expression();
			match(Kind.RPAREN);
			break;
		default:
			throw new SyntaxException(t,"Expected "+t.kind+" at " + t.line() + ":" + t.posInLine() + "\n");
		}
		return prim;
	}*/

	public Token consume() throws SyntaxException {
		if (checkKind(Kind.EOF)) {
			System.out.println("End of file"); //return t;
		}
		else {
			t = scanner.nextToken();
			return t;
		}
		String msg = "Expected " + t.kind + " at " + t.line() + ":" + t.posInLine() + "\n";
		throw new SyntaxException(t,msg);
	}
	
	protected boolean checkKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean checkKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}
	
	private Token matchEOF() throws SyntaxException {
		if (checkKind(Kind.EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}
	
	/**
	 * @param kind
	 * @return 
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (kind == Kind.EOF) {
			System.out.println("End of file"); //return t;
		}
		else if (checkKind(kind)) {
			t = scanner.nextToken();
			return t;
		}
		String msg = "Expected " + kind + " at " + t.line() + ":" + t.posInLine() + "\n";
		throw new SyntaxException(t,msg);
	}
}
