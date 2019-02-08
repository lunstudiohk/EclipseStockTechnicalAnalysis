package com.lunstudio.stocktechnicalanalysis.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.IndexOptionsDao;
import com.lunstudio.stocktechnicalanalysis.entity.IndexOptionsEntity;

@Service
public class OptionsSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private IndexOptionsDao indexOptionsDao;
	
	public void saveIndexOptions(List<IndexOptionsEntity> optionsList) {
		this.indexOptionsDao.save(optionsList, optionsList.size());
		return;
	}


}
