package ai;

import java.io.Serializable;

public class TanHyperbolicTransferFunction implements TransferFunction, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public double evalute(double value) {return Math.tanh(value); }//{return 1 / (1 + Math.exp(- value));}
	@Override
	public double evaluteDerivate(double value) {return 1.0 - Math.pow(evalute(value), 2); } //{return (value * (1.0 - value));}
}
