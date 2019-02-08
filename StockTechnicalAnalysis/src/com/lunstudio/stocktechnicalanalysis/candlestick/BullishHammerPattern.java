package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishHammerPattern extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishHammerPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishHammer;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo candlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( candlestick.getLowerShadow().compareTo(candlestick.getBody().multiply(two)) > 0 ) {
			if( candlestick.isHollow() ) {
				if( candlestick.isShortBody() ) {
					if( candlestick.isShortUpperShadow() ) {
						if( candlestick.isLongLowerShadow() ) {
							if( candlestick.getUpperShadow().compareTo(candlestick.getBody()) < 0 ) {
								super.init(candlestick);
								super.candlestickEntity.setConfirmPrice(candlestick.getTop());
								super.candlestickEntity.setStoplossPrice(candlestick.getDayLow());
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
