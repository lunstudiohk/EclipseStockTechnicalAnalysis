package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
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
			if( secondCandlestick.isGapDown(firstCandlestick)) {
				if( secondCandlestick.isShortBody() ) {
					if( secondCandlestick.getUpperShadow().compareTo(secondCandlestick.getBody().multiply(two)) >= 0 ) {
						if( secondCandlestick.isShortLowerShadow() ) {
							if( secondCandlestick.isLongUpperShadow() ) {
								if( secondCandlestick.getLowerShadow().compareTo(secondCandlestick.getBody()) < 0 ) {
									super.init(secondCandlestick);
									BigDecimal confirmation = secondCandlestick.getTop().add(secondCandlestick.getUpperShadow().divide(two));
									super.candlestickEntity.setConfirmPrice(confirmation);
									super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayLow());
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}
