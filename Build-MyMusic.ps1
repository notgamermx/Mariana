param(
    [Parameter(Mandatory = $true)][string]$JavaPath,
    [Parameter(Mandatory = $true)][string]$SdkPath,
    [switch]$RunTests,
    [switch]$Hardened
)
$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = (Resolve-Path -LiteralPath $JavaPath).Path
$env:ANDROID_HOME = (Resolve-Path -LiteralPath $SdkPath).Path
$env:METROLIST_APPLICATION_ID = 'app.personal.mymusic'
$env:METROLIST_APP_NAME = 'My Music'
$env:METROLIST_DEBUG_KEYSTORE_PATH = Join-Path $PSScriptRoot 'app/persistent-debug.keystore'
if (-not (Test-Path -LiteralPath $env:METROLIST_DEBUG_KEYSTORE_PATH)) {
    & (Join-Path $env:JAVA_HOME 'bin/keytool.exe') -genkeypair -keystore $env:METROLIST_DEBUG_KEYSTORE_PATH -storepass android -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=My Music Personal Build'
    if ($LASTEXITCODE -ne 0) { throw 'Could not generate the personal build key.' }
}
Push-Location $PSScriptRoot
try {
    $musicVariant = if ($Hardened) { 'FossHardened' } else { 'FossDebug' }
    $musicTasks = @(':app:assemble' + $musicVariant)
    if ($RunTests) { $musicTasks += ':app:testFossDebugUnitTest'; $musicTasks += ':innertube:test' }
    if ($Hardened) { $musicTasks += ':app:assembleFossDebug' }
    & ./gradlew.bat @musicTasks --no-daemon
    if ($LASTEXITCODE -ne 0) { throw 'Android build failed.' }
} finally {
    Pop-Location
}
