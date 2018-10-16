package eu.verdelhan.ta4j.indicators.simple;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

public class DayRangeIndicator extends CachedIndicator<Decimal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TimeSeries series;

    public DayRangeIndicator(TimeSeries series) {
        super(series);
        this.series = series;
    }

    @Override
    protected Decimal calculate(int index) {
        return series.getTick(index).getMaxPrice().minus(series.getTick(index).getMinPrice());
    }
}
