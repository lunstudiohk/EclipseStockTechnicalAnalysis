package eu.verdelhan.ta4j.indicators.simple;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

public class CandleStickUpperShadowIndicator extends CachedIndicator<Decimal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TimeSeries series;

    public CandleStickUpperShadowIndicator(TimeSeries series) {
        super(series);
        this.series = series;
    }

    @Override
    protected Decimal calculate(int index) {
    	Tick tick = series.getTick(index);
    	if( tick.getClosePrice().isGreaterThan(tick.getOpenPrice())) {
    		return tick.getMaxPrice().minus(tick.getClosePrice());
    	} else {
    		return tick.getMaxPrice().minus(tick.getOpenPrice());
    	}
    }
}
