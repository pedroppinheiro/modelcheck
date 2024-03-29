package br.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.algorithms.Algorithms;
import br.mef.Expression;
import br.mef.ExpressionType;
import br.mef.Property;
import br.mef.State;

public class ComplexCTLTest extends Assert{

	ArrayList<State> states;
	Expression expression;
	
	@Before
	public void initialize() {
		states = new ArrayList<State>();
		State s0 = new State("S0");
		State s1 = new State("S1");
		State s2 = new State("S2");
		State s3 = new State("S3");

		s0.addLabelsString("p");
		s0.addLabelsString("q");

		s1.addLabelsString("q");
		s1.addLabelsString("r");

		s2.addLabelsString("r");

		s3.addLabelsString("q");
		s3.addLabelsString("r");

		s0.addChild(s1);
		s0.addChild(s2);

		s1.addChild(s0);

		s2.addChild(s3);

		s3.addChild(s3);

		states.add(s0);
		states.add(s1);
		states.add(s2);
		states.add(s3);
	}

	@Test
	public void test1() {
		
//		(1) M, s0 |= EX(¬p);
		Expression NOT = new Expression("NOT p");
		NOT.setExp1(new Expression("p"));
		NOT.setType(ExpressionType.NOT);
		expression = new Expression("EX (NOT p)");
		expression.setExp1(NOT);
		expression.setType(ExpressionType.EX);
//		(1) YES;
		assertTrue(Algorithms.executeOperation(this.states.get(0), expression));
	}
	@Test
	public void test2() {	
//		(2) M, s0 |= EX (EG(r));
		Expression EG = new Expression("EG r");
		EG.setExp1(new Expression("r"));
		EG.setType(ExpressionType.EG);
		expression = new Expression("EX (EG r)");
		expression.setExp1(EG);
		expression.setType(ExpressionType.EX);
//		(2) YES;
		assertTrue(Algorithms.executeOperation(this.states.get(0), expression));
	}
	
	@Test
	public void test3() {
//		(3) M, s1 |= AG (q ∨ r);
		Expression OR = new Expression("q OR r");
		OR.setExp1(new Expression("q"));
		OR.setExp2(new Expression("r"));
		OR.setType(ExpressionType.OR);
		expression = new Expression("AG (q OR r)");
		expression.setExp1(OR);
		expression.setType(ExpressionType.AG);
//		(3) YES ;
		assertTrue(Algorithms.executeOperation(this.states.get(1), expression));
	}
	
	@Test
	public void test4() {
//		(4) M, s2 |= A [r U q];
		expression = new Expression("A [r U q]");
		expression.setExp1(new Expression("r"));
		expression.setExp2(new Expression("q"));
		expression.setType(ExpressionType.AU);
//		(4) YES;
		assertTrue(Algorithms.executeOperation(this.states.get(2), expression));
	}
	
	@Test
	public void test5() {
//		(5) M, s1 |= A [q U AG(r)];
		Expression AG = new Expression("AG r");
		AG.setExp1(new Expression("r"));
		AG.setType(ExpressionType.AG);
		expression = new Expression("A [q U AG(r)]");
		expression.setExp1(new Expression("q"));
		expression.setExp2(AG);
		expression.setType(ExpressionType.AU);
//		(5) NO (because AG(r) is never true if you keep looping between s0 and s1);
		assertFalse(Algorithms.executeOperation(this.states.get(0), expression));
		assertFalse(Algorithms.executeOperation(this.states.get(1), expression));
		assertTrue(Algorithms.executeOperation(this.states.get(2), expression));
		assertTrue(Algorithms.executeOperation(this.states.get(3), expression));
	}
	
	@Test
	public void test6() {
//		(6) M, s1 |= E[q U EG(r)];
		Expression EG = new Expression("EG r");
		EG.setExp1(new Expression("r"));
		EG.setType(ExpressionType.EG);
		
		expression = new Expression("E [q U EG(r)]");
		expression.setExp1(new Expression("q"));
		expression.setExp2(EG);
		expression.setType(ExpressionType.EU);
//		(6) YES;
		assertTrue(Algorithms.executeOperation(this.states.get(1), expression));
	}
	
