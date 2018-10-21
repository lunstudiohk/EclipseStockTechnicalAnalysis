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

@Repository ("cbbcPriceDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CbbcPriceDaoImpl extends BaseDaoImpl implements CbbcPriceDao {
	
}
