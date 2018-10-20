package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantInfoEntity;

@Repository ("warrantInfoDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WarrantInfoDaoImpl extends BaseDaoImpl implements WarrantInfoDao {


	@Override
	public List<WarrantInfoEntity> getWarrantList() {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WarrantInfoEntity> query = builder.createQuery(WarrantInfoEntity.class);
        Root<WarrantInfoEntity> warrantInfoRoot = query.from(WarrantInfoEntity.class);
        query.select(warrantInfoRoot);
	    query.orderBy(builder.asc(warrantInfoRoot.get("warrantCode")));
		return session.createQuery(query).getResultList();
	}

}
