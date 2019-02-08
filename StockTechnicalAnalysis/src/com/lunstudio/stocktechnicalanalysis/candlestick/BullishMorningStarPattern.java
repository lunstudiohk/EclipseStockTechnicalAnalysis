package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishMorningStarPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishMorningStarPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishMorningStar;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isGapDown(firstCandlestick) && secondCandlestick.isShortBody() ) {
				if( thirdCandlestick.isHollow() ) {
					if( thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getBottom()) > 0 ) {
						BigDecimal targetPrice = firstCandlestick.getOpenPrice().add(secondCandlestick.getBottom()).setScale(5).divide(two, RoundingMode.HALF_UP);
						if( thirdCandlestick.getClosePrice().compareTo(targetPrice) >= 0 ) {
							super.init(thirdCandlestick);
							super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
							if( secondCandlestick.getDayLow().compareTo(thirdCandlestick.getDayLow()) < 0 ) {
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayLow());	
							} else {
								super.candlestickEntity.setStoplossPrice(thirdCandlestick.getDayLow());
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