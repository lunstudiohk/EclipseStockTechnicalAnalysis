package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishBeltHoldPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishBeltHoldPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishBeltHold;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index));

		if( BullishBeltHoldPattern.isValid(firstCandlestick) ) {
			super.init(firstCandlestick);
			super.candlestickEntity.setConfirmPrice(firstCandlestick.getClosePrice());
			super.candlestickEntity.setStoplossPrice(firstCandlestick.getOpenPrice());
			return true;	
		}
		return false;
	}

	public static boolean isValid(CandleStickVo firstCandlestick) throws Exception {
		if( firstCandlestick.isHollow() ) {
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
