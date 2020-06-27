package cop5556fa18;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa18.PLPScanner;
import cop5556fa18.PLPTypeChecker.SemanticException;
import cop5556fa18.PLPAST.PLPASTVisitor;
import cop5556fa18.PLPAST.Program;

public class PLPTypeCheckerTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Prints objects in a way that is easy to turn on and off
	 */
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Scan, parse, and type check an input string
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		// instantiate a Scanner and scan input
		PLPScanner scanner = new PLPScanner(input).scan();
		show(scanner);
		// instantiate a Parser and parse input to obtain and AST
		Program ast = new PLPParser(scanner).parse();
		show(ast);
		// instantiate a TypeChecker and visit the ast to perform type checking and
		// decorate the AST.
		PLPASTVisitor v = new PLPTypeChecker();
		ast.visit(v, null);
	}
	
	
	@Test
	public void emptyProg() throws Exception {
		String input = "emptyProg{}";
		typeCheck(input);
	}

	@Test
	public void expression1() throws Exception {
		String input = "prog {show 1+2;}";
		typeCheck(input);
	}

	@Test
	public void expression2_fail() throws Exception {
		String input = "prog { show true+4; }"; //should throw an error due to incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression3() throws Exception {
		String input = "b{int i; char c;}";
		typeCheck(input);
	}
	
	@Test
	public void expression4() throws Exception {
		String input = "b{int i,j,k,l;}";
		typeCheck(input);
	}
	
	@Test
	public void expression5() throws Exception {
		String input = "b{char a=',';}";
		typeCheck(input);
	}
	
	@Test
	public void expression6() throws Exception {
		String input = "b{if(true) {int a = 1;};}";
		typeCheck(input);
	}
	
	@Test
	public void expression7() throws Exception {
		String input = "b{while(true) {int a = 1;};}";
		typeCheck(input);
	}
	
	@Test
	public void expression8() throws Exception {
		String input = "b{print(true);}";
		typeCheck(input);
	}
	
	@Test
	public void expression9() throws Exception {
		String input = "b{int a = 5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression10() throws Exception {
		String input = "b{int a,b,c,d; a=5;b=0.6;}";
		typeCheck(input);
	}
	
	@Test
	public void expression11() throws Exception {
		String input = "b{int a=0.5+102.2;}";
		typeCheck(input);
	}
	
	@Test
	public void expression12() throws Exception {
		String input = "b{float a=0.5+102.2;}";
		typeCheck(input);
	}
	
	@Test
	public void expression13() throws Exception {
		String input = "b{float a=0.5+102;}";
		typeCheck(input);
	}
	
	@Test
	public void expression14() throws Exception {
		String input = "b{boolean a=true|false&true;}";
		typeCheck(input);
	}
	
	@Test
	public void expression15() throws Exception {
		String input = "b{float a=float(1);}";
		typeCheck(input);
	}
	
	@Test
	public void expression16() throws Exception {
		String input = "b{float a=int(1);}";
		typeCheck(input);
	}
	
	@Test
	public void expression17() throws Exception {
		String input = "b{float a=1**0.02;}";
		typeCheck(input);
	}
	
	@Test
	public void expression18() throws Exception {
		String input = "b{float a=1**0.02;}";
		typeCheck(input);
	}
	
	@Test
	public void expression19() throws Exception {
		String input = "b{boolean a,b;a=b?true:0;}";
		typeCheck(input);
	}
	
	@Test
	public void expression20() throws Exception {
		String input = "b{boolean a,b;a=b?true:false;}";
		typeCheck(input);
	}
	
	@Test
	public void expression21() throws Exception {
		String input = "b{int a=true?1:0;}";
		typeCheck(input);
	}
	
	@Test
	public void expression23() throws Exception {
		String input = "prog { int x; int x;}";
		typeCheck(input);
	}
	
	@Test
	public void expression24() throws Exception {
		String input = "prog { if (true) { int z; z = x;}; }";
		typeCheck(input);
	}
	
	@Test
	public void expression25() throws Exception {
		String input = "p{int var; if(true) {float var; var = 5;}; var = 5;}";
		typeCheck(input);
	}
	
	@Test
	public void expression26() throws Exception {
		String input = "prog { int x; while(true) { int x; }; }";
		typeCheck(input);
	}
	
	@Test
	public void expression27() throws Exception {
		String input = "p{int var; if(true) {float var; var = 5.0;}; var = 5;}";
		typeCheck(input);
	}
}
