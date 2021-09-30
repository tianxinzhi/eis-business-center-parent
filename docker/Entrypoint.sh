#!/bin/bash
#echo "success"
#nohup java -Xms1g -Xmx1g -Xmn512m -Xss256k
env=$1
#java -Xms1g -Xmx2g -Xmn512m -Xss256k -Djava.security.egd=file:/dev/./urandom -jar /usr/local/apps/*.jar --spring.profiles.active=$env
java -XX:MetaspaceSize=56m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx512m -Xmn110m -Xss256k -XX:SurvivorRatio=8 -Djava.security.egd=file:/dev/./urandom -jar /usr/local/apps/*.jar --spring.profiles.active=$env
