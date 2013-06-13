@echo off
cd..
echo [INFO] Éú³Éwar°ü
call mvn clean compile war:war -Dmaven.test.skip=true
pause