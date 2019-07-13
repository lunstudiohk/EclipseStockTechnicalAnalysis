package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.CbbcPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CbbcAmountVo;

@Service
public class CbbcSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private CbbcPriceDao cbbcPriceDao;
	
	public void saveCbbcPriceList(List<CbbcPriceEntity> cbbcPriceList) throws Exception {
		this.cbbcPriceDao.save(cbbcPriceList, 10000);
		return;
	}

	public List<CbbcPriceEntity> getCbbcPriceList(Date tradeDate) throws Exception {
		return this.cbbcPriceDao.getCbbcPriceList(tradeDate);
	}
	
	public Map<Date, CbbcAmountVo> getCbbcAmountDateMap(String stockCode, Date startDate) throws Exception {
		Map<Date, CbbcAmountVo> cbbcAmountDateMap = new HashMap<Date, CbbcAmountVo>();
		List<CbbcAmountVo> cbbcList = this.cbbcPriceDao.getCbbcAmountList(stockCode, startDate);
		for(CbbcAmountVo amountVo: cbbcList) {
			cbbcAmountDateMap.put(amountVo.getTradeDate(), amountVo);
		}
		/*
		List<CbbcPriceEntity> cbbcPriceList = this.cbbcPriceDao.getCbbcPriceList(stockCode, startDate);
		for(CbbcPriceEntity cbbcPrice : cbbcPriceList) {
			if( cbbcPrice.getQustanding() == null ) {
				continue;
			}
			StockPriceEntity stockPrice = stockPriceDateMap.get(cbbcPrice.getTradeDate());
			BigDecimal upperLimit = stockPrice.getClosePrice().multiply(BigDecimal.valueOf(1.05));
			BigDecimal lowerLimit = stockPrice.getClosePrice().multiply(BigDecimal.valueOf(0.95));
			CbbcAmountVo cbbcAmountVo = cbbcAmountDateMap.get(cbbcPrice.getTradeDate());
			if( cbbcAmountVo == null ) {
				cbbcAmountVo = new CbbcAmountVo(stockCode, cbbcPrice.getTradeDate());
				cbbcAmountDateMap.put(cbbcPrice.getTradeDate(), cbbcAmountVo);
			}
			if( cbbcPrice.getClosePrice() != null ) {
				double amount = (cbbcPrice.getClosePrice().doubleValue() * cbbcPrice.getIssueSize().doubleValue() * cbbcPrice.getQustanding().doubleValue()/100)/1000;
				double cost = cbbcPrice.getClosePrice().doubleValue() * cbbcPrice.getIssueSize().doubleValue();
				if( CbbcPriceEntity.CBBC_TYPE_BULL.equals(cbbcPrice.getCbbcType()) ) {
					cbbcAmountVo.setCbbcBullAmount(cbbcAmountVo.getCbbcBullAmount().add(BigDecimal.valueOf(amount)));
					cbbcAmountVo.setCbbcBullCost(cbbcAmountVo.getCbbcBullCost().add(BigDecimal.valueOf(cost)));
					
					cbbcAmountVo.setCbbcAllBullAmount(cbbcAmountVo.getCbbcAllBullAmount().add(BigDecimal.valueOf(amount)));
					cbbcAmountVo.setCbbcAllBullTurnover(cbbcAmountVo.getCbbcAllBullTurnover().add(cbbcPrice.getTurnover()));
					if( cbbcPrice.getCbbcCallLevel().compareTo(lowerLimit) > 0 ) {
						cbbcAmountVo.setCbbcNearBullAmount(cbbcAmountVo.getCbbcNearBullAmount().add(BigDecimal.valueOf(amount)));
						cbbcAmountVo.setCbbcNearBullTurnover(cbbcAmountVo.getCbbcNearBullTurnover().add(cbbcPrice.getTurnover()));
					}
				} else {
					cbbcAmountVo.setCbbcBearAmount(cbbcAmountVo.getCbbcBearAmount().add(BigDecimal.valueOf(amount)));
					cbbcAmountVo.setCbbcBearCost(cbbcAmountVo.getCbbcBearCost().add(BigDecimal.valueOf(cost)));
					
					cbbcAmountVo.setCbbcAllBearAmount(cbbcAmountVo.getCbbcAllBearAmount().add(BigDecimal.valueOf(amount)));
					cbbcAmountVo.setCbbcAllBearTurnover(cbbcAmountVo.getCbbcAllBearTurnover().add(cbbcPrice.getTurnover()));
					if( cbbcPrice.getCbbcStrikeLevel().compareTo(upperLimit) < 0 ) {
						cbbcAmountVo.setCbbcNearBearAmount(cbbcAmountVo.getCbbcNearBearAmount().add(BigDecimal.valueOf(amount)));
						cbbcAmountVo.setCbbcNearBearTurnover(cbbcAmountVo.getCbbcNearBearTurnover().add(cbbcPrice.getTurnover()));
					}
				}
			}
		}
		*/
		return cbbcAmountDateMap;
	}
	
}
