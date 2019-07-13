package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishUpsideGapTwoCrowsPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishUpsideGapTwoCrowsPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishUpsideGapTwoCrows;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isFilled() && secondCandlestick.isGapUp(firstCandlestick) ) {
				if( thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) >= 0 ) {
					if( thirdCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) <= 0 ) {
						if( thirdCandlestick.getClosePrice().compareTo(firstCandlestick.getClosePrice()) > 0 ) {
							super.init(thirdCandlestick);
							super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
							super.candlestickEntity.setStoplossPrice(thirdCandlestick.getDayHigh());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
