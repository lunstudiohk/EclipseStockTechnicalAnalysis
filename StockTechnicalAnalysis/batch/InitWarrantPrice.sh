#!/bin/bash

#export ROOT=/Project/StockTechnicalAnalysis
export ROOT=/Volumes/Project/Data/Project/StockAnalysis/Eclipse/StockTechnicalAnalysis/StockTechnicalAnalysis

export LIB=$ROOT/lib
export RESOURCES=$ROOT/resources

export CLASSPATH=$ROOT/bin
export CLASSPATH=$CLASSPATH:$LIB/activation-1.1.jar
export CLASSPATH=$CLASSPATH:$LIB/antlr-2.7.7.jar
export CLASSPATH=$CLASSPATH:$LIB/aspectjtools-1.8.10.jar
export CLASSPATH=$CLASSPATH:$LIB/aspectjweaver-1.8.9.jar
export CLASSPATH=$CLASSPATH:$LIB/byte-buddy-1.6.6.jar
export CLASSPATH=$CLASSPATH:$LIB/classmate-1.3.0.jar
export CLASSPATH=$CLASSPATH:$LIB/commons-codec-1.9.jar
export CLASSPATH=$CLASSPATH:$LIB/commons-dbcp2-2.1.1.jar
export CLASSPATH=$CLASSPATH:$LIB/commons-lang3-3.0.jar
export CLASSPATH=$CLASSPATH:$LIB/commons-logging-1.2.jar
export CLASSPATH=$CLASSPATH:$LIB/commons-math3-3.6.1.jar
export CLASSPATH=$CLASSPATH:$LIB/commons-pool2-2.4.2.jar
export CLASSPATH=$CLASSPATH:$LIB/dom4j-1.6.1.jar
export CLASSPATH=$CLASSPATH:$LIB/firebase-admin-5.2.0.jar
export CLASSPATH=$CLASSPATH:$LIB/google-api-client-1.22.0.jar
export CLASSPATH=$CLASSPATH:$LIB/google-api-client-gson-1.22.0.jar
export CLASSPATH=$CLASSPATH:$LIB/google-http-client-1.22.0.jar
export CLASSPATH=$CLASSPATH:$LIB/google-http-client-gson-1.22.0.jar
export CLASSPATH=$CLASSPATH:$LIB/google-http-client-jackson2-1.22.0.jar
export CLASSPATH=$CLASSPATH:$LIB/google-oauth-client-1.22.0.jar
export CLASSPATH=$CLASSPATH:$LIB/gson-2.1.jar
export CLASSPATH=$CLASSPATH:$LIB/guava-20.0.jar
export CLASSPATH=$CLASSPATH:$LIB/hibernate-commons-annotations-5.0.1.Final.jar
export CLASSPATH=$CLASSPATH:$LIB/hibernate-core-5.2.10.Final.jar
export CLASSPATH=$CLASSPATH:$LIB/hibernate-entitymanager-5.2.10.Final.jar
export CLASSPATH=$CLASSPATH:$LIB/hibernate-jpa-2.1-api-1.0.0.Final.jar
export CLASSPATH=$CLASSPATH:$LIB/httpclient-4.5.3.jar
export CLASSPATH=$CLASSPATH:$LIB/httpcore-4.4.6.jar
export CLASSPATH=$CLASSPATH:$LIB/httpunit.jar
export CLASSPATH=$CLASSPATH:$LIB/jackson-core-2.1.3.jar
export CLASSPATH=$CLASSPATH:$LIB/jandex-2.0.3.Final.jar
export CLASSPATH=$CLASSPATH:$LIB/javassist-3.20.0-GA.jar
export CLASSPATH=$CLASSPATH:$LIB/jaxb-api-2.4.0-b180830.0359.jar
export CLASSPATH=$CLASSPATH:$LIB/jaxb-runtime-2.4.0-b180830.0438.jar
export CLASSPATH=$CLASSPATH:$LIB/jboss-logging-3.3.0.Final.jar
export CLASSPATH=$CLASSPATH:$LIB/jboss-transaction-api_1.2_spec-1.0.1.Final.jar
export CLASSPATH=$CLASSPATH:$LIB/js-1.6R5.jar
export CLASSPATH=$CLASSPATH:$LIB/json-20170516.jar
export CLASSPATH=$CLASSPATH:$LIB/json-simple-1.1.1.jar
export CLASSPATH=$CLASSPATH:$LIB/jsoup-1.10.3.jar
export CLASSPATH=$CLASSPATH:$LIB/jsr305-1.3.9.jar
export CLASSPATH=$CLASSPATH:$LIB/jtidy-4aug2000r7-dev.jar
export CLASSPATH=$CLASSPATH:$LIB/junit-3.8.1.jar
export CLASSPATH=$CLASSPATH:$LIB/log4j-api-2.8.2.jar
export CLASSPATH=$CLASSPATH:$LIB/log4j-core-2.8.2.jar
export CLASSPATH=$CLASSPATH:$LIB/mail-1.4.jar
export CLASSPATH=$CLASSPATH:$LIB/mysql-connector-java-6.0.6.jar
export CLASSPATH=$CLASSPATH:$LIB/nekohtml-0.9.5.jar
export CLASSPATH=$CLASSPATH:$LIB/servlet-api-2.4.jar
export CLASSPATH=$CLASSPATH:$LIB/slf4j-api-1.7.25.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-aop-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-aspects-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-beans-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-context-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-core-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-expression-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-jdbc-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-orm-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/spring-tx-4.3.9.RELEASE.jar
export CLASSPATH=$CLASSPATH:$LIB/xercesImpl-2.6.1.jar
export CLASSPATH=$CLASSPATH:$LIB/xmlParserAPIs-2.6.1.jar

java -Dspring.config=file:$RESOURCES/Spring-Config.xml -Duser.timezone=Asia/Hong_Kong -Xmx128m -Dlog4j.configurationFile=$RESOURCES/log4j2.xml com.lunstudio.stocktechnicalanalysis.init.InitCbbcPrice $1

unset CLASSPATH
