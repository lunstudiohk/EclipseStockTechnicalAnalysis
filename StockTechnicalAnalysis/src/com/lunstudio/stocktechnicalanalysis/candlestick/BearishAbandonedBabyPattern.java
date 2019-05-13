package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BearishAbandonedBabyPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishAbandonedBabyPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishAbandonedBaby;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isShortBody() ) {
				if( firstCandlestick.getDayHigh().compareTo(secondCandlestick.getDayLow()) < 0 ) {
					if( thirdCandlestick.isFilled() ) {
						if( secondCandlestick.getDayLow().compareTo(thirdCandlestick.getDayHigh()) > 0 ) {
							if( thirdCandlestick.getClosePrice().compareTo(firstCandlestick.getBodyHalf()) <= 0 ) {
								super.init(thirdCandlestick);
								super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayHigh());
								return true;	
							}
						}
					}
				}
			}
		}
		return false;
	}

}