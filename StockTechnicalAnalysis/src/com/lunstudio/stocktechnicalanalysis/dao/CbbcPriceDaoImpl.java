package com.lunstudio.stocktechnicalanalysis.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CbbcAmountVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.WarrantAmountVo;

@Repository ("cbbcPriceDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CbbcPriceDaoImpl extends BaseDaoImpl implements CbbcPriceDao {

	@Override
	public List<CbbcPriceEntity> getCbbcPriceList(Date tradeDate) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CbbcPriceEntity> query = builder.createQuery(CbbcPriceEntity.class);
        Root<CbbcPriceEntity> cbbcPriceRoot = query.from(CbbcPriceEntity.class);
        query.select(cbbcPriceRoot);
        query.where(builder.and(builder.equal(cbbcPriceRoot.get("tradeDate"), tradeDate)));
		List<CbbcPriceEntity> cbbcPriceEntityList = session.createQuery(query).getResultList();
		return cbbcPriceEntityList;
	}

	@Override
	public List<CbbcPriceEntity> getCbbcPriceList(String cbbcUnderlying, Date tradeDate) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CbbcPriceEntity> query = builder.createQuery(CbbcPriceEntity.class);
        Root<CbbcPriceEntity> cbbcPriceRoot = query.from(CbbcPriceEntity.class);
        query.select(cbbcPriceRoot);
        
        query.where(
        		builder.greaterThanOrEqualTo(cbbcPriceRoot.get("tradeDate"), tradeDate),
        		builder.equal(cbbcPriceRoot.get("cbbcUnderlying"), cbbcUnderlying)
        );
		List<CbbcPriceEntity> cbbcPriceEntityList = session.createQuery(query).getResultList();
		return cbbcPriceEntityList;
	}

	@Override
	public List<CbbcAmountVo> getCbbcAmountList(String stockCode, Date startDate) {
		List<CbbcAmountVo> cbbcList = new ArrayList<CbbcAmountVo>();
		// Divide 100 for %, 1000 for amount => 100000
		String hql = "SELECT tradeDate, cbbcType, Sum(closePrice*issueSize*qustanding/100000), Sum(turnover) " + 
				"FROM CbbcPriceEntity WHERE cbbcUnderlying = :stockCode AND tradeDate >= :tradeDate " + 
				"GROUP BY tradeDate, cbbcType " + 
				"ORDER BY tradeDate DESC";
		Session session = this.sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("stockCode",	stockCode);
		query.setParameter("tradeDate", startDate);
		List<Object[]> result = query.list();
		CbbcAmountVo vo = null;
		for(Object[] obj: result) {
			if( vo == null ) {
				vo = new CbbcAmountVo();
				vo.setStockCode(stockCode);
				vo.setTradeDate((Date)obj[0]);
			}
			if( "Bull".equals(obj[1]) ) {
				vo.setCbbcBullAmount(new BigDecimal(obj[2].toString()));
				vo.setCbbcBullTurnover( new BigDecimal(obj[3].toString()));
			} else {
				vo.setCbbcBearAmount( new BigDecimal(obj[2].toString()));
				vo.setCbbcBearTurnover( new BigDecimal(obj[3].toString()));
			}
			if( vo.getCbbcBullAmount() != null && vo.getCbbcBearAmount() != null ) {
				cbbcList.add(vo);
				vo = null;
			}
		}
		return cbbcList;
	}
	
}
