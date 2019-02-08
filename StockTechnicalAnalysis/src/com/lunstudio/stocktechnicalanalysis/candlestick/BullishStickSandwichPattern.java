package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishStickSandwichPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishStickSandwichPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishStickSandwich;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isHollow() ) {
				if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
						if( thirdCandlestick.isFilled() && thirdCandlestick.isGapUp(secondCandlestick) ) {
							if( CandleStickVo.isSamePrice(thirdCandlestick.getClosePrice(), firstCandlestick.getClosePrice())) {
								super.init(thirdCandlestick);
								BigDecimal confirmationPrice = secondCandlestick.getClosePrice().add(thirdCandlestick.getClosePrice()).setScale(5).divide(two, RoundingMode.HALF_UP);
								super.candlestickEntity.setConfirmPrice(confirmationPrice);
								super.candlestickEntity.setStoplossPrice(thirdCandlestick.getClosePrice());
								return true;	
							}
						}
					}
				}
			}
		}
		return false;
	}
}
