package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishDojiStartPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishDojiStartPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishDojiStart;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isFilled() ) {
			if( !firstCandlestick.isShortBody() ) {
				if( secondCandlestick.isGapDown(firstCandlestick) ) {
					if( secondCandlestick.isDoji() ) {
						super.init(secondCandlestick);
						super.candlestickEntity.setConfirmPrice(secondCandlestick.getDayHigh());
						super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayLow());
						return true;
					}
				}
			}
		}
		return false;
	}
}
