package org.solovyev.math.dde;

import jscl.math.*;
import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: serso
 * Date: 2/13/12
 * Time: 12:15 AM
 */
public class FormulaHelper {

	public static void main(String[] args) {

		// number of unknowns
		final int n = 10;

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
				if ( row == col ) {
					a0[row][col] = createConstant("α", row + 1);
				} else if ( row + 1 == col ) {
					a0[row][col] = createConstant("β", row + 1);
				} else if ( col >= n ) {
					a0[row][col] = createConstant("γ", row + 1);
				} else {
					int delayI = 0;
					for (Integer delay : delays) {
						delayI++;

						if ( row == col + delay ) {
							a0[row][col] = createConstant("τ", delayI, row + 1 - delay);
						}
					}

					if (a0[row][col] == null) {
						a0[row][col] = JsclInteger.ZERO;
					}
				}
			}
		}

		getUnknowns(rows, cols, a);
	}

	private static void getUnknowns(int rows, int cols, Matrix a) {
		final Generic[][] a0 = a.elements();
		final Generic[] b0 = new Generic[rows];

		final JsclVector b = new JsclVector(b0);
		for (int row = 0; row < rows; row++) {
			b0[row] = createConstant("b", row + 1);
		}

		final Map<String, Generic> cache = new HashMap<String, Generic>();
		for (int col = 0; col < cols; col++) {
			final Generic x = createConstant("x", col + 1);
			cache.put(x.toString(), x);

		}
		for (int row = 0; row < rows; row++) {

			Generic x_i = JsclInteger.ZERO;
			for (int col = 0; col < cols; col++) {

				if ( row + 1 != col ) {
					x_i = x_i.add(new Fraction(a0[row][col].negate().multiply(cache.get(getConstantName("x", col + 1))), a0[row][row + 1]).simplify());
				}
			}

			cache.put(getConstantName("x", row + 2), x_i);
		}


		System.out.println("A = ");
		System.out.println(a);

		System.out.println("b = ");
		System.out.println(b);

		System.out.println("x = ");
		for (int col = 0; col < cols; col++) {
			String xiName = getConstantName("x", col + 1);
			System.out.println(xiName + " = " + cache.get(xiName));

		}
	}

	private static String getConstantName( String name, int i ) {
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
}
