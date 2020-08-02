package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishDownsideGapTwoRabbits extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishDownsideGapTwoRabbits(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishDownsideGapTwoRabbits;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isHollow() && !secondCandlestick.isShortBody() ) {
				if( secondCandlestick.isGapDown(firstCandlestick) ) {
					if( thirdCandlestick.isHollow() ) {
						if( thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
							if( thirdCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
								if( thirdCandlestick.getClosePrice().compareTo(firstCandlestick.getClosePrice()) < 0 ) {
									super.init(thirdCandlestick);
									super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
									super.candlestickEntity.setStoplossPrice(thirdCandlestick.getLowPrice());
									return true;
								}
							}
						}
					}
				}
			}		
		}
		
		return false;
	}
}
