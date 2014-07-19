@ECHO OFF

cmd /c gradlew -info clean setupDecompWorkSpace eclipse %*

pause
