package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishMatchingLowPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishMatchingLowPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		//super.pattern = BullishPatterns.BullishMatchingLow;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() ) {
			if( firstCandlestick.getBodyLength().compareTo(firstCandlestick.getBodyMedian()) > 0 ) {
				if( secondCandlestick.isFilled() ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
						if( CandleStickVo.isSamePrice(firstCandlestick.getClosePrice(), secondCandlestick.getClosePrice()) ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyMiddle());	
							if( firstCandlestick.getLowPrice().compareTo(secondCandlestick.getLowPrice()) < 0 ) {
								super.candlestickEntity.setStoplossPrice(firstCandlestick.getLowPrice());
							} else {
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getLowPrice());
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
