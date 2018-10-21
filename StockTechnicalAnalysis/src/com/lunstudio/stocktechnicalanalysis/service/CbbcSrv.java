package com.lunstudio.stocktechnicalanalysis.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.CbbcPriceDao;
import com.lunstudio.stocktechnicalanalysis.dao.WarrantPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;

@Service
public class CbbcSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private CbbcPriceDao cbbcPriceDao;
		
	public void saveCbbcPriceList(List<CbbcPriceEntity> cbbcPriceList) throws Exception {
		this.cbbcPriceDao.save(cbbcPriceList, 10000);
		return;
	}

}
