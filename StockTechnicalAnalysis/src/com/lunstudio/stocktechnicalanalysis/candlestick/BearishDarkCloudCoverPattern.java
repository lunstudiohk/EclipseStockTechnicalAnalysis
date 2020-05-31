package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishDarkCloudCoverPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishDarkCloudCoverPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishDarkCloudCover;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() ) {
			if( secondCandlestick.isFilled() ) {
				if( secondCandlestick.isGapUp(firstCandlestick) ) {
					if( firstCandlestick.getBodyHalf().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
						if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
