package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BearishHangingManPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishHangingManPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishHangingMan;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
			if( secondCandlestick.getLowerShadow().compareTo(secondCandlestick.getBody().multiply(two)) > 0 ) {
				if( secondCandlestick.isFilled() ) {
					if( secondCandlestick.isShortBody() ) {
						if( CandleStickVo.isSamePrice(secondCandlestick.getOpenPrice(), secondCandlestick.getDayHigh()) ) {
							if( secondCandlestick.isLongLowerShadow() ) {
								if( secondCandlestick.getUpperShadow().compareTo(secondCandlestick.getBody()) < 0 ) {
									super.init(secondCandlestick);
									super.candlestickEntity.setConfirmPrice(secondCandlestick.getLowerShadowHalf());
									super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayHigh());
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
