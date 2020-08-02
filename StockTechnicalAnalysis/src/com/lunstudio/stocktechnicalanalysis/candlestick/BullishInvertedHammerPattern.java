package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishInvertedHammerPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishInvertedHammerPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishInvertedHammer;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) < 0 ) {
				if( secondCandlestick.getCandleLength().compareTo(secondCandlestick.getHighlowMedian()) >= 0 ) {
					if( MathUtils.getPrecentage(secondCandlestick.getBodyLength(), secondCandlestick.getCandleLength()).compareTo(BigDecimal.valueOf(25)) < 0 ) {
						if( MathUtils.getPrecentage(secondCandlestick.getLowerShadowLength(), secondCandlestick.getCandleLength()).compareTo(BigDecimal.valueOf(10)) < 0 ) {
							super.init(secondCandlestick);
							BigDecimal confirmation = secondCandlestick.getTop().add(secondCandlestick.getUpperShadowLength().divide(two));
							super.candlestickEntity.setConfirmPrice(confirmation);
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getLowPrice());
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
