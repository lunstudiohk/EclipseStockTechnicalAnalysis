package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BearishHangingMan extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishHangingMan(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishHangingMan;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		return false;
	}

}
