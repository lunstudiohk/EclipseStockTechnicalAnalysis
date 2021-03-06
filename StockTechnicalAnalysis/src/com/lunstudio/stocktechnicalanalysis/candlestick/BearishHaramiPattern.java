package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishHaramiPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishHaramiPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishHarami;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() ) {
			if( !firstCandlestick.isShortBody() ) {
				if( secondCandlestick.isFilled() ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
						if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
							super.init(secondCandlestick);
							if( firstCandlestick.getBodyMiddle().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
								super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
							} else {
								super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyMiddle());
							}
							super.candlestickEntity.setStoplossPrice(firstCandlestick.getHighPrice());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
