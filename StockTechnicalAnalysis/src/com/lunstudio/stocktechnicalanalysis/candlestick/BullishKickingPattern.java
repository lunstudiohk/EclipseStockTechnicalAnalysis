package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishKickingPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishKickingPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishKicking;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( BearishBeltHoldPattern.isValid(firstCandlestick) ) {
			if( secondCandlestick.isHollow() ) {
				if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) < 0  ) {
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
