package com.lunstudio.stocktechnicalanalysis.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.WarrantPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;

@Service
public class WarrantSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private WarrantPriceDao warrantPriceDao;
		
	public void saveWarrantPriceList(List<WarrantPriceEntity> warrantPriceList) throws Exception {
		this.warrantPriceDao.save(warrantPriceList, 10000);
		return;
	}

}
