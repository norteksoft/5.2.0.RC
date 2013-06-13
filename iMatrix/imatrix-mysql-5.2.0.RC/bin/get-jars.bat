@echo off
cd..
echo [INFO] 使用maven根据pom.xml 复制依赖jar到/WEB-INF/lib
call mvn clean dependency:copy-dependencies -e

pause