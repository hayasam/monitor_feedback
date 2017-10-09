#!/bin/bash

echo -e "\nCleaning and building Monitor Manager...\n"
./gradlew clean build --refresh-dependencies

cd ../monitors/twitter
echo -e "\nCleaning and building Twitter Monitor..\n"
./gradlew clean build --refresh-dependencies

cd ../appStore
echo -e "\nCleaning and building AppStore Monitor...\n"
./gradlew clean build --refresh-dependencies

cd ../googlePlay
echo -e "\nCleaning and building Google Play Monitor...\n"
./gradlew clean build --refresh-dependencies

cd ../HttpMonitoring
echo -e "\nCleaning and building HTTP Monitor...\n"
./gradlew clean build --refresh-dependencies

cd ../JsonLogs
echo -e "\nCleaning and building Json Logs Monitor...\n"
./gradlew clean build --refresh-dependencies

cd ../MonitoringUserEvents/PrjMonitoringUserEvents
echo -e "\nCleaning and building MonitoringUserEvents Monitor...\n"
mvn clean package 

cd ../../ConfigurationFileHTMLMonitor/
echo -e "\nCleaning and building ConfigurationFileHTML Monitor...\n"
mvn clean package

cd ../DiskMonitor
echo -e "\nCleaning and building DiskMonitor Monitor...\n"
cp /home/tomcat/.ssh/id_rsa src/main/webapp/ssh
./gradlew clean build --refresh-dependencies

