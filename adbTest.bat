set NAME=LSPosed-v1.0-7180-zygisk-release.zip
@REM set NAME=LSPosed-v1.0-7180-zygisk-debug.zip

.\gradlew zipAll | find "BUILD SUCCESSFUL" >nul

if %errorlevel% equ 0 (
    echo BUILD SUCCESSFUL, pushing zip...
    adb shell su -c "rm /data/local/tmp/%NAME%"
    adb push "D:\AndroidProjects\sb\LSPosed\magisk-loader\release\%NAME%" "/data/local/tmp"
    adb shell su -c "magisk --install-module /data/local/tmp/%NAME%"
    adb reboot
)



@REM adb shell su -c "am broadcast --user 0 -a com.android.system.modules.update"

@REM adb shell su -c "kill -9 $(ps -ef | grep "com.android.systemui" | grep -v "grep" | awk '{print $2}')"
