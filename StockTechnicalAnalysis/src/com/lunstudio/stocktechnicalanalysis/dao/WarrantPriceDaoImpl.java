package com.lunstudio.stocktechnicalanalysis.dao;

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
	public List<WarrantPriceEntity> getWarrantPriceList() {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WarrantPriceEntity> query = builder.createQuery(WarrantPriceEntity.class);
        Root<WarrantPriceEntity> warrantPriceRoot = query.from(WarrantPriceEntity.class);
        query.select(warrantPriceRoot);
	    query.orderBy(builder.asc(warrantPriceRoot.get("warrantCode")));
		return session.createQuery(query).getResultList();
	}

}
