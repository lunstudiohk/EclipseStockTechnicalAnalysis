package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishEngulfingPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishEngulfingPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishEngulfing;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( BullishEngulfingPattern.isValid(firstCandlestick, secondCandlestick) ) {
			super.init(secondCandlestick);
			super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
			super.candlestickEntity.setStoplossPrice(secondCandlestick.getOpenPrice());
			return true;
		}
		return false;
	}

	public static boolean isValid(CandleStickVo firstCandlestick, CandleStickVo secondCandlestick) throws Exception {
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isHollow() ) {
				if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getOpenPrice()) > 0 ) {
					if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) < 0 ) {
						if( secondCandlestick.getCandleLength().compareTo(secondCandlestick.getHighlowMedian()) > 0 ) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
