package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishHomingPigeonPattern extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishHomingPigeonPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishHomingPigeon;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isFilled() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isFilled() ) {
				if( firstCandlestick.getTop().compareTo(secondCandlestick.getTop()) > 0 ) {
					if( firstCandlestick.getBottom().compareTo(secondCandlestick.getBottom()) < 0 ) {
						super.init(secondCandlestick);
						if( firstCandlestick.getBodyHalf().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
							super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyHalf());	
						} else {
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
						}
						if( firstCandlestick.getDayLow().compareTo(secondCandlestick.getDayLow()) < 0 ) {
							super.candlestickEntity.setStoplossPrice(firstCandlestick.getDayLow());
						} else {
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayLow());
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}
