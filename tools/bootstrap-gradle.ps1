param(
    [Parameter(Mandatory = $true)]
    [string]$Version
)
$ErrorActionPreference = 'Stop'
$gradleUserHome = if ($env:GRADLE_USER_HOME) { $env:GRADLE_USER_HOME } else { Join-Path $env:USERPROFILE '.gradle' }
$root = Join-Path $gradleUserHome 'homecash-distributions'
$zip = Join-Path $root "gradle-$Version-bin.zip"
$shaFile = "$zip.sha256"
$distributionUrl = "https://services.gradle.org/distributions/gradle-$Version-bin.zip"
$expectedDirectory = Join-Path $root "gradle-$Version"
New-Item -ItemType Directory -Force -Path $root | Out-Null

try {
    Invoke-WebRequest -Uri "$distributionUrl.sha256" -OutFile $shaFile
    $expected = (Get-Content $shaFile -Raw).Trim().Split(' ')[0].ToLowerInvariant()
    Invoke-WebRequest -Uri $distributionUrl -OutFile $zip
    $actual = (Get-FileHash $zip -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $expected) {
        throw 'Gradle distribution SHA-256 verification failed.'
    }
    Expand-Archive -Path $zip -DestinationPath $root -Force
    if (-not (Test-Path (Join-Path $expectedDirectory 'bin/gradle.bat'))) {
        throw 'Gradle archive did not contain the expected executable.'
    }
}
finally {
    Remove-Item $zip, $shaFile -Force -ErrorAction SilentlyContinue
}
