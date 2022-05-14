#!/bin/bash

TOMCAT_PATH='/home/miaomiao/tomcat8'
EXAM_PATH='/home/miaomiao/Exam'

# get classpath
CP=${TOMCAT_PATH}/lib/servlet-api.jar

# 使用for循环，将webapps/ROOT/WEB-INF/lib下的所有jar包的文件名一一取出
# 并追加到变量CP中


# compile
javac -classpath $CP ${EXAM_PATH}/code/*.java -d ${EXAM_PATH}/target

if [ $? -ne 0 ]
then
   echo 'complie error'
   exit
fi

# copy classes
# 删除目录 webapps/ROOT/WEB-INF/classes
# 将target目录拷贝到 webapps/ROOT/WEB-INF/classes

# restart tomcat
# 重启tomcat
