package org.solovyev.math.dde;

import jscl.math.*;
import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.text.ParseException;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * User: serso
 * Date: 2/13/12
 * Time: 12:15 AM
 */
public class FormulaHelper {

	public static void main(String[] args) {

		// number of unknowns
		final int n = 7;

		// number of estimated parameters
		final int p = 1;

		final List<Integer> delays = new ArrayList<Integer>();
		delays.add(3);
		//delays.add(3);
		//delays.add(7);

		final int rows = n - 1;
		final int cols = n + p;

		Generic[][] a0 = new Generic[rows][cols];
		final Matrix a = new Matrix(a0);

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				if (row == col) {
					a0[row][col] = createConstant("α", row + 1);
				} else if (row + 1 == col) {
					a0[row][col] = createConstant("β", row + 1);
				} else if (col >= n) {
					a0[row][col] = createConstant("γ", row + 1);
				} else {
					int delayI = 0;
					for (Integer delay : delays) {
						delayI++;

						if (row == col + delay) {
							a0[row][col] = createConstant("τ", row + 1 - delay);
						}
					}

					if (a0[row][col] == null) {
						a0[row][col] = JsclInteger.ZERO;
					}
				}
			}
		}

		try {
			getUnknowns(rows, cols, a);
		} catch (ParseException e) {
		}
	}

	private static void getUnknowns(int rows, int cols, Matrix a) throws ParseException {
		final Generic[][] a0 = a.elements();
		final Generic[] b0 = new Generic[rows];

		final JsclVector b = new JsclVector(b0);
		for (int row = 0; row < rows; row++) {
			b0[row] = createConstant("b", row + 1);
		}

		final Generic[] x0 = new Generic[cols];
		for (int col = 0; col < cols; col++) {
			final Generic x = createConstant("x", col + 1);
			x0[col] = x;

		}
		for (int row = 0; row < rows; row++) {

			Generic x_i = b0[row];
			for (int col = 0; col < cols; col++) {

				if (row + 1 != col) {
					x_i = x_i.add(new Fraction(a0[row][col].negate().multiply(x0[col]), a0[row][row + 1]).simplify());
				}
			}

			x0[row + 1] = x_i;
		}


		System.out.println("A = ");
		System.out.println(a);

		System.out.println("b = ");
		System.out.println(b);

		System.out.println("x = ");
		for (int i = 0; i < x0.length; i++) {
			final Generic x = x0[i];

			String x_s = x.simplify().toString();
			//System.out.println(getConstantName("x", i + 1) + " = " + x_s);
			for (String s : Arrays.asList("α", "β", "γ")) {
				for (int from = 0; from < cols ; from++) {
					for (int to = cols - 1; to >= from + 1 ; to--) {
						final String product = getProduct(s, from, to);
						x_s = x_s.replace(product, "∏(" +getConstantName(s, "i")+",i," + from + "," + to +")");
					}
				}
			}
			System.out.println(getConstantName("x", i + 1) + " = " + x_s);
			x0[i] = Expression.valueOf(x_s);
		}


		final List<String> expressions = new ArrayList<String>();
		for (Generic x : x0) {
			expressions.add(x.toMathML());
		}

		MathMlViewer.show(new MathMlViewer.Input(expressions));
	}

	private static String getProduct(String s, int j, int k) {
		final StringBuilder result = new StringBuilder();
		for ( int i = j; i <= k; i++ ) {
			if ( i != j ) {
				result.append("*");
			}
			result.append(getConstantName(s, i));
		}
		return result.toString();
	}

	private static String getConstantName(String name, int i) {
		return getConstantName(name, String.valueOf(i));
	}

	private static String getConstantName(String name, String i) {
		return name + "[" + i + "]";
	}

	private static Expression createConstant(String name, int row) {
		return createConstant(name, row, null);
	}

	private static Expression createConstant(String name, int row, @Nullable Integer col) {
		Generic[] subscripts;
		if (col != null) {
			subscripts = new Generic[]{JsclInteger.valueOf(row), JsclInteger.valueOf(col)};
		} else {
			subscripts = new Generic[]{JsclInteger.valueOf(row)};
		}
		return new Constant(name, 0, subscripts).expressionValue();
	}

	private static class MyConstant extends Constant {

		public MyConstant(String name) {
			super(name);
		}

		public MyConstant(String name, int prime, Generic[] subscripts) {
			super(name, prime, subscripts);
		}

		@Override
		public String toString() {
			final StringBuilder result = new StringBuilder();
			result.append(name);

			for (Generic subscript : subscript()) {
				result.append("_").append(subscript);
			}

			return result.toString();
		}
	}
}
