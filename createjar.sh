#!/bin/sh
cd /Users/vl474/Trabajo/NetBeans\ projects/subversionProjects/PowerAquaSVN/build/classes/
export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/ 
echo $JAVA_HOME
export CLASSPATH=$CLASSPATH:.
echo $CLASSPATH

$JAVA_HOME/bin/jar cvf core.jar poweraqua/core/tripleModel/ontologyTriple/*.class poweraqua/core/tripleModel/linguisticTriple/*.class poweraqua/core/model/myocmlmodel/*.class poweraqua/core/model/myrdfmodel/*.class poweraqua/core/model/myrdfmodel/constants/*.class poweraqua/core/plugin/*.class poweraqua/core/utils/*.class 
$JAVA_HOME/bin/jar cvf powerMap.jar poweraqua/powermap/elementPhase/*.class poweraqua/powermap/triplePhase/*.class poweraqua/powermap/stringMetrics/*.class poweraqua/powermap/mappingModel/*.class poweraqua/ranking/*.class 
$JAVA_HOME/bin/jar cvf wordnetJwnl.jar poweraqua/WordNetJWNL/*.class 
$JAVA_HOME/bin/jar cvf serviceConfig.jar poweraqua/serviceConfig/*.class 
$JAVA_HOME/bin/jar cvf indexingService.jar poweraqua/indexingService/*.class poweraqua/indexingService/creator/*.class poweraqua/indexingService/manager/*.class 
$JAVA_HOME/bin/jar cvf indexingServiceWatson.jar poweraqua/indexingService/manager/watson/*.class 
$JAVA_HOME/bin/jar cvf indexingServiceVirtuoso.jar poweraqua/indexingService/manager/virtuoso/*.class poweraqua/indexingService/manager/virtuoso/virtuosohelpers/*.class 
$JAVA_HOME/bin/jar cvf queryWords.jar poweraqua/query/*.class 
$JAVA_HOME/bin/jar cvf poweraquaDB.jar poweraquaDB/*.class 
$JAVA_HOME/bin/jar cvf linguistic.jar poweraqua/LinguisticComponent/*.class poweraqua/LinguisticComponent/QueryClassify/*.class 
$JAVA_HOME/bin/jar cvf fusion.jar poweraqua/fusion/*.class
$JAVA_HOME/bin/jar cvf lexicon.jar poweraqua/lexicon/*.class
$JAVA_HOME/bin/jar cvf yahooweb.jar poweraqua/yahoo/*.class  
$JAVA_HOME/bin/jar cvf yahooweb.jar poweraqua/yahoo/*.class    
$JAVA_HOME/bin/jar cvf essepuntato.jar it/essepuntato/trust/engine/exception/*.class it/essepuntato/trust/engine/*.class  
$JAVA_HOME/bin/jar cvf trustEngine.jar TrustEngine/*.class TrustEngine/userSession/*.class  
$JAVA_HOME/bin/jar cvfm WatsonPlugin.jar WatsonPlugin/manifestWatson WatsonPlugin/*.class 
$JAVA_HOME/bin/jar cvfm SesamePlugin.jar SesamePlugin/manifestSesame SesamePlugin/*.class 
$JAVA_HOME/bin/jar cvfm Sesame2Plugin.jar Sesame2Plugin/manifestSesame Sesame2Plugin/*.class 
$JAVA_HOME/bin/jar cvfm VirtuosoPlugin.jar virtuosoPlugin/manifestVirtuoso virtuosoPlugin/*.class virtuosoPlugin/virtuosoHelpers/*.class 
$JAVA_HOME/bin/jar cvfm RemoteSPARQLPlugin.jar RemoteSPARQLPlugin/manifestRemoteSPARQL RemoteSPARQLPlugin/*.class
$JAVA_HOME/bin/jar cvf sealsWrapper.jar eu/sealsproject/domain/sst/*.class 
