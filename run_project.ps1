
# ──────────────────────────────────────────────────────────
#  Student Management System – compile & run script
#  Uses a symlink junction (E:\SMS) to avoid spaces in path
# ──────────────────────────────────────────────────────────

$projectDir = "e:\Github Projects\Student-Management-System-Java-Swing"
$linkDir    = "e:\SMS"    # No spaces – used for javac argfile
$libDir     = "$projectDir\lib"
$buildDir   = "$projectDir\build\classes"
$dataDir    = "$projectDir\data"
$dbPath     = "$dataDir\schoolmanagment"

# ── 1. Create directories ──────────────────────────────────
foreach ($d in @($buildDir, $dataDir)) {
    if (-not (Test-Path $d)) { New-Item -ItemType Directory -Force -Path $d | Out-Null }
}

# ── 2. Create symlink junction (spaceless alias) ───────────
if (-not (Test-Path $linkDir)) {
    cmd /c "mklink /J `"$linkDir`" `"$projectDir`"" | Out-Null
    Write-Host "  Created junction: $linkDir -> $projectDir" -ForegroundColor DarkCyan
}

# ── 3. Build classpath ─────────────────────────────────────
$cp = (Get-ChildItem -Path $libDir -Filter *.jar | ForEach-Object { $_.FullName }) -join ";"
@(
    "C:\Program Files\Apache NetBeans\java\modules\ext\AbsoluteLayout.jar",
    "C:\Program Files\NetBeans\java\modules\ext\AbsoluteLayout.jar"
) | ForEach-Object { if ((Test-Path $_) -and ($cp -notlike "*AbsoluteLayout*")) { $script:cp += ";$_" } }

# ── 4. Compile ─────────────────────────────────────────────
Write-Host "Compiling source files..." -ForegroundColor Cyan

# Write sources list using the spaceless junction path
$sourcesFile = "$linkDir\sources_list.txt"
$javaFiles   = Get-ChildItem -Path "$linkDir\src" -Filter *.java | ForEach-Object { $_.FullName }
[System.IO.File]::WriteAllLines($sourcesFile, $javaFiles, [System.Text.Encoding]::ASCII)

Write-Host "  javac @$sourcesFile" -ForegroundColor DarkGray

$procInfo = New-Object System.Diagnostics.ProcessStartInfo
$procInfo.FileName               = "javac"
$procInfo.Arguments              = "-d `"$buildDir`" -cp `"$cp`" -encoding UTF-8 @$sourcesFile"
$procInfo.RedirectStandardOutput = $true
$procInfo.RedirectStandardError  = $true
$procInfo.UseShellExecute        = $false
$procInfo.WorkingDirectory       = $projectDir

$proc = [System.Diagnostics.Process]::Start($procInfo)
$stdout = $proc.StandardOutput.ReadToEnd()
$stderr = $proc.StandardError.ReadToEnd()
$proc.WaitForExit()

($stdout + $stderr) | Out-File -FilePath "$projectDir\compile_log.txt" -Encoding ASCII

if ($proc.ExitCode -ne 0) {
    Write-Host "Compilation FAILED:" -ForegroundColor Red
    ($stdout + $stderr) -split "`n" | Where-Object { $_ -match "error:" } | ForEach-Object { Write-Host "  $_" -ForegroundColor Red }
    Write-Host "See compile_log.txt for full details." -ForegroundColor Yellow
    exit 1
}

Write-Host "Compilation successful!" -ForegroundColor Green

# ── 5. Copy resources ──────────────────────────────────────
$imagesSource = "$projectDir\src\images"
$imagesDest   = "$buildDir\images"
if (Test-Path $imagesSource) {
    if (-not (Test-Path $imagesDest)) { New-Item -ItemType Directory -Force -Path $imagesDest | Out-Null }
    Copy-Item "$imagesSource\*" -Destination $imagesDest -Recurse -Force
}
Get-ChildItem -Path "$projectDir\src" -Include *.jrxml, *.jasper -File |
    ForEach-Object { Copy-Item $_.FullName -Destination $buildDir -Force }

# ── 6. Launch ──────────────────────────────────────────────
Write-Host "Launching Student Management System..." -ForegroundColor Cyan
Write-Host "  Database: $dbPath" -ForegroundColor DarkCyan

Set-Location $projectDir
$runCp = "$buildDir;$cp"

$runInfo = New-Object System.Diagnostics.ProcessStartInfo
$runInfo.FileName               = "java"
$runInfo.Arguments              = "-cp `"$runCp`" `"-Ddb.path=$dbPath`" login"
$runInfo.RedirectStandardOutput = $false
$runInfo.RedirectStandardError  = $false
$runInfo.UseShellExecute        = $false
$runInfo.WorkingDirectory       = $projectDir

Write-Host "  java $($runInfo.Arguments)" -ForegroundColor DarkGray
$runProc = [System.Diagnostics.Process]::Start($runInfo)
$runProc.WaitForExit()
