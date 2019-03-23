package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishBeltHoldPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishBeltHoldPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishBeltHold;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.getDayLow().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
			if( secondCandlestick.isHollow() ) {
				if( secondCandlestick.isShortLowerShadow() ) {
					if( secondCandlestick.isLongBody() ) {
						super.init(secondCandlestick);
						super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
						super.candlestickEntity.setStoplossPrice(secondCandlestick.getOpenPrice());
						return true;
					}
				}
			}
		}
		return false;
	}

	
}
