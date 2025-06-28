package io.sim;

public class Rec {

	public static void main(String[] args) {

		// F1 + F2 +  F3 + F4 + F5 = F6
		// =====>O=====>O=====>O=====> ======>


		
		double[] y = new double[]{10.687,	2.866,7.517,	7.915,	7.476,	35};
		// double[] y = new double[]{87.712,	21.563,	57.918,	57.411,	55.185,	279.789
		// };

		double[] v = new double[]{1.656,	0.528,	1.519, 0.602, 0.696, 3.683
		};

		double[][] A = new double[][] { { -1, -1, -1, -1,-1,1}};


		Reconciliation rec = new Reconciliation(y, v, A);
		System.out.println("Y_hat:");
		rec.printMatrix(rec.getReconciledFlow());
	}

}
