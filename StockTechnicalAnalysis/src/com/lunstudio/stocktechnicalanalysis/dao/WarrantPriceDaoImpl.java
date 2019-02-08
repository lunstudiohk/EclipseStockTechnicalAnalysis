package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;

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
	
}
