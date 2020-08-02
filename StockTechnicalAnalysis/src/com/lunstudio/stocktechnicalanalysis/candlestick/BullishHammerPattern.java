package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=1101
 * A candle for which the body is less than or equal to 25% of the entire candle, 
 * and the upper shadow is less than or equal to 5 percent of the entire candle length. 
 * Formally, B/WC <= .25 AND US/WC <= .05
 * @author alankam
 *
 */
public class BullishHammerPattern extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishHammerPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishHammer;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( BullishHammerPattern.isValid(firstCandlestick) ) {
			super.init(firstCandlestick);
			super.candlestickEntity.setConfirmPrice(firstCandlestick.getTop());
			super.candlestickEntity.setStoplossPrice(firstCandlestick.getLowPrice());
			return true;
		}
		return false;
	}

	public static boolean isValid(CandleStickVo candlestick) throws Exception {
		if( candlestick.isLongCandlestick() ) {
			if( candlestick.isShortShadow(candlestick.getUpperShadowLength()) ) {
				if( candlestick.isLongShadow(candlestick.getLowerShadowLength()) ) {
					return true;
				}
			}
		}
		return false;
	}
}
