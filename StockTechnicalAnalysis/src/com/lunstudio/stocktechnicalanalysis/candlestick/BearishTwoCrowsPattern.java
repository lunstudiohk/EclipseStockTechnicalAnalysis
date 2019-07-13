package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishTwoCrowsPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishTwoCrowsPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishTwoCrows;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isFilled() ) {
				if( secondCandlestick.isGapUp(firstCandlestick) ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getClosePrice()) > 0 ) {
						if( thirdCandlestick.isFilled() ) {
							if( thirdCandlestick.isOpenInsideBody(secondCandlestick) ) {
								if( thirdCandlestick.isCloseInsideBody(firstCandlestick) ) {
									super.init(thirdCandlestick);
									super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
									super.candlestickEntity.setStoplossPrice(thirdCandlestick.getDayHigh());
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
