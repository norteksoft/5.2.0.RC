@echo off
cd..
echo [INFO] 生成jar包并上传jar包到Nexus
call mvn clean deploy -Dmaven.test.skip=true
pause