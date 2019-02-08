package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishThreeWhiteSoldiersPattern extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishThreeWhiteSoldiersPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishThreeWhiteSoldiers;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isHollow() && !secondCandlestick.isShortBody() ) {
				if( thirdCandlestick.isHollow() && !thirdCandlestick.isShortBody() ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
						if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
							if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
								if( secondCandlestick.getOpenPrice().compareTo(thirdCandlestick.getOpenPrice()) < 0 ) {
									if( secondCandlestick.getClosePrice().compareTo(thirdCandlestick.getOpenPrice()) > 0 ) {
										if( secondCandlestick.getClosePrice().compareTo(thirdCandlestick.getClosePrice()) < 0 ) {
											super.init(thirdCandlestick);
											super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
											super.candlestickEntity.setStoplossPrice(thirdCandlestick.getDayLow());
											return true;				
										}
									}
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
