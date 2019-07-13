package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishPiercingLinePattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishPiercingLinePattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishPiercingLine;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isHollow() ) {
				if( secondCandlestick.isGapDown(firstCandlestick) ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getBodyHalf()) >= 0 ) {
						if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getTop()) < 0 ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getTop());
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayLow());
							return true;	
						}
					}
				}
			}
		}
		return false;
	}
}
