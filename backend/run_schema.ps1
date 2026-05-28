param(
    [string]$MySqlExePath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    [string]$User = "root",
    [string]$Password = "",
    [string]$DatabaseFile = "src/main/resources/db/migration/V1__init_schema.sql"
)

if (-not (Test-Path $MySqlExePath)) {
    Write-Error "MySQL executable not found at $MySqlExePath"
    exit 1
}

$fullPath = Join-Path -Path (Get-Location) -ChildPath $DatabaseFile
if (-not (Test-Path $fullPath)) {
    Write-Error "SQL file not found: $fullPath"
    exit 1
}

$pwdArg = if ($Password -ne "") { "-p$Password" } else { "" }

Write-Host "Executing schema script: $fullPath"
Get-Content -Raw -Path $fullPath | & "$MySqlExePath" -u $User $pwdArg
if ($LASTEXITCODE -eq 0) {
    Write-Host "Schema executed successfully."
} else {
    Write-Error "Schema execution failed with exit code $LASTEXITCODE."
    exit $LASTEXITCODE
}
