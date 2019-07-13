package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishMeetingLinePattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishMeetingLinePattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishMeetingLine;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isFilled() ) {
			if( !firstCandlestick.isShortBody() ) {
				if( secondCandlestick.isHollow() ) {
					if( !secondCandlestick.isShortBody() ) {
						if( CandleStickVo.isSamePrice(secondCandlestick.getClosePrice(), firstCandlestick.getClosePrice()) ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayLow());
							return true;	
						}
					}
				}
			}
		}
		return false;
	}	
}
