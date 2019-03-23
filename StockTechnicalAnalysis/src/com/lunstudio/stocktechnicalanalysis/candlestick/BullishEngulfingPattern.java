package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BullishEngulfingPattern extends BullishCandlestickPatterns implements CandlestickPattern {

	public BullishEngulfingPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BullishPatterns.BullishEngulfing;
		return;
	}

	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.getDayVolume() == null || secondCandlestick.getDayVolume() == null || firstCandlestick.getDayVolume().compareTo(secondCandlestick.getDayVolume()) > 0 ) {
			if( secondCandlestick.isFilled() ) {
				if( thirdCandlestick.isHollow() ) {
					if( secondCandlestick.getOpenPrice().compareTo(thirdCandlestick.getClosePrice()) < 0 ) {
						if( secondCandlestick.getClosePrice().compareTo(thirdCandlestick.getOpenPrice()) > 0 ) {
							if( !thirdCandlestick.isShortBody() ) {
								super.init(thirdCandlestick);
								super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
								super.candlestickEntity.setStoplossPrice(thirdCandlestick.getOpenPrice());
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
