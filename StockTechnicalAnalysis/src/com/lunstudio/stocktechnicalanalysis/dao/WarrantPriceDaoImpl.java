package com.lunstudio.stocktechnicalanalysis.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.OptionAmountVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.WarrantAmountVo;

@Repository ("warrantPriceDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WarrantPriceDaoImpl extends BaseDaoImpl implements WarrantPriceDao {

	@Override
	public List<WarrantPriceEntity> getLastWarrantPriceList(String warrantCode, Integer period) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WarrantPriceEntity> query = builder.createQuery(WarrantPriceEntity.class);
        Root<WarrantPriceEntity> warrantPriceRoot = query.from(WarrantPriceEntity.class);
        query.select(warrantPriceRoot);
        query.where(builder.and(builder.equal(warrantPriceRoot.get("warrantCode"), warrantCode)));
	    query.orderBy(builder.desc(warrantPriceRoot.get("tradeDate")));
		List<WarrantPriceEntity> warrantPriceEntityList = null;
		if( period != null ) {
			warrantPriceEntityList = session.createQuery(query).setMaxResults(period).getResultList();
		} else {
			warrantPriceEntityList = session.createQuery(query).getResultList();
		}
		List<WarrantPriceEntity> sortedWarrantPriceEntityList = new ArrayList<WarrantPriceEntity>(warrantPriceEntityList.size());
		if(warrantPriceEntityList != null && !warrantPriceEntityList.isEmpty() ) {
			for(WarrantPriceEntity entity : warrantPriceEntityList ) {
				sortedWarrantPriceEntityList.add(0, entity);
			}
			return sortedWarrantPriceEntityList;
		} else {
			return null;
		}
	}

	@Override
	public List<WarrantPriceEntity> getWarrantPriceList(Date tradeDate) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WarrantPriceEntity> query = builder.createQuery(WarrantPriceEntity.class);
        Root<WarrantPriceEntity> warrantPriceRoot = query.from(WarrantPriceEntity.class);
        query.select(warrantPriceRoot);
        query.where(builder.and(builder.equal(warrantPriceRoot.get("tradeDate"), tradeDate)));
		List<WarrantPriceEntity> warrantPriceEntityList = session.createQuery(query).getResultList();
		return warrantPriceEntityList;
	}

	@Override
	public List<WarrantPriceEntity> getWarrantPriceList(String warrantUnderlying, Date tradeDate) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WarrantPriceEntity> query = builder.createQuery(WarrantPriceEntity.class);
        Root<WarrantPriceEntity> warrantPriceRoot = query.from(WarrantPriceEntity.class);
        query.select(warrantPriceRoot);
        
        query.where(
        		builder.greaterThanOrEqualTo(warrantPriceRoot.get("tradeDate"), tradeDate),
        		builder.equal(warrantPriceRoot.get("warrantUnderlying"), warrantUnderlying)
        );
		List<WarrantPriceEntity> warrantPriceEntityList = session.createQuery(query).getResultList();
		return warrantPriceEntityList;
	}
	
	@Override
	public List<WarrantAmountVo> getWarrantAmountList(String stockCode, Date startDate) {
		List<WarrantAmountVo> warrantList = new ArrayList<WarrantAmountVo>();
		// Divide 100 for %, 1000 for amount => 100000
		String hql = "SELECT tradeDate, warrantType, Sum(closePrice*issueSize*qustanding/100000), Sum(turnover) " + 
				"FROM WarrantPriceEntity WHERE warrantUnderlying = :stockCode AND tradeDate >= :tradeDate " + 
				"GROUP BY tradeDate, warrantType " + 
				"ORDER BY tradeDate DESC";
		Session session = this.sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("stockCode",	stockCode);
		query.setParameter("tradeDate", startDate);
		List<Object[]> result = query.list();
		WarrantAmountVo vo = null;
		for(Object[] obj: result) {
			if( vo == null ) {
				vo = new WarrantAmountVo();
				vo.setStockCode(stockCode);
				vo.setTradeDate((Date)obj[0]);
			}
			if( "C".equals(obj[1]) ) {
				vo.setWarrantCallAmount(new BigDecimal(obj[2].toString()));
				vo.setWarrantCallTurnover( new BigDecimal(obj[3].toString()));
			} else {
				vo.setWarrantPutAmount( new BigDecimal(obj[2].toString()));
				vo.setWarrantPutTurnover( new BigDecimal(obj[3].toString()));
			}
			if( vo.getWarrantCallAmount() != null && vo.getWarrantPutAmount() != null ) {
				warrantList.add(vo);
				vo = null;
			}
		}
		return warrantList;
	}
}