	@Test
	public void test7() {
//		(7) M, s0 |= ¬EG(q);
		Expression EG = new Expression("EG q");
		EG.setExp1(new Expression("q"));
		EG.setType(ExpressionType.EG);
		
		expression = new Expression("NOT (EG q)");
		expression.setExp1(EG);
		expression.setType(ExpressionType.NOT);
//		(7) YES;
		assertFalse(Algorithms.executeOperation(this.states.get(0), expression));
	}
	
	@Test
	public void test8() {
//		(8) M, s1 |= EF (AG(q)).
		Expression AG = new Expression("AG q");
		AG.setExp1(new Expression("q"));
		AG.setType(ExpressionType.AG);
		
		expression = new Expression("EF (AG q)");
		expression.setExp1(AG);
		expression.setType(ExpressionType.EF);
//		(8) YES.
		assertTrue(Algorithms.executeOperation(this.states.get(1), expression));
	}
	
	@Test
	public void test9() {
//		(9) created by Pedro
//		EF [(EG r) OR (EG q)] = TRUE
		Expression EG1 = new Expression("EG r");
		EG1.setType(ExpressionType.EG);
		EG1.setExp1(new Expression("r"));
		
		Expression EG2 = new Expression("EG q");
		EG2.setType(ExpressionType.EG);
		EG2.setExp1(new Expression("q"));
		
		Expression OR = new Expression("(EG r) OR (EG q)");
		OR.setExp1(EG1);
		OR.setExp2(EG2);
		OR.setType(ExpressionType.OR);
		
		expression = new Expression("EF [(EG r) OR (EG q)]");
		expression.setExp1(OR);
		expression.setType(ExpressionType.EF);
		
		assertTrue(Algorithms.executeOperation(this.states.get(0), expression));
		printLabelsInformation();
	}
	
	@Test
	public void test10() {
//		(10) created by Pedro
//		A [EG q U AG r]
		Expression EG = new Expression("EG q");
		EG.setType(ExpressionType.EG);
		EG.setExp1(new Expression("q"));
		
		Expression AG = new Expression("AG r");
		AG.setType(ExpressionType.AG);
		AG.setExp1(new Expression("r"));
		
		expression = new Expression("A [EG q U AG r]");
		expression.setExp1(EG);
		expression.setExp2(AG);
		expression.setType(ExpressionType.AU);
//		printLabelsInformation();
		
		assertFalse(Algorithms.executeOperation(this.states.get(0), expression));
		assertFalse(Algorithms.executeOperation(this.states.get(1), expression));
		assertTrue(Algorithms.executeOperation(this.states.get(2), expression));
		assertTrue(Algorithms.executeOperation(this.states.get(3), expression));
	}
	
	@Test
	public void test11() {
//		(11) created by Pedro
//		E [EG q U AG r]
		Expression EG = new Expression("EG q");
		EG.setType(ExpressionType.EG);
		EG.setExp1(new Expression("q"));
		
		Expression AG = new Expression("AG r");
		AG.setType(ExpressionType.AG);
		AG.setExp1(new Expression("r"));
		
		expression = new Expression("E [EG q U AG r]");
		expression.setExp1(EG);
		expression.setExp2(AG);
		expression.setType(ExpressionType.EU);
		boolean result = Algorithms.executeOperation(this.states.get(0), expression);
		printLabelsInformation();
		assertTrue(result);
	}

	public void printLabelsInformation() {
		// Ainda não contém Assert
//		ArrayList<State> validStates = Algorithms.OR(states, expression);

		// imprime todos os estados com seus respectivos labels e propriedades
		// A partir daqui é só System.out.print
		System.out.println("Expression: " + expression.getName());

		for (Iterator<State> iterator = states.iterator(); iterator.hasNext();) {
			State state = (State) iterator.next();
			System.out.println("\nState: " + "\"" + state.getName() + "\"");
			ArrayList<String> labels = state.getLabelsString();
			System.out.println("Labels: ");
			for (Iterator<String> iterator2 = labels.iterator(); iterator2
					.hasNext();) {
				String string = (String) iterator2.next();
				System.out.print("\"" + string + "\"" + " ");
			}
		}

		// System.out.println("\nValid States:");
		// for (Iterator<State> iterator = validStates.iterator();
		// iterator.hasNext();) {
		// State state = (State) iterator.next();
		// System.out.print(state.getName() + " ");
		// }
	}
}
