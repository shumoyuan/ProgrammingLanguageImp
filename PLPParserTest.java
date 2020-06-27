package cop5556fa18;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa18.PLPScanner;
import cop5556fa18.PLPParser;
import cop5556fa18.PLPScanner.LexicalException;
import cop5556fa18.PLPParser.SyntaxException;

public class PLPParserTest {
	
	//set Junit to be able to catch exceptions
		@Rule
		public ExpectedException thrown = ExpectedException.none();

		
		//To make it easy to print objects and turn this output on and off
		static final boolean doPrint = true;
		private void show(Object input) {
			if (doPrint) {
				System.out.println(input.toString());
			}
		}


		//creates and returns a parser for the given input.
		private PLPParser makeParser(String input) throws LexicalException {
			show(input);
			PLPScanner scanner = new PLPScanner(input).scan();
			show(scanner);
			PLPParser parser = new PLPParser(scanner);
			return parser;
		}	

		/**
		 * An empty program.  This throws an exception because it lacks an identifier and a block. 
		 * The test case passes because the unit test expects an exception.
		 *  
		 * @throws LexicalException
		 * @throws SyntaxException 
		 */
		@Test
		public void testEmpty() throws LexicalException, SyntaxException {
			String input = "";  
			PLPParser parser = makeParser(input);
			thrown.expect(SyntaxException.class);
			parser.parse();
		}
		
		/**
		 * Smallest legal program.
		 *   
		 * @throws LexicalException
		 * @throws SyntaxException 
		 */
		@Test
		public void testSmallest() throws LexicalException, SyntaxException {
			String input = "b{}";  
			PLPParser parser = makeParser(input);
			parser.parse();
		}	
		
		//This test will fail in the starter code. However, it should pass in a complete parser.
		@Test
		public void testDec0() throws LexicalException, SyntaxException {
			String input = "b{int c;}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
		
		//This test will fail in the starter code. However, it should pass in a complete parser.
		@Test
		public void testA() throws LexicalException, SyntaxException {
			String input = "b{char a,b;}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
		
		@Test
		public void testB() throws LexicalException, SyntaxException {
			String input = "b{char a,b;string b,d;int _AS_1,_2SC_;boolean A_sw_2;float _232B=2.22335;}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
		
		@Test
		public void testC() throws LexicalException, SyntaxException {
			String input = "b{sleep sin(A|B);if(a?1:b) {while(a|C&E==12<oi+wer*233**!sin(a)) {string a,b,c;};};}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
		
		@Test
		public void testD() throws LexicalException, SyntaxException {
			String input = "b{boolean a;int b, x, y;char  c;float d,t;string e;}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
		
		@Test
		public void testE() throws LexicalException, SyntaxException {
			String input = "b{a = true;B = 10;b = \'a\';d = 23.2;e = \"Hello, World!\";}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
		
		@Test
		public void testF() throws LexicalException, SyntaxException {
			String input = "b{a = 1+2; d = 2.12 - 1; a=3; a  = 1 + 2 *4.5; t = (1+2) * 4.5; t = (((4-2)*5.6)/3)+2; t = 4 - 2 * 5.6 / 3;}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
		
		@Test
		public void testG() throws LexicalException, SyntaxException {
			String input = "b\n{if \t( a==100 )\r{print\f (\"Value of a is 100\");};}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}

		@Test
		public void testH() throws LexicalException, SyntaxException {
			String input = "b{if ( score > 100 )%{121312%} {print( a );print(B);print(score);};}";
			PLPParser parser = makeParser(input);
			parser.parse();
		}
}
