package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishShootingStarPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishShootingStarPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishShootingStar;
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
					if( secondCandlestick.getUpperShadowLength().compareTo(secondCandlestick.getBodyLength().multiply(two)) > 0 ) {
						if( secondCandlestick.isShortBody() ) {
							if( CandleStickVo.isSamePrice(secondCandlestick.getClosePrice(), secondCandlestick.getLowPrice()) ) {
								super.init(secondCandlestick);
								super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
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
