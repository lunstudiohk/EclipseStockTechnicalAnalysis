package com.lunstudio.stockanalysis.batch;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.dao.StockDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;

@Component
public class FuncTest {

	private static final Logger logger = LogManager.getLogger(FuncTest.class);

	@Autowired
	private StockDao stockDao;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			FuncTest instance = context.getBean(FuncTest.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		List<StockEntity> stockInfoList = this.stockDao.getStockList();
		for(StockEntity entity : stockInfoList) {
			System.out.println(entity);
		}
		return;
	}

}
