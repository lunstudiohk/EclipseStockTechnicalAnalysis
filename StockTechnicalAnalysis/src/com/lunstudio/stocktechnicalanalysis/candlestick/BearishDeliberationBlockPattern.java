package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishDeliberationBlockPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishDeliberationBlockPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishDeliberationBlock;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isHollow() ) {
				if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getOpenPrice()) > 0 && secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) < 0 ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getClosePrice()) > 0 ) {
						if( thirdCandlestick.isHollow() && thirdCandlestick.isShortBody() ) {
							if( thirdCandlestick.isGapUp(secondCandlestick) ) {
								super.init(thirdCandlestick);
								super.candlestickEntity.setConfirmPrice(thirdCandlestick.getBodyMiddle());
								super.candlestickEntity.setStoplossPrice(thirdCandlestick.getHighPrice());
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

}
