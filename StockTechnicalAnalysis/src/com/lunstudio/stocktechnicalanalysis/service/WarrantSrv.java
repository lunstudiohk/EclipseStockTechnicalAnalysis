package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockPriceDao;
import com.lunstudio.stocktechnicalanalysis.dao.WarrantPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;

@Service
public class WarrantSrv {

	private static final Logger logger = LogManager.getLogger();

	private static final BigDecimal drate = BigDecimal.ZERO;
	
	@Autowired
	private WarrantPriceDao warrantPriceDao;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private StockSrv stockSrv;
	
	public void saveWarrantPriceList(List<WarrantPriceEntity> warrantPriceList) throws Exception {
		this.warrantPriceDao.save(warrantPriceList, 10000);
		return;
	}

	public WarrantPriceEntity getLatestWarrantPriceEntity(String warrantCode) throws Exception {
		return this.warrantPriceDao.getLastWarrantPriceList(warrantCode, 1).get(0);
	}
	
	public List<WarrantPriceEntity> getWarrantPriceList(Date tradeDate) throws Exception {
		return this.warrantPriceDao.getWarrantPriceList(tradeDate);
	}
	
	
	public BigDecimal getWarrantValue(WarrantPriceEntity warrantPrice) throws Exception {
		BigDecimal price = null;
		String stockCode = this.stockSrv.getStockCode(warrantPrice.getWarrantUnderlying());
		StockPriceEntity stockPrice = this.stockPriceSrv.getDailyStockPrice(stockCode, warrantPrice.getTradeDate());
		long diff = DateUtils.getDayDiff(warrantPrice.getWarrantMaturityDate(), stockPrice.getTradeDate());
		double tm = diff/360.0;
		BigDecimal k = this.getBlackscholes(warrantPrice.getWarrantType(), stockPrice.getClosePrice().doubleValue(), 
				warrantPrice.getWarrantStrikePrice().doubleValue(), tm, warrantPrice.getRiskFactor().doubleValue()/100, warrantPrice.getImpVol().doubleValue() / 100);
		price = k.divide(warrantPrice.getWarrantRatio(), 10, RoundingMode.HALF_UP);
		return price;
	}
	
	private BigDecimal getBlackscholes(String warrantType, double stockPrice, double strikePrice, double timeDiff, double risk, double impliedVolatility) throws Exception {
		BigDecimal value = null;
		/*
		 * 	var v1 = Math.log(s/xvalue*1);
		 *	var v2 = r-b+Math.pow(v, 2)/2;
		 *	var v3 = v*Math.sqrt(t);
		*/
		double v1 = Math.log(stockPrice/strikePrice);
		double v2 = risk - drate.doubleValue() + Math.pow(impliedVolatility, 2)/2;
		double v3 = impliedVolatility * Math.sqrt(timeDiff);
		double d1 = (v1 + v2*timeDiff)/v3;
		/*	d2 = d1-v*Math.sqrt(t);	*/
		double d2 = d1 - impliedVolatility * Math.sqrt(timeDiff);
		double k = 0;
		if( WarrantPriceEntity.WARRANT_TYPE_CALL.equals(warrantType) ) {
			k = stockPrice * this.cnd(d1) - strikePrice * Math.exp(-1*(risk-drate.doubleValue())*timeDiff) * this.cnd(d2);
		} else if( WarrantPriceEntity.WARRANT_TYPE_PUT.equals(warrantType) ) {
			k = strikePrice * Math.exp(-1*(risk-drate.doubleValue()) * timeDiff) * this.cnd(-1*d2) - stockPrice * this.cnd(-1*d1);
		}
		return BigDecimal.valueOf(k);
	}

	private double cnd(double xvalue) throws Exception {
		double a1 = 0.31938153;
		double a2 = -0.356563782;
		double a3 = 1.781477937;
		double a4 = -1.821255978;
		double a5 = 1.330274429;
		double l = Math.abs(xvalue);
		double k = 1/(1+0.2316419*l);
		double r = 1-1/Math.sqrt(2*Math.PI)*Math.exp(-1*Math.pow(l, 2)/2)*(a1*k+a2*Math.pow(k, 2)+a3*Math.pow(k, 3)+a4*Math.pow(k, 4)+a5*Math.pow(k, 5));
		if (xvalue<0) {
			r = 1-r;
		}
		return r;
	}
	
}
