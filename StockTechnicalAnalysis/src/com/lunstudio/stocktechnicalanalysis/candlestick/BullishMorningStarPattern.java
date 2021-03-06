package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

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
		
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isShortBody() && secondCandlestick.getHighPrice().compareTo(firstCandlestick.getClosePrice()) < 0  ) {
				if( thirdCandlestick.isHollow() && thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getBottom()) > 0 ) {
					if( thirdCandlestick.getClosePrice().compareTo(firstCandlestick.getBodyMiddle()) >= 0 ) {
						super.init(thirdCandlestick);
						super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
						if( secondCandlestick.getLowPrice().compareTo(thirdCandlestick.getLowPrice()) < 0 ) {
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getLowPrice());	
						} else {
							super.candlestickEntity.setStoplossPrice(thirdCandlestick.getLowPrice());
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}
