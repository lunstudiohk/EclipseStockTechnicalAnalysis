/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Marc de Verdelhan & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.verdelhan.ta4j.trading.rules;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.indicators.simple.ConstantIndicator;

/**
 * Indicator-over-indicator rule.
 * <p>
 * Satisfied when the value of the first {@link Indicator indicator} is strictly greater than the value of the second one.
 */
public class ChangeIndicatorRule extends AbstractRule {

    /** The first indicator */
    private Indicator<Decimal> first;

    private Decimal delta;
    
    /**
     * Constructor.
     * @param first the first indicator
     * @param second the second indicator
     */
    public ChangeIndicatorRule(Indicator<Decimal> first, Decimal delta) {
        this.first = first;
        this.delta = delta;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
    		if(this.delta.isGreaterThan(Decimal.ZERO)) {
    			Decimal percentage = Decimal.ONE.plus(this.delta);
    			final boolean satisfied = first.getValue(index).isGreaterThan(first.getValue(index-1).multipliedBy(percentage));
    			traceIsSatisfied(index, satisfied);
    			return satisfied;
    		} else if(this.delta.isLessThan(Decimal.ZERO)) {
    			Decimal percentage = Decimal.ONE.plus(this.delta);
    			final boolean satisfied = (first.getValue(index-1).multipliedBy(percentage)).isLessThan(first.getValue(index));
    			traceIsSatisfied(index, satisfied);
    			return satisfied;
    		} else {
    			final boolean satisfied = first.getValue(index).isEqual(first.getValue(index-1));
    			traceIsSatisfied(index, satisfied);
    			return satisfied;
    		}
    }
}
