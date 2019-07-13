package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishMeetingLinePattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishMeetingLinePattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishMeetingLine;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() ) {
			if( !firstCandlestick.isShortBody() ) {
				if( secondCandlestick.isFilled() ) {
					if( !secondCandlestick.isShortBody() ) {
						if( CandleStickVo.isSamePrice(firstCandlestick.getClosePrice(), secondCandlestick.getClosePrice()) ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayHigh());
							return true;	
						}
					}
				}
			}
		}
		return false;
	}

}
