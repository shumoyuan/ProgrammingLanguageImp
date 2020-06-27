/**
* Name£ºJue Yuan
* Assignment number£ºProject Final
* Date Due£ºDecbember 4, 2018
*/

package cop5556fa18;

import cop5556fa18.PLPTypes;
import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPTypeChecker.SemanticException;
import cop5556fa18.PLPAST.AssignmentStatement;
import cop5556fa18.PLPAST.Block;
import cop5556fa18.PLPAST.Declaration;
import cop5556fa18.PLPAST.VariableDeclaration;
import cop5556fa18.PLPAST.Expression;
import cop5556fa18.PLPAST.ExpressionBinary;
import cop5556fa18.PLPAST.ExpressionBooleanLiteral;
import cop5556fa18.PLPAST.ExpressionCharLiteral;
import cop5556fa18.PLPAST.ExpressionConditional;
import cop5556fa18.PLPAST.ExpressionFloatLiteral;
import cop5556fa18.PLPAST.ExpressionIdentifier;
import cop5556fa18.PLPAST.ExpressionIntegerLiteral;
import cop5556fa18.PLPAST.ExpressionStringLiteral;
import cop5556fa18.PLPAST.ExpressionUnary;
import cop5556fa18.PLPAST.FunctionWithArg;
import cop5556fa18.PLPAST.IfStatement;
import cop5556fa18.PLPAST.LHS;
import cop5556fa18.PLPAST.PLPASTNode;
import cop5556fa18.PLPAST.PLPASTVisitor;
import cop5556fa18.PLPAST.PrintStatement;
import cop5556fa18.PLPAST.Program;
import cop5556fa18.PLPAST.SleepStatement;
import cop5556fa18.PLPAST.VariableDeclaration;
import cop5556fa18.PLPAST.VariableListDeclaration;
import cop5556fa18.PLPAST.WhileStatement;
import cop5556fa18.PLPParser.SyntaxException;
import cop5556fa18.PLPTypes.Type;
import cop5556fa18.PLPScanner.Token;
import java.util.HashMap;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;

public class PLPCodeGen implements PLPASTVisitor, Opcodes {
	
	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	PLPScanner scanner;
	
	MethodVisitor mv; // visitor of method currently under construction
	FieldVisitor fv;

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	int timeslot = 1;
	int LayerNum = 1;
	Label cStart;
	Label cEnd;

	public PLPCodeGen(String sourceFileName, boolean dEVEL, boolean gRADE) {
		super();
		this.sourceFileName = sourceFileName;
		DEVEL = dEVEL;
		GRADE = gRADE;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		for (PLPASTNode node : block.declarationsAndStatements) {
			node.visit(this, null);
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0); 
		// If the call to mv.visitMaxs(1, 1) crashes, it is sometimes helpful 
		// to temporarily run it without COMPUTE_FRAMES. You probably won't 
		// get a completely correct classfile, but you will be able to see the 
		// code that was generated.
		
		className = program.name;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();
		
		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		PLPCodeGenUtils.genLog(DEVEL, mv, "entering main");

		program.block.visit(this, arg);

		// generates code to add string to log
		PLPCodeGenUtils.genLog(DEVEL, mv, "leaving main");
		
		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor, asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0, 0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();			
	}

	@Override
	public Object visitVariableDeclaration(VariableDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub	
		declaration.settimeslot(timeslot);
		timeslot++;
	    /*if(declaration.typeName.equals(Type.INTEGER)) {
	    	fv = cw.visitField(ACC_STATIC, declaration.name, "I", null, null);
	    }
	    else if(declaration.typeName.equals(Type.FLOAT)) {
	    	fv = cw.visitField(ACC_STATIC, declaration.name, "F", null, null);
	    }
	    else if(declaration.typeName.equals(Type.BOOLEAN)) {
	    	fv = cw.visitField(ACC_STATIC, declaration.name, "Z", null, null);
	    }
	    else if(declaration.typeName.equals(Type.CHAR)) {
	    	fv = cw.visitField(ACC_STATIC, declaration.name, "C", null, null);
	    }
	    else if(declaration.typeName.equals(Type.STRING)) {
	    	fv = cw.visitField(ACC_STATIC, declaration.name, "Ljava/lang/String;", null, null);
	    }
	    fv.visitEnd();*/
	    
		if(declaration.expression != null) {
			declaration.expression.visit(this, arg);
			//mv.visitFieldInsn(PUTSTATIC,className, declaration.name, type);
		    if(declaration.typeName.equals(Type.INTEGER)) {
		    	mv.visitVarInsn(ISTORE,declaration.gettimeslot());
		    	//mv.visitFieldInsn(PUTSTATIC,className, declaration.name, "I");
		    }
		    else if(declaration.typeName.equals(Type.FLOAT)) {
		    	mv.visitVarInsn(FSTORE,declaration.gettimeslot());
		    	//mv.visitFieldInsn(PUTSTATIC,className, declaration.name, "F");
		    }
		    else if(declaration.typeName.equals(Type.BOOLEAN)) {
		    	mv.visitVarInsn(ISTORE,declaration.gettimeslot());
		    	//mv.visitFieldInsn(PUTSTATIC,className, declaration.name, "Z");
		    }
		    else if(declaration.typeName.equals(Type.CHAR)) {
		    	mv.visitVarInsn(ISTORE,declaration.gettimeslot());
		    	//mv.visitFieldInsn(PUTSTATIC,className, declaration.name, "C");
		    }
		    else if(declaration.typeName.equals(Type.STRING)) {
		    	mv.visitVarInsn(ASTORE,declaration.gettimeslot());
		    	//mv.visitFieldInsn(PUTSTATIC,className, declaration.name, "Ljava/lang/String;");
		    }	    
		}
		
		return null;
	}

