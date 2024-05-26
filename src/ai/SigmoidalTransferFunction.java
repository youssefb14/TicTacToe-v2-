package ai;

import java.io.Serializable;

public class SigmoidalTransferFunction implements TransferFunction, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public double evalute(double value) {return 1 / (1 + Math.exp(- value));}
	@Override
	public double evaluteDerivate(double value) {return (value * (1.0 - value));}
}
