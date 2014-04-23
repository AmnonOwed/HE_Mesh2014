package wblut.external.JAMA;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import wblut.math.WB_Math;

/**
 * Part of JAMA: http://math.nist.gov/javanumerics/jama/
 * 
 * Original copyright Notice:
 * 
 * This software is a cooperative product of The MathWorks and the National
 * Institute of Standards and Technology (NIST) which has been released to the
 * public domain. Neither The MathWorks nor NIST assumes any responsibility
 * whatsoever for its use by other parties, and makes no guarantees, expressed
 * or implied, about its quality, reliability, or any other characteristic.
 */

public class Matrix implements Cloneable, java.io.Serializable {

	/*
	 * ------------------------ Class variables ------------------------
	 */

	/**
	 * Array for internal storage of elements.
	 * 
	 * @serial internal array storage.
	 */
	private final double[][] A;

	/**
	 * Row and column dimensions.
	 * 
	 * @serial row dimension.
	 * @serial column dimension.
	 */
	private final int m, n;

	/*
	 * ------------------------ Constructors ------------------------
	 */

	/**
	 * Construct an m-by-n matrix of zeros.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */

	public Matrix(final int m, final int n) {
		this.m = m;
		this.n = n;
		A = new double[m][n];
	}

	/**
	 * Construct an m-by-n constant matrix.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param s
	 *            Fill the matrix with this scalar value.
	 */

