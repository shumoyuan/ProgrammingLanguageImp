/**
* Name£ºJue Yuan
* Assignment number£ºProject Final
* Date Due£ºDecbember 4, 2018
*/

package cop5556fa18;

import cop5556fa18.PLPAST.*;
import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPScanner.Token;
import java.util.HashMap;
import cop5556fa18.PLPTypes.Type;

public class PLPTypeChecker implements PLPASTVisitor {
	
	PLPTypeChecker() {
	}
	
	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}
	
	SymbolTable symTab = new SymbolTable();
	
	// Name is only used for naming the output file. 
	// Visit the child block to type check program.
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		program.block.visit(this, arg);
		return null;
	}
		
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symTab.enterScope();
		for (PLPASTNode node: block.declarationsAndStatements) {
			node.visit(this, arg);
		}
		symTab.leaveScope();
		return block;
	}

	@Override
	public Object visitVariableDeclaration(VariableDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		declaration.typeName = PLPTypes.getType(declaration.type);
		if(!symTab.insert(declaration.name, declaration)) {
			throw new SemanticException(declaration.firstToken, "Variable declared again");
		}
		if(declaration.expression != null) {
			Expression expr = (Expression) declaration.expression.visit(this, arg);
			if(declaration.typeName != expr.typeName) {
				throw new SemanticException(declaration.firstToken, "Different types for 2 sides of equal");
			}
		}
		return declaration;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitVariableListDeclaration(VariableListDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(String i : declaration.names) {
			declaration.typeName = PLPTypes.getType(declaration.type);
			if(symTab.lookup(i)!=null) {
				throw new SemanticException(declaration.firstToken, "Variable declared again");
			}else {
				symTab.insert(i, declaration);
			}
		}
		return declaration;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		expressionBooleanLiteral.typeName = Type.BOOLEAN;		
		return expressionBooleanLiteral;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		Expression lexpr = (Expression) expressionBinary.leftExpression.visit(this, arg);
		Expression rexpr = (Expression) expressionBinary.rightExpression.visit(this, arg);
		
		Kind op = expressionBinary.op;
		
		if ((op == Kind.OP_EQ||op == Kind.OP_NEQ||op == Kind.OP_GE||op == Kind.OP_GT||op == Kind.OP_LT||op == Kind.OP_LE)&&(lexpr.typeName == Type.INTEGER)&&(rexpr.typeName == Type.INTEGER)){
			expressionBinary.typeName = Type.BOOLEAN;
		} else if ((op == Kind.OP_EQ||op == Kind.OP_NEQ||op == Kind.OP_GE||op == Kind.OP_GT||op == Kind.OP_LT||op == Kind.OP_LE)&&(lexpr.typeName == Type.FLOAT)&&(rexpr.typeName == Type.FLOAT)){
			expressionBinary.typeName = Type.BOOLEAN;
		} else if ((op == Kind.OP_EQ||op == Kind.OP_NEQ||op == Kind.OP_GE||op == Kind.OP_GT||op == Kind.OP_LT||op == Kind.OP_LE)&&(lexpr.typeName == Type.BOOLEAN)&&(rexpr.typeName == Type.BOOLEAN)){
			expressionBinary.typeName = Type.BOOLEAN;
		} else if ((op == Kind.OP_AND||op == Kind.OP_OR)&&(lexpr.typeName == Type.BOOLEAN)&&(rexpr.typeName == Type.BOOLEAN) ) {
			expressionBinary.typeName = Type.BOOLEAN;
		} else if ((op == Kind.OP_AND||op == Kind.OP_OR)&&(lexpr.typeName == Type.INTEGER)&&(rexpr.typeName == Type.INTEGER) ) {
			expressionBinary.typeName = Type.INTEGER;
		} else if ((op == Kind.OP_PLUS) && (lexpr.typeName == Type.STRING)&&(rexpr.typeName == Type.STRING) ) {
			expressionBinary.typeName = Type.STRING;
		} else if ((op == Kind.OP_PLUS||op == Kind.OP_MINUS||op == Kind.OP_TIMES||op == Kind.OP_DIV||op == Kind.OP_POWER)&&(lexpr.typeName == Type.FLOAT)&&(rexpr.typeName == Type.FLOAT)){
			expressionBinary.typeName = Type.FLOAT;
		} else if ((op == Kind.OP_PLUS||op == Kind.OP_MINUS||op == Kind.OP_TIMES||op == Kind.OP_DIV||op == Kind.OP_POWER)&&(lexpr.typeName == Type.FLOAT)&&(rexpr.typeName == Type.INTEGER)){
			expressionBinary.typeName = Type.FLOAT;
		} else if ((op == Kind.OP_PLUS||op == Kind.OP_MINUS||op == Kind.OP_TIMES||op == Kind.OP_DIV||op == Kind.OP_POWER)&&(lexpr.typeName == Type.INTEGER)&&(rexpr.typeName == Type.FLOAT)){
			expressionBinary.typeName = Type.FLOAT;
		} else if ((op == Kind.OP_PLUS||op == Kind.OP_MINUS||op == Kind.OP_TIMES||op == Kind.OP_DIV||op == Kind.OP_MOD||op == Kind.OP_POWER)&&(lexpr.typeName == Type.INTEGER)&&(rexpr.typeName == Type.INTEGER)){
			expressionBinary.typeName = Type.INTEGER;
		}
		else {
			throw new SemanticException(expressionBinary.firstToken, "Wrong Format of Expression Binary.");
		}
		return expressionBinary;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		Expression e0 = (Expression) expressionConditional.condition.visit(this, arg);
		Expression e1 = (Expression) expressionConditional.trueExpression.visit(this, arg);
		Expression e2 = (Expression) expressionConditional.falseExpression.visit(this, arg);

		if(e0.typeName == Type.BOOLEAN && (e1.typeName == e2.typeName)) {			
	
		}else {
			throw new SemanticException(expressionConditional.firstToken, "Wrong Format of Expression Conditional.");
		}
		
		expressionConditional.typeName = e1.typeName;
		return expressionConditional;		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg) throws Exception {
		expressionFloatLiteral.typeName = Type.FLOAT;		
		return expressionFloatLiteral;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFunctionWithArg(FunctionWithArg FunctionWithArg, Object arg) throws Exception {
		Expression expr = (Expression) FunctionWithArg.expression.visit(this, arg);
		
		if(FunctionWithArg.functionName == Kind.KW_abs && expr.typeName == Type.INTEGER) {
			FunctionWithArg.typeName = Type.INTEGER;
		} else if((FunctionWithArg.functionName == Kind.KW_sin||FunctionWithArg.functionName == Kind.KW_cos||FunctionWithArg.functionName == Kind.KW_atan||FunctionWithArg.functionName == Kind.KW_abs||FunctionWithArg.functionName == Kind.KW_log) && expr.typeName == Type.FLOAT) {
			FunctionWithArg.typeName = Type.FLOAT;
		} else if(FunctionWithArg.functionName == Kind.KW_float && expr.typeName == Type.INTEGER) {
			FunctionWithArg.typeName = Type.FLOAT;
		} else if(FunctionWithArg.functionName == Kind.KW_float && expr.typeName == Type.FLOAT) {
			FunctionWithArg.typeName = Type.FLOAT;
		} else if(FunctionWithArg.functionName == Kind.KW_int && expr.typeName == Type.FLOAT) {
			FunctionWithArg.typeName = Type.INTEGER;
		} else if(FunctionWithArg.functionName == Kind.KW_int && expr.typeName == Type.INTEGER) {
			FunctionWithArg.typeName = Type.INTEGER;
		} 
		else {
			throw new SemanticException(FunctionWithArg.firstToken, "Wrong type name for condition of WhileStatement");
		}
		return FunctionWithArg;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdentifier expressionIdent, Object arg) throws Exception {
		if(symTab.lookup(expressionIdent.name)!=null) {
			expressionIdent.typeName = symTab.lookup(expressionIdent.name).typeName;
		}else {
			throw new SemanticException(expressionIdent.firstToken, "Illegal Typename");
		}
		
		expressionIdent.setDecl(symTab.lookup(expressionIdent.name));
		
		return expressionIdent;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg) throws Exception {
		expressionIntegerLiteral.typeName = Type.INTEGER;		
		return expressionIntegerLiteral;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionStringLiteral(ExpressionStringLiteral expressionStringLiteral, Object arg)	throws Exception {
		expressionStringLiteral.typeName = Type.STRING;		
		return expressionStringLiteral;		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionCharLiteral(ExpressionCharLiteral expressionCharLiteral, Object arg) throws Exception {
		expressionCharLiteral.typeName = Type.CHAR;		
		return expressionCharLiteral;		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws Exception {
		LHS lhs = (LHS) statementAssign.lhs.visit(this, arg);
		Expression expr = (Expression) statementAssign.expression.visit(this, arg);
		
		if(lhs.typeName != expr.typeName) {
			throw new SemanticException(statementAssign.firstToken, "Different types for 2 sides of equal");
		}
		
		return statementAssign;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Expression expr = (Expression) ifStatement.condition.visit(this, arg);
		Block block = (Block) ifStatement.block.visit(this, arg);
		
		if(expr.typeName != Type.BOOLEAN) {
			throw new SemanticException(ifStatement.firstToken, "Wrong type name for condition of IfStatement");
		}
		return ifStatement;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Expression expr = (Expression) whileStatement.condition.visit(this, arg);
		Block block = (Block) whileStatement.b.visit(this, null);
		
		if(expr.typeName != Type.BOOLEAN) {
			throw new SemanticException(whileStatement.firstToken, "Wrong type name for condition of WhileStatement");
		}
		return whileStatement;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg) throws Exception {
		Expression expr = (Expression) printStatement.expression.visit(this, arg);
		
		if(expr.typeName == Type.INTEGER||expr.typeName == Type.BOOLEAN||expr.typeName == Type.CHAR||expr.typeName == Type.STRING||expr.typeName == Type.FLOAT) {
		} else {
			throw new SemanticException(printStatement.firstToken, "Wrong type name for expression of PrintStatement");
		}
		return printStatement;
				
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		Expression expr = (Expression) sleepStatement.time.visit(this, arg);
		
		if(expr.typeName != Type.INTEGER) {
			throw new SemanticException(sleepStatement.firstToken, "Wrong type name for time of SleepStatement");
		}
		return sleepStatement;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		Expression expr = (Expression) expressionUnary.expression.visit(this, arg);		
		Kind op =  expressionUnary.op;
		
		if (op == Kind.OP_EXCLAMATION &&(expr.typeName == Type.BOOLEAN || expr.typeName == Type.INTEGER) ) {
			expressionUnary.typeName = expr.typeName;
		} else if ((op == Kind.OP_PLUS||op == Kind.OP_MINUS ) && (expr.typeName == Type.INTEGER || expr.typeName == Type.FLOAT)) {
			expressionUnary.typeName = expr.typeName;
		}else {
			throw new SemanticException(expressionUnary.firstToken, "Wrong type for ExpressionUnary");
		}
		
		return expressionUnary;
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		String name = lhs.identifier;
		if(symTab.lookup(name)!=null) {
			lhs.decl = symTab.lookup(name);
			if(lhs.decl!=null) {
				lhs.typeName = lhs.decl.typeName;
			}
			else{
				throw new SemanticException(lhs.firstToken, "LHS has not been claimed");
			}	
		}else {
			throw new SemanticException(lhs.firstToken, "LHS has not been claimed");
		}
		
		lhs.setDecl(symTab.lookup(name));
		
		return lhs;
	}

}
