package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishEngulfingPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishEngulfingPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishEngulfing;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isHollow() ) {
				if( firstCandlestick.getTop().compareTo(secondCandlestick.getTop()) < 0 ) {
					if( firstCandlestick.getBottom().compareTo(secondCandlestick.getBottom()) > 0 ) {
						if( !secondCandlestick.isShortBody() ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getTop());
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getBottom());
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

}
