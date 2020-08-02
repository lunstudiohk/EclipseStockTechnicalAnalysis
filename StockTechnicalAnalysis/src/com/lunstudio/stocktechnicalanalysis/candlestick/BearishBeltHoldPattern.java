package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishBeltHoldPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishBeltHoldPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishBeltHold;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( BearishBeltHoldPattern.isValid(firstCandlestick) ) {
			super.init(firstCandlestick);
			super.candlestickEntity.setConfirmPrice(firstCandlestick.getClosePrice());
			super.candlestickEntity.setStoplossPrice(firstCandlestick.getOpenPrice());
			return true;
		}			
		return false;
	}

	public static boolean isValid(CandleStickVo firstCandlestick) throws Exception {
		if( firstCandlestick.isFilled() ) {
			if( MathUtils.getPrecentage(firstCandlestick.getUpperShadowLength(), firstCandlestick.getCandleLength()).compareTo(BigDecimal.valueOf(10)) < 0 ) {
				if( MathUtils.getPrecentage(firstCandlestick.getLowerShadowLength(), firstCandlestick.getCandleLength()).compareTo(BigDecimal.valueOf(10)) < 0 ) {
					if( firstCandlestick.getBodyLength().compareTo(firstCandlestick.getBodyMedian()) > 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
