package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;


import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BearishDojiStarPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishDojiStarPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishDojiStar;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() ) {
			if( !firstCandlestick.isShortBody() ) {
				if( secondCandlestick.isGapUp(firstCandlestick) ) {
					if( secondCandlestick.isDoji() ) {
						super.init(secondCandlestick);
						super.candlestickEntity.setConfirmPrice(secondCandlestick.getDayLow());
						super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayHigh());
						return true;
					}
				}
			}
		}
		return false;
	}

}
