package eu.verdelhan.ta4j.indicators.trackers;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

public class SlopeIndicator extends CachedIndicator<Decimal> {

	private final Indicator<Decimal> indicator;

    private final int timeFrame;
    
	private SimpleRegression regression;


    public SlopeIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
        this.regression = new SimpleRegression();
    }
    
	@Override
	protected Decimal calculate(int index) {
		this.regression.clear();
		int count = 1;
        for (int i = Math.max(0, index - timeFrame + 1); i <= index; i++) {
            this.regression.addData(count++, this.indicator.getValue(i).toDouble());
        }
        return Decimal.valueOf(this.regression.getSlope());
	}

	@Override
    public String toString() {
        return getClass().getSimpleName() + " timeFrame: " + timeFrame;
    }
}
