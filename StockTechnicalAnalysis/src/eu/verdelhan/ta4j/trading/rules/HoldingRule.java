package eu.verdelhan.ta4j.trading.rules;

import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.TradingRecord;

public class HoldingRule extends AbstractRule {

	private int holdingDay;
	
	public HoldingRule(int holding) {
		this.holdingDay = holding;
		return;
	}
	
	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        if (tradingRecord != null) {
            Trade currentTrade = tradingRecord.getCurrentTrade();
            if (currentTrade.isOpened()) {
            		if( index - currentTrade.getEntry().getIndex() == this.holdingDay-1 ) {
            			return true;
            		}
            }
        }
		return false;
	}

}
