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
			if( secondCandlestick.isGapDown(firstCandlestick) ) {
				if( secondCandlestick.isDoji() ) {
					if( secondCandlestick.getCandleLength().compareTo(secondCandlestick.getHighlowMedian()) > 0 ) {
						super.init(secondCandlestick);
						super.candlestickEntity.setConfirmPrice(secondCandlestick.getHighPrice());
						super.candlestickEntity.setStoplossPrice(secondCandlestick.getLowPrice());
						return true;
					}
				}
			}
		}
		return false;
	}
}
