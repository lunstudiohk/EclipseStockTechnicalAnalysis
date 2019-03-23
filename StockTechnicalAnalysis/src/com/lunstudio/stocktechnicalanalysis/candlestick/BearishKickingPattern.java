package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BearishKickingPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishKickingPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishKicking;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isFilled() && !secondCandlestick.isShortBody() ) {
				if( secondCandlestick.isGapDown(firstCandlestick) ) {
					super.init(secondCandlestick);
					super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
					super.candlestickEntity.setStoplossPrice(secondCandlestick.getOpenPrice());
					return true;
				}
			}
		}
		return false;
	}

}
