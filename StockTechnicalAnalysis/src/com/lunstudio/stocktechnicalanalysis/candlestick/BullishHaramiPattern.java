package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishHaramiPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishHaramiPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishHarami;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( BullishHaramiPattern.isValid(firstCandlestick, secondCandlestick) ) {
			super.init(secondCandlestick);
			if( firstCandlestick.getBodyMiddle().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
				super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyMiddle());
			} else {
				super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());	
			}
			super.candlestickEntity.setStoplossPrice(firstCandlestick.getLowPrice());
			return true;
		}
		return false;
	}
	
	public static boolean isValid(CandleStickVo firstCandlestick, CandleStickVo secondCandlestick) throws Exception {
		if( firstCandlestick.isFilled() ) {
			if( firstCandlestick.getBodyLength().compareTo(firstCandlestick.getBodyMedian()) > 0 ) {
				if( secondCandlestick.isHollow() ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
						if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
}
