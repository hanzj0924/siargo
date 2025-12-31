@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

rem 设置MySQL安装路径
set MYSQL_PATH="D:\dev\program\mysql\bin"
rem 设置备份存储目录
set BACKUP_DIR="E:\eclipse-workspace\siargo\MySQL_Backups"
rem 设置MySQL用户名
set DB_USER=root
rem 设置MySQL密码
set DB_PASSWORD=123456
rem 设置要备份的数据库名称（备份所有数据库使用 --all-databases）
set DATABASE_NAME=siargo

rem 按日期创建子目录
set TODAY=%date:~0,4%%date:~5,2%%date:~8,2%
set BACKUP_PATH=%BACKUP_DIR%\%TODAY%

rem 如果目录不存在则创建
if not exist %BACKUP_PATH% mkdir %BACKUP_PATH%

rem 设置备份文件名（包含时间戳）
set TIME_STAMP=%time:~0,2%%time:~3,2%%time:~6,2%
set BACKUP_FILE=%BACKUP_PATH%\backup_%TODAY%_%TIME_STAMP%.sql

rem 备份单个数据库
echo 开始备份数据库 %DATABASE_NAME%...
%MYSQL_PATH%\mysqldump.exe -u%DB_USER% -p%DB_PASSWORD^" %DATABASE_NAME% > %BACKUP_FILE%

rem 检查备份是否成功
if %ERRORLEVEL% equ 0 (
    echo 备份成功: %BACKUP_FILE%
    
    rem 压缩备份文件（可选）
    rem 如果需要压缩，取消下面两行的注释
    rem echo 正在压缩备份文件...
    rem "%ProgramFiles%\7-Zip\7z.exe" a -tzip "%BACKUP_FILE%.zip" "%BACKUP_FILE%" && del "%BACKUP_FILE%"
    
    rem 删除超过30天的备份（可选）
    rem forfiles /p "%BACKUP_DIR%" /s /m *.sql /d -30 /c "cmd /c del @path"
) else (
    echo 备份失败!
    echo 错误代码: %ERRORLEVEL%
)

rem 记录日志
echo %date% %time% - 备份执行完成，状态: %ERRORLEVEL% >> "%BACKUP_DIR%\backup_log.txt"

endlocal