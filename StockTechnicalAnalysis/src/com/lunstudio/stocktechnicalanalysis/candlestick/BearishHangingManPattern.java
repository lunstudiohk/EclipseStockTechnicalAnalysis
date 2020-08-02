package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

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
			if( secondCandlestick.getLowerShadowLength().compareTo(secondCandlestick.getBodyLength().multiply(two)) > 0 ) {
				if( secondCandlestick.isFilled() ) {
					if( secondCandlestick.isShortBody() ) {
						if( CandleStickVo.isSamePrice(secondCandlestick.getOpenPrice(), secondCandlestick.getHighPrice()) ) {
							if( secondCandlestick.isLongLowerShadow() ) {
								if( secondCandlestick.getUpperShadowLength().compareTo(secondCandlestick.getBodyLength()) < 0 ) {
									super.init(secondCandlestick);
									super.candlestickEntity.setConfirmPrice(secondCandlestick.getLowerShadowMiddle());
									super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
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