	public Matrix(final int m, final int n, final double s) {
		this.m = m;
		this.n = n;
		A = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = s;
			}
		}
	}

	/**
	 * Construct a matrix from a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of doubles.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 * @see #constructWithCopy
	 */

	public Matrix(final double[][] A) {
		m = A.length;
		n = A[0].length;
		for (int i = 0; i < m; i++) {
			if (A[i].length != n) {
				throw new IllegalArgumentException(
						"All rows must have the same length.");
			}
		}
		this.A = A;
	}

	/**
	 * Construct a matrix quickly without checking arguments.
	 * 
	 * @param A
	 *            Two-dimensional array of doubles.
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */

	public Matrix(final double[][] A, final int m, final int n) {
		this.A = A;
		this.m = m;
		this.n = n;
	}

	/**
	 * Construct a matrix from a one-dimensional packed array
	 * 
	 * @param vals
	 *            One-dimensional array of doubles, packed by columns (ala
	 *            Fortran).
	 * @param m
	 *            Number of rows.
	 * @exception IllegalArgumentException
	 *                Array length must be a multiple of m.
	 */

	public Matrix(final double vals[], final int m) {
		this.m = m;
		n = (m != 0 ? vals.length / m : 0);
		if (m * n != vals.length) {
			throw new IllegalArgumentException(
					"Array length must be a multiple of m.");
		}
		A = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = vals[i + j * m];
			}
		}
	}

	/*
	 * ------------------------ Public Methods ------------------------
	 */

	/**
	 * Construct a matrix from a copy of a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of doubles.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 */

	public static Matrix constructWithCopy(final double[][] A) {
		final int m = A.length;
		final int n = A[0].length;
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			if (A[i].length != n) {
				throw new IllegalArgumentException(
						"All rows must have the same length.");
			}
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * Make a deep copy of a matrix
	 */

	public Matrix copy() {
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * Clone the Matrix object.
	 */

	@Override
	public Object clone() {
		return copy();
	}

	/**
	 * Access the internal two-dimensional array.
	 * 
	 * @return Pointer to the two-dimensional array of matrix elements.
	 */

	public double[][] getArray() {
		return A;
	}

	/**
	 * Copy the internal two-dimensional array.
	 * 
	 * @return Two-dimensional array copy of matrix elements.
	 */

	public double[][] getArrayCopy() {
		final double[][] C = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j];
			}
		}
		return C;
	}

	/**
	 * Make a one-dimensional column packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by columns.
	 */

	public double[] getColumnPackedCopy() {
		final double[] vals = new double[m * n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				vals[i + j * m] = A[i][j];
			}
		}
		return vals;
	}

	/**
	 * Make a one-dimensional row packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by rows.
	 */

	public double[] getRowPackedCopy() {
		final double[] vals = new double[m * n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				vals[i * n + j] = A[i][j];
			}
		}
		return vals;
	}

	/**
	 * Get row dimension.
	 * 
	 * @return m, the number of rows.
	 */

	public int getRowDimension() {
		return m;
	}

	/**
	 * Get column dimension.
	 * 
	 * @return n, the number of columns.
	 */

	public int getColumnDimension() {
		return n;
	}

	/**
	 * Get a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @return A(i,j)
	 * @exception ArrayIndexOutOfBoundsException
	 */

	public double get(final int i, final int j) {
		return A[i][j];
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(final int i0, final int i1, final int j0,
			final int j1) {
		final Matrix X = new Matrix(i1 - i0 + 1, j1 - j0 + 1);
		final double[][] B = X.getArray();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					B[i - i0][j - j0] = A[i][j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(final int[] r, final int[] c) {
		final Matrix X = new Matrix(r.length, c.length);
		final double[][] B = X.getArray();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					B[i][j] = A[r[i]][c[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @return A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(final int i0, final int i1, final int[] c) {
		final Matrix X = new Matrix(i1 - i0 + 1, c.length);
		final double[][] B = X.getArray();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					B[i - i0][j] = A[i][c[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(final int[] r, final int j0, final int j1) {
		final Matrix X = new Matrix(r.length, j1 - j0 + 1);
		final double[][] B = X.getArray();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					B[i][j - j0] = A[r[i]][j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Set a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @param s
	 *            A(i,j).
	 * @exception ArrayIndexOutOfBoundsException
	 */

	public void set(final int i, final int j, final double s) {
		A[i][j] = s;
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(final int i0, final int i1, final int j0,
			final int j1, final Matrix X) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					A[i][j] = X.get(i - i0, j - j0);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(final int[] r, final int[] c, final Matrix X) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					A[r[i]][c[j]] = X.get(i, j);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(final int[] r, final int j0, final int j1,
			final Matrix X) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					A[r[i]][j] = X.get(i, j - j0);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(final int i0, final int i1, final int[] c,
			final Matrix X) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					A[i][c[j]] = X.get(i - i0, j);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Matrix transpose.
	 * 
	 * @return A'
	 */

	public Matrix transpose() {
		final Matrix X = new Matrix(n, m);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[j][i] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * One norm
	 * 
	 * @return maximum column sum.
	 */

	public double norm1() {
		double f = 0;
		for (int j = 0; j < n; j++) {
			double s = 0;
			for (int i = 0; i < m; i++) {
				s += Math.abs(A[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Infinity norm
	 * 
	 * @return maximum row sum.
	 */

	public double normInf() {
		double f = 0;
		for (int i = 0; i < m; i++) {
			double s = 0;
			for (int j = 0; j < n; j++) {
				s += Math.abs(A[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Frobenius norm
	 * 
	 * @return sqrt of sum of squares of all elements.
	 */

	public double normF() {
		double f = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				f = WB_Math.hypot(f, A[i][j]);
			}
		}
		return f;
	}

	/**
	 * Unary minus
	 * 
	 * @return -A
	 */

	public Matrix uminus() {
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = -A[i][j];
			}
		}
		return X;
	}

	/**
	 * C = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */

	public Matrix plus(final Matrix B) {
		checkMatrixDimensions(B);
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] + B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * A = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */

	public Matrix plusEquals(final Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] + B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * C = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */

	public Matrix minus(final Matrix B) {
		checkMatrixDimensions(B);
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] - B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * A = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */

	public Matrix minusEquals(final Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] - B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element multiplication, C = A.*B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */

	public Matrix arrayTimes(final Matrix B) {
		checkMatrixDimensions(B);
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] * B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element multiplication in place, A = A.*B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */

	public Matrix arrayTimesEquals(final Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] * B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element right division, C = A./B
	 * 
	 * @param B
	 *            another matrix
	 * @return A./B
	 */

	public Matrix arrayRightDivide(final Matrix B) {
		checkMatrixDimensions(B);
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] / B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element right division in place, A = A./B
	 * 
	 * @param B
	 *            another matrix
	 * @return A./B
	 */

	public Matrix arrayRightDivideEquals(final Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] / B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element left division, C = A.\B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */

	public Matrix arrayLeftDivide(final Matrix B) {
		checkMatrixDimensions(B);
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = B.A[i][j] / A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element left division in place, A = A.\B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */

	public Matrix arrayLeftDivideEquals(final Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = B.A[i][j] / A[i][j];
			}
		}
		return this;
	}

	/**
	 * Multiply a matrix by a scalar, C = s*A
	 * 
	 * @param s
	 *            scalar
	 * @return s*A
	 */

	public Matrix times(final double s) {
		final Matrix X = new Matrix(m, n);
		final double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = s * A[i][j];
			}
		}
		return X;
	}

	/**
	 * Multiply a matrix by a scalar in place, A = s*A
	 * 
	 * @param s
	 *            scalar
	 * @return replace A by s*A
	 */

	public Matrix timesEquals(final double s) {
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = s * A[i][j];
			}
		}
		return this;
	}

	/**
	 * Linear algebraic matrix multiplication, A * B
	 * 
	 * @param B
	 *            another matrix
	 * @return Matrix product, A * B
	 * @exception IllegalArgumentException
	 *                Matrix inner dimensions must agree.
	 */

	public Matrix times(final Matrix B) {
		if (B.m != n) {
			throw new IllegalArgumentException(
					"Matrix inner dimensions must agree.");
		}
		final Matrix X = new Matrix(m, B.n);
		final double[][] C = X.getArray();
		final double[] Bcolj = new double[n];
		for (int j = 0; j < B.n; j++) {
			for (int k = 0; k < n; k++) {
				Bcolj[k] = B.A[k][j];
			}
			for (int i = 0; i < m; i++) {
				final double[] Arowi = A[i];
				double s = 0;
				for (int k = 0; k < n; k++) {
					s += Arowi[k] * Bcolj[k];
				}
				C[i][j] = s;
			}
		}
		return X;
	}

	/**
	 * LU Decomposition
	 * 
	 * @return LUDecomposition
	 * @see LUDecomposition
	 */

	public LUDecomposition lu() {
		return new LUDecomposition(this);
	}

	/**
	 * Solve A*X = B
	 * 
	 * @param B
	 *            right hand side
	 * @return solution if A is square, least squares solution otherwise
	 */

	public Matrix solve(final Matrix B) {
		return (m == n ? (new LUDecomposition(this)).solve(B)
				: (new QRDecomposition(this)).solve(B));
	}

	/**
	 * Solve X*A = B, which is also A'*X' = B'
	 * 
	 * @param B
	 *            right hand side
	 * @return solution if A is square, least squares solution otherwise.
	 */

	public Matrix solveTranspose(final Matrix B) {
		return transpose().solve(B.transpose());
	}

	/**
	 * Matrix inverse or pseudoinverse
	 * 
	 * @return inverse(A) if A is square, pseudoinverse otherwise.
	 */

	public Matrix inverse() {
		return solve(identity(m, m));
	}

	/**
	 * Matrix determinant
	 * 
	 * @return determinant
	 */

	public double det() {
		return new LUDecomposition(this).det();
	}

	/**
	 * Matrix trace.
	 * 
	 * @return sum of the diagonal elements.
	 */

	public double trace() {
		double t = 0;
		for (int i = 0; i < Math.min(m, n); i++) {
			t += A[i][i];
		}
		return t;
	}

	/**
	 * Generate matrix with random elements
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with uniformly distributed random elements.
	 */

	public static Matrix random(final int m, final int n) {
		final Matrix A = new Matrix(m, n);
		final double[][] X = A.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				X[i][j] = Math.random();
			}
		}
		return A;
	}

	/**
	 * Generate identity matrix
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
	 */

	public static Matrix identity(final int m, final int n) {
		final Matrix A = new Matrix(m, n);
		final double[][] X = A.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				X[i][j] = (i == j ? 1.0 : 0.0);
			}
		}
		return A;
	}

	/**
	 * Print the matrix to stdout. Line the elements up in columns with a
	 * Fortran-like 'Fw.d' style format.
	 * 
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */

	public void print(final int w, final int d) {
		print(new PrintWriter(System.out, true), w, d);
	}

	/**
	 * Print the matrix to the output stream. Line the elements up in columns
	 * with a Fortran-like 'Fw.d' style format.
	 * 
	 * @param output
	 *            Output stream.
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */

	public void print(final PrintWriter output, final int w, final int d) {
		final DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(d);
		format.setMinimumFractionDigits(d);
		format.setGroupingUsed(false);
		print(output, format, w + 2);
	}

	/**
	 * Print the matrix to stdout. Line the elements up in columns. Use the
	 * format object, and right justify within columns of width characters. Note
	 * that is the matrix is to be read back in, you probably will want to use a
	 * NumberFormat that is set to US Locale.
	 * 
	 * @param format
	 *            A Formatting object for individual elements.
	 * @param width
	 *            Field width for each column.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */

	public void print(final NumberFormat format, final int width) {
		print(new PrintWriter(System.out, true), format, width);
	}

	// DecimalFormat is a little disappointing coming from Fortran or C's
	// printf.
	// Since it doesn't pad on the left, the elements will come out different
	// widths. Consequently, we'll pass the desired column width in as an
	// argument and do the extra padding ourselves.

	/**
	 * Print the matrix to the output stream. Line the elements up in columns.
	 * Use the format object, and right justify within columns of width
	 * characters. Note that is the matrix is to be read back in, you probably
	 * will want to use a NumberFormat that is set to US Locale.
	 * 
	 * @param output
	 *            the output stream.
	 * @param format
	 *            A formatting object to format the matrix elements
	 * @param width
	 *            Column width.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */

	public void print(final PrintWriter output, final NumberFormat format,
			final int width) {
		output.println(); // start on new line.
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				final String s = format.format(A[i][j]); // format the number
				final int padding = Math.max(1, width - s.length()); // At
																		// _least_
																		// 1
																		// space
				for (int k = 0; k < padding; k++) {
					output.print(' ');
				}
				output.print(s);
			}
			output.println();
		}
		output.println(); // end with blank line.
	}

	/**
	 * Read a matrix from a stream. The format is the same the print method, so
	 * printed matrices can be read back in (provided they were printed using US
	 * Locale). Elements are separated by whitespace, all the elements for each
	 * row appear on a single line, the last row is followed by a blank line.
	 * 
	 * @param input
	 *            the input stream.
	 */

	public static Matrix read(final BufferedReader input)
			throws java.io.IOException {
		final StreamTokenizer tokenizer = new StreamTokenizer(input);

		// Although StreamTokenizer will parse numbers, it doesn't recognize
		// scientific notation (E or D); however, Double.valueOf does.
		// The strategy here is to disable StreamTokenizer's number parsing.
		// We'll only get whitespace delimited words, EOL's and EOF's.
		// These words should all be numbers, for Double.valueOf to parse.

		tokenizer.resetSyntax();
		tokenizer.wordChars(0, 255);
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.eolIsSignificant(true);
		final java.util.Vector<Double> vD = new java.util.Vector<Double>();

		// Ignore initial empty lines
		while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
			;
		}
		if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
			throw new java.io.IOException("Unexpected EOF on matrix read.");
		}
		do {
			vD.addElement(Double.valueOf(tokenizer.sval)); // Read & store 1st
															// row.
		} while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

		final int n = vD.size(); // Now we've got the number of columns!
		double row[] = new double[n];
		for (int j = 0; j < n; j++) {
			row[j] = vD.elementAt(j).doubleValue();
		}
		final java.util.Vector<double[]> v = new java.util.Vector<double[]>();
		v.addElement(row); // Start storing rows instead of columns.
		while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
			// While non-empty lines
			v.addElement(row = new double[n]);
			int j = 0;
			do {
				if (j >= n) {
					throw new java.io.IOException("Row " + v.size()
							+ " is too long.");
				}
				row[j++] = Double.valueOf(tokenizer.sval).doubleValue();
			} while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);
			if (j < n) {
				throw new java.io.IOException("Row " + v.size()
						+ " is too short.");
			}
		}
		final int m = v.size(); // Now we've got the number of rows.
		final double[][] A = new double[m][];
		v.copyInto(A); // copy the rows out of the vector
		return new Matrix(A);
	}

	/*
	 * ------------------------ Private Methods ------------------------
	 */

	/** Check if size(A) == size(B) **/

	private void checkMatrixDimensions(final Matrix B) {
		if (B.m != m || B.n != n) {
			throw new IllegalArgumentException("Matrix dimensions must agree.");
		}
	}

	private static final long serialVersionUID = 1;
}
