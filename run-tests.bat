@echo off
echo ========================================
echo 订单管理系统 - 测试套件运行脚本
echo ========================================
echo.

echo 正在清理之前的测试结果...
if exist target\test-results rmdir /s /q target\test-results
if exist target\surefire-reports rmdir /s /q target\surefire-reports

echo.
echo 正在运行单元测试...
call mvn test -Dtest=*Test -DfailIfNoTests=false

echo.
echo 正在运行集成测试...
call mvn test -Dtest=*IntegrationTest -DfailIfNoTests=false

echo.
echo 正在生成测试报告...
call mvn surefire-report:report

echo.
echo 测试完成！
echo 测试报告位置: target\site\surefire-report.html
echo.

pause 