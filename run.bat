::@echo off
::将结果输出到屏幕
::java -jar 21vReport.jar

::输出最原本程序生成的数据
::java -cp 21vReport.jar ReportGenerator > 运行结果.txt

::将结果输出到 文件
java -jar 21vReport.jar > 运行结果.txt

::提示输出
::echo 运行结果已保存至 → “运行结果.txt”

::打开输出结果,然后关闭批处理程序窗口
start 运行结果.txt