	@Override
	public Object visitVariableListDeclaration(VariableListDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		declaration.settimeslot(timeslot);
		for(String i : declaration.names) {
			declaration.MapAdd(i, timeslot);
			timeslot++;
		}

		return null;
	}

	@Override
	public Object visitExpressionBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		
		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Label lab1 = new Label();
		Label lab2 = new Label();
		Type type1 = expressionBinary.leftExpression.typeName;
		Type type2 = expressionBinary.rightExpression.typeName;
		expressionBinary.leftExpression.visit(this, arg);
		expressionBinary.rightExpression.visit(this, arg);
		if(expressionBinary.op.equals(Kind.OP_PLUS)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(IADD);
			}
			else if(type1.equals(Type.INTEGER) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FADD);
			}
			else if(type1.equals(Type.STRING) && type2.equals(Type.STRING)) {
				//mv.visitVarInsn(ASTORE,1);
				
				/*mv.visitInsn(SWAP);
				mv.visitTypeInsn(NEW, "java/lang/StringBuilder"); 
				mv.visitInsn(DUP);
				mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder","append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
				mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder","append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder","toString", "()Ljava/lang/String;", false);
				mv.visitInsn(ARETURN);*/
				
				//mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_MINUS)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(ISUB);
			}
			else if(type1.equals(Type.INTEGER) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(SWAP);
				mv.visitInsn(FSUB);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(I2F);
				mv.visitInsn(FSUB);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FSUB);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_TIMES)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(IMUL);
			}
			else if(type1.equals(Type.INTEGER) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FMUL);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_DIV)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(IDIV);
			}
			else if(type1.equals(Type.INTEGER) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(SWAP);
				mv.visitInsn(FDIV);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(I2F);
				mv.visitInsn(FDIV);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FDIV);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_POWER)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE,timeslot);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD,timeslot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2I);
				timeslot++;
			}
			else if(type1.equals(Type.INTEGER) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(F2D);
				mv.visitVarInsn(DSTORE,timeslot);
				mv.visitInsn(I2D);
				mv.visitVarInsn(DLOAD,timeslot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				timeslot++;
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.INTEGER)) {
				mv.visitInsn(I2D);
				mv.visitVarInsn(DSTORE,timeslot);
				mv.visitInsn(F2D);
				mv.visitVarInsn(DLOAD,timeslot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				timeslot++;
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(F2D);
				mv.visitVarInsn(DSTORE,timeslot);
				mv.visitInsn(F2D);
				mv.visitVarInsn(DLOAD,timeslot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				timeslot++;
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_MOD)) {
			mv.visitInsn(IREM);
		}
		else if(expressionBinary.op.equals(Kind.OP_AND)) {
			mv.visitInsn(IAND);	
		}
		else if(expressionBinary.op.equals(Kind.OP_OR)) {
			mv.visitInsn(IOR);	
		}
		else if(expressionBinary.op.equals(Kind.OP_EQ)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitJumpInsn(IF_ICMPEQ, lab1);
			}
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPEQ, lab1);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFEQ, lab1);
			}
			mv.visitLdcInsn(false);
		}
		else if(expressionBinary.op.equals(Kind.OP_NEQ)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitJumpInsn(IF_ICMPNE, lab1);
			}
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPNE, lab1);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFNE, lab1);
			}
			mv.visitLdcInsn(false);
		}
		else if(expressionBinary.op.equals(Kind.OP_GE)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitJumpInsn(IF_ICMPGE, lab1);
			}
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPGE, lab1);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGE, lab1);
			}
			mv.visitLdcInsn(false);
		}
		else if(expressionBinary.op.equals(Kind.OP_LE)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitJumpInsn(IF_ICMPLE, lab1);
			}
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPLE, lab1);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLE, lab1);
			}
			mv.visitLdcInsn(false);
		}
		else if(expressionBinary.op.equals(Kind.OP_GT)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitJumpInsn(IF_ICMPGT, lab1);
			}
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPGT, lab1);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGT, lab1);
			}
			mv.visitLdcInsn(false);
		}
		else if(expressionBinary.op.equals(Kind.OP_LT)) {
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER)) {
				mv.visitJumpInsn(IF_ICMPLT, lab1);
			}
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPLT, lab1);
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT)) {
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLT, lab1);
			}
			mv.visitLdcInsn(false);
		}
		mv.visitJumpInsn(GOTO, lab2);
		mv.visitLabel(lab1);
		mv.visitLdcInsn(true);
		mv.visitLabel(lab2);
		
		return null;
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		// TODO Auto-generated method stub
		expressionConditional.condition.visit(this, arg);
		Label truelab = new Label();
		Label falselab = new Label();
		mv.visitJumpInsn(IFNE, falselab);
		expressionConditional.falseExpression.visit(this,arg);
		mv.visitJumpInsn(GOTO,truelab);
		mv.visitLabel(falselab);
		expressionConditional.trueExpression.visit(this, arg);
		mv.visitLabel(truelab);
		
		return null;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionFloatLiteral.value);
		
		return null;
	}

	@Override
	public Object visitFunctionWithArg(FunctionWithArg FunctionWithArg, Object arg) throws Exception {
		// TODO Auto-generated method stub
		FunctionWithArg.expression.visit(this, arg);
		Type type = FunctionWithArg.expression.typeName;
		if(FunctionWithArg.functionName.equals(Kind.KW_sin)) {
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(FunctionWithArg.functionName.equals(Kind.KW_cos)) {
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(FunctionWithArg.functionName.equals(Kind.KW_atan))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(FunctionWithArg.functionName.equals(Kind.KW_log))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "log", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(FunctionWithArg.functionName.equals(Kind.KW_abs))
		{
			if(type.equals(Type.FLOAT)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false);
			}
			else if(type.equals(Type.INTEGER)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
			}
		}
		else if(FunctionWithArg.functionName.equals(Kind.KW_int))
		{
			if(type.equals(Type.FLOAT)) {
				mv.visitInsn(F2I);
			}
		}
		else if(FunctionWithArg.functionName.equals(Kind.KW_float))
		{
			if(type.equals(Type.INTEGER)) {
				mv.visitInsn(I2F);
			}
		}
		return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdentifier expressionIdent, Object arg) throws Exception {
		// TODO Auto-generated method stub
		int tmpslot=0;
		
		if(expressionIdent.getDecl().MapSearch(expressionIdent.name)!=0) {
			tmpslot = expressionIdent.getDecl().MapSearch(expressionIdent.name);
		}
		
		if(tmpslot == 0) {
			tmpslot = expressionIdent.getDecl().gettimeslot();
		}
		
		if(expressionIdent.typeName.equals(Type.INTEGER)) {
			//mv.visitFieldInsn(GETSTATIC, className, expressionIdent.name, "I");
			mv.visitVarInsn(ILOAD, tmpslot);
		}
		else if(expressionIdent.typeName.equals(Type.FLOAT)) {
			//mv.visitFieldInsn(GETSTATIC, className, expressionIdent.name, "F");
			mv.visitVarInsn(FLOAD, tmpslot);
	    }
		else if(expressionIdent.typeName.equals(Type.BOOLEAN)) {
			//mv.visitFieldInsn(GETSTATIC, className, expressionIdent.name, "Z");	
			mv.visitVarInsn(ILOAD, tmpslot);
	    }
		else if(expressionIdent.typeName.equals(Type.CHAR)) {
			//mv.visitFieldInsn(GETSTATIC, className, expressionIdent.name, "C");
			mv.visitVarInsn(ILOAD, tmpslot);
	    }
		else if(expressionIdent.typeName.equals(Type.STRING)) {
			//mv.visitFieldInsn(GETSTATIC, className, expressionIdent.name, "Ljava/lang/String;");	
			mv.visitVarInsn(ALOAD, tmpslot);
	    }
		
		return null;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionStringLiteral(ExpressionStringLiteral expressionStringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionStringLiteral.text);
		
		return null;
	}

	@Override
	public Object visitExpressionCharLiteral(ExpressionCharLiteral expressionCharLiteral, Object arg) throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionCharLiteral.text);
		
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		statementAssign.lhs.visit(this, arg);
		statementAssign.expression.visit(this, arg);
		
		int tmpslot=0;
		
		if(statementAssign.lhs.getDecl().MapSearch(statementAssign.lhs.identifier)!=0) {
			tmpslot = statementAssign.lhs.getDecl().MapSearch(statementAssign.lhs.identifier);
		}
		
		if(tmpslot == 0) {
			tmpslot = statementAssign.lhs.getDecl().gettimeslot();
		}
		//statementAssign.lhs.getDecl().settimeslot(timeslot);
		
		if(statementAssign.expression.typeName.equals(Type.INTEGER))
		{
			//mv.visitFieldInsn(PUTSTATIC, className, lhs.identifier, "I");
			mv.visitVarInsn(ISTORE, tmpslot);
		}
		else if(statementAssign.expression.typeName.equals(Type.BOOLEAN))
		{
			//mv.visitFieldInsn(PUTSTATIC, className, lhs.identifier, "Z");
			mv.visitVarInsn(ISTORE, tmpslot);
		}
		else if(statementAssign.expression.typeName.equals(Type.FLOAT))
		{
			//mv.visitFieldInsn(PUTSTATIC, className, lhs.identifier, "F");
			mv.visitVarInsn(FSTORE, tmpslot);
		}
		else if(statementAssign.expression.typeName.equals(Type.CHAR))
		{
			//mv.visitFieldInsn(PUTSTATIC, className, lhs.identifier, "C");
			mv.visitVarInsn(ISTORE, tmpslot);
		}
		else if(statementAssign.expression.typeName.equals(Type.STRING))
		{
			//mv.visitFieldInsn(PUTSTATIC, className, lhs.identifier, "LJava/lang/String;");
			mv.visitVarInsn(ASTORE, tmpslot);
		}
		
		return null;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub	
		//lhs.visit(this, arg);
		
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		ifStatement.condition.visit(this, arg);
		Label lab = new Label();
		mv.visitJumpInsn(IFEQ, lab);
		ifStatement.block.visit(this, arg);
		mv.visitLabel(lab);
		
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Label truelab = new Label();
		mv.visitJumpInsn(GOTO, truelab);
		Label endlab = new Label();
		mv.visitLabel(endlab);
		whileStatement.b.visit(this, arg);
		mv.visitLabel(truelab);
		whileStatement.condition.visit(this, arg);
		mv.visitJumpInsn(IFNE,endlab);
				
		return null;
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg) throws Exception {
		/**
		 * TODO refactor and complete implementation.
		 * 
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */
		printStatement.expression.visit(this, arg);
		Type type = printStatement.expression.typeName;
		switch (type) {
		case INTEGER : {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
					"println", "(I)V", false);
		}
		break;
		case BOOLEAN : {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
					"println", "(Z)V", false);
			// TODO implement functionality
			//throw new UnsupportedOperationException();
		}
	    break; //commented out because currently unreachable. You will need
		// it.
		case FLOAT : {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
					"println", "(F)V", false);
			// TODO implement functionality
			//throw new UnsupportedOperationException();
		}
		break; //commented out because currently unreachable. You will need
		// it.
		case CHAR : {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
					"println", "(C)V", false);
			// TODO implement functionality
			//throw new UnsupportedOperationException();
		}
		break; //commented out because currently unreachable. You will need
		// it.
		case STRING : {
			PLPCodeGenUtils.genLogTOS(GRADE, mv, type);
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitInsn(Opcodes.SWAP);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
					"println", "(Ljava/lang/String;)V", false);
			// TODO implement functionality
			//throw new UnsupportedOperationException();
		}
		break;
		default: {
			throw new Exception("Illegal Expression Type for Print\n");
		}

		}
		return null;
		
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		sleepStatement.time.visit(this,arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC,  "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type type1 = expressionUnary.expression.typeName;
		 expressionUnary.expression.visit(this, arg);
		Label label1 = new Label();
		Label label2 = new Label();
		if(expressionUnary.op.equals(Kind.OP_MINUS)) {
			if(type1.equals(Type.INTEGER)) {
				mv.visitInsn(INEG);	
			}
			else if(type1.equals(Type.FLOAT)) {
				mv.visitInsn(FNEG);	
			}
		}
		else if(expressionUnary.op.equals(Kind.OP_EXCLAMATION)) {
			if(type1.equals(Type.INTEGER)) {
				mv.visitLdcInsn(-1);
				mv.visitInsn(IXOR);
			}
			else if(type1.equals(Type.BOOLEAN)) {
				mv.visitLdcInsn(1);
				mv.visitInsn(IXOR);
			}
		}
		
		return null;
	}

}
