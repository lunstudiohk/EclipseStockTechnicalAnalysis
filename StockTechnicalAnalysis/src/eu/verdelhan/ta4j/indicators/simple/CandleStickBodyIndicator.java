package eu.verdelhan.ta4j.indicators.simple;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

public class CandleStickBodyIndicator extends CachedIndicator<Decimal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TimeSeries series;

    public CandleStickBodyIndicator(TimeSeries series) {
        super(series);
        this.series = series;
    }

    @Override
    protected Decimal calculate(int index) {
        return (series.getTick(index).getClosePrice().minus(series.getTick(index).getOpenPrice())).abs();
    }
}
