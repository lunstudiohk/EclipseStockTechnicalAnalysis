package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishConcealingBabySwallowPattern extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishConcealingBabySwallowPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		//super.pattern = BullishPatterns.BullishConcealingBabySwallow;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-3));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo forthCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() /*&& firstCandlestick.isLongBody()*/ ) {
			if( secondCandlestick.isFilled() /*&& secondCandlestick.isLongBody()*/ ) {
				//if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
					if( thirdCandlestick.isFilled() && !thirdCandlestick.isShortBody() ) {
						if( thirdCandlestick.isGapDown(secondCandlestick) ) {
							if( thirdCandlestick.isLongUpperShadow() ) {
								if( forthCandlestick.isFilled() ) {
									if( thirdCandlestick.getHighPrice().compareTo(forthCandlestick.getTop()) < 0 ) {
										if( thirdCandlestick.getLowPrice().compareTo(forthCandlestick.getBottom()) > 0 ) {
											super.init(forthCandlestick);
											super.candlestickEntity.setConfirmPrice(thirdCandlestick.getBodyMiddle());
											super.candlestickEntity.setStoplossPrice(forthCandlestick.getLowPrice());
											return true;				
										}
									}
								}
							}
						}
					}
				//}
			}
		}
		return false;
	}
}
