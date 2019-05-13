package com.lunstudio.stocktechnicalanalysis.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;

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
	
}
