
# Script to compile and run the Student Management System

$projectDir = "e:\Github Projects\Student-Management-System-Java-Swing"
$srcDir = "$projectDir\src"
$libDir = "$projectDir\lib"
$buildDir = "$projectDir\build\classes"
$netbeansLib = "C:\Program Files\Apache NetBeans\java\modules\ext\AbsoluteLayout.jar"

# Create necessary directories
if (-not (Test-Path $buildDir)) {
    New-Item -ItemType Directory -Force -Path $buildDir
}
if (-not (Test-Path "$projectDir\data")) {
    New-Item -ItemType Directory -Force -Path "$projectDir\data"
}

Write-Host "Compiling source files..." -ForegroundColor Cyan

# Create a temporary file list for javac (ASCII to avoid BOM, quoted for spaces, forward slashes for comfort)
$sourcesFile = "$env:TEMP\sources.txt"
Get-ChildItem -Path "$srcDir" -Filter *.java | ForEach-Object { "`"$($_.FullName.Replace('\', '/'))`"" } | Out-File -FilePath $sourcesFile -Encoding ASCII

# Build the initial classpath from the lib directory
$libs = Get-ChildItem -Path $libDir -Filter *.jar
$cp = ($libs | ForEach-Object { "$($_.FullName)" }) -join ";"

# Build the final classpath
$finalCp = "$cp"
if (Test-Path $netbeansLib) {
    if ($finalCp) { $finalCp += ";" }
    $finalCp += "$netbeansLib"
}

# Run javac with the @argfile (quote the whole @path for spaces)
javac -d "$buildDir" -cp "$finalCp" -encoding UTF-8 "@$sourcesFile" 2>&1 | Out-File -FilePath compile_log.txt -Encoding ASCII

# Cleanup
if (Test-Path $sourcesFile) {
    # Remove-Item $sourcesFile # Commented out for debugging if needed
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
    Write-Host "Running application..." -ForegroundColor Cyan
    
    # Run from the build directory, adding it to the classpath
    $runCp = "$buildDir;$cp"
    
    # Launch the application
    java -cp "$runCp" login
} else {
    Write-Host "Compilation failed with exit code $LASTEXITCODE" -ForegroundColor Red
}
