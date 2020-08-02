package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishHomingPigeonPattern extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishHomingPigeonPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishHomingPigeon;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() ) {
			if( firstCandlestick.getBodyLength().compareTo(firstCandlestick.getBodyMedian()) > 0 ) {
				if( secondCandlestick.isFilled() ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
						if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
							super.init(secondCandlestick);
							if( firstCandlestick.getBodyMiddle().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
								super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyMiddle());	
							} else {
								super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
							}
							if( firstCandlestick.getLowPrice().compareTo(secondCandlestick.getLowPrice()) < 0 ) {
								super.candlestickEntity.setStoplossPrice(firstCandlestick.getLowPrice());
							} else {
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getLowPrice());
							}
							return true;							
						}
					}
				}
			}
		}
		return false;
	}
}
