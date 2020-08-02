package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishDescendingHawkPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishDescendingHawkPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishDescendingHawk;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() && secondCandlestick.isHollow() ) {
			if( !firstCandlestick.isShortBody() ) {
				if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
						super.init(secondCandlestick);
						if( firstCandlestick.getBodyMiddle().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
							super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyMiddle());	
						} else {
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
						}
						if( firstCandlestick.getHighPrice().compareTo(secondCandlestick.getHighPrice()) > 0 ) {
							super.candlestickEntity.setStoplossPrice(firstCandlestick.getHighPrice());
						} else {
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
						}
						return true;
					}
				}
			}
		}
		return false;
	}

}
