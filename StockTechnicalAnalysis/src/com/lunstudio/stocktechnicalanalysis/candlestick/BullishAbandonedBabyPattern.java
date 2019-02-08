package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishAbandonedBabyPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishAbandonedBabyPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishAbandonedBaby;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isDoji() ) {
				if( firstCandlestick.getDayLow().compareTo(secondCandlestick.getDayHigh()) > 0 ) {
					if( thirdCandlestick.isHollow() ) {
						if( secondCandlestick.getDayHigh().compareTo(thirdCandlestick.getBottom()) < 0 ) {
							super.init(thirdCandlestick);
							super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
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
