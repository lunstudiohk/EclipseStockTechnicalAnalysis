package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishMatchingLowPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishMatchingLowPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishMatchingLow;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isFilled() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isFilled() ) {
				if( firstCandlestick.getDayVolume() == null || secondCandlestick.getDayVolume() == null || firstCandlestick.getDayVolume().compareTo(secondCandlestick.getDayVolume()) < 0 ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
						if( CandleStickVo.isSamePrice(firstCandlestick.getClosePrice(), secondCandlestick.getClosePrice()) ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyHalf());	
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
		}
		return false;
	}	
}
