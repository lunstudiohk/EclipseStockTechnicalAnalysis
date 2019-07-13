package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishEveningStarPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishEveningStarPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishEveningStar;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isGapUp(firstCandlestick) && secondCandlestick.isShortBody() ) {
				if( thirdCandlestick.isFilled() && thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getTop()) < 0 ) {
					if( thirdCandlestick.getDayVolume() == null || firstCandlestick.getDayVolume() == null || secondCandlestick.getDayVolume() == null 
							|| thirdCandlestick.getDayVolume().compareTo(firstCandlestick.getDayVolume()) > 0
							|| thirdCandlestick.getDayVolume().compareTo(secondCandlestick.getDayVolume()) > 0 ) {
						if( thirdCandlestick.getClosePrice().compareTo(firstCandlestick.getBodyHalf()) <= 0 ) {
							super.init(thirdCandlestick);
							super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
							if( secondCandlestick.getDayHigh().compareTo(thirdCandlestick.getDayHigh()) > 0 ) {
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayHigh());	
							} else {
								super.candlestickEntity.setStoplossPrice(thirdCandlestick.getDayHigh());
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
