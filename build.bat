@echo off
chcp 65001 >nul
echo ========================================
echo   Windows 防休眠工具 - 打包脚本
echo ========================================
echo.

:: 检查 Python
python --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到 Python，请先安装 Python 3.8+
    pause
    exit /b 1
)

:: 安装依赖
echo [1/2] 安装依赖...
pip install pyinstaller pystray Pillow -q
if errorlevel 1 (
    echo [错误] 依赖安装失败
    pause
    exit /b 1
)

:: 打包
echo [2/2] 打包为 exe...
pyinstaller --onefile --noconsole --name "AntiSleep" anti_sleep.py
if errorlevel 1 (
    echo [错误] 打包失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo   打包完成！
echo   exe 文件位于: dist\AntiSleep.exe
echo ========================================

:: 清理打包产生的临时文件
rmdir /s /q build 2>nul
del AntiSleep.spec 2>nul

echo 已清理临时文件（build 目录和 .spec 文件）
echo.
pause
