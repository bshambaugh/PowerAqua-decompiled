#!/bin/sh
 CLASSPATH=$CLASSPATH:.:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/Facility.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/KVDB.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/Taxonomy-1.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/VirtuosoPlugin.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/a-openrdf-sesame-2.3-pr1-onejar.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/ab-slf4j-log4j12-1.5.8.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/activation-1.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-appbase-core-3.4.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-appbase-logging-api-3.4.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-appbase-logging-file-3.4.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-appbase-webapp-base-core-3.4.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-collections-2.3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-concurrent-2.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-i18n-1.0.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-io-2.4.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-iteration-2.3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-lang-2.3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-net-2.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-platform-info-2.4.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-text-2.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-webapp-core-2.4.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aduna-commons-xml-2.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/antlr-2.7.5.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/aopalliance-1.0.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/arq.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/asm-1.5.3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/axis.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/cglib-2.1_3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-cli-1.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-codec-1.3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-dbcp-1.2.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-discovery-0.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-fileupload-1.2.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-httpclient-3.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-io-1.3.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-logging-1.0.4.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-logging.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/commons-pool-1.3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/concurrent.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/core.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/essepuntato.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/fusion.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/gate3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/icu4j_3_4.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/indexingService.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/indexingServiceVirtuoso.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/indexingServiceWatson.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/iri.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jasper-compiler-jdt.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jaws-bin.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jaxrpc.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jazzlib-binary-0.07-juz.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jcl-over-slf4j-1.5.8.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jcl104-over-slf4j-1.5.0.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jdom.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jena.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jenatest.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/json.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jstl-1.1.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/junit-4.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/junit.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/jwnl.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/linguistic.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/log4j-1.2.15.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/log4j-over-slf4j-1.5.8.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/logback-core-0.9.9.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/lucene-core-2.4.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/lucene-spellchecker-2.1.0.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/mysql-connector-java-3.1.11-bin.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/ontotext.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/openrdf-model.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/openrdf-util.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/out:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/powerMap.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/poweraquaDB.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/queryWords.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/ranking.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/rio.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/s-xmldocumenthandler.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/saaj.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/sealsWrapper.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/secondstring-20030401.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/serviceConfig.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/sesame.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/slf4j-api-1.5.8.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/spring-aop-2.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/spring-beans-2.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/spring-context-2.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/spring-context-support-2.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/spring-core-2.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/spring-web-2.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/spring-webmvc-2.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/standard-1.1.2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/stax-api-1.0.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/tests.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/tripleModel.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/trustEngine.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/utilities.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/virtjdbc3.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/virtjdbc3ssl.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/watson_client_api_v2.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/wordnetJwnl.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/wsdl4j-1.5.1.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/wstx-asl-2.8.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/xercesImpl.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/xml-apis.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/xmlParserAPIs.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/yahoo.jar:/Applications/apache-tomcat-5.5.23/webapps/poweraqualinked/WEB-INF/lib/yahooweb.jar

export CLASSPATH
echo $CLASSPATH

java -Xmx1000M poweraqua.indexingService.creator.IndexingCreator "UPDATE_NEW"