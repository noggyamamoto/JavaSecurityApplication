Get-ChildItem -Path 'C:\JavaSecurityApplication\session-hijacking-demo\src\main\java' -Recurse -Filter *.java | ForEach-Object {
    $p = $_.FullName
    $b = [System.IO.File]::ReadAllBytes($p)
    if ($b.Length -ge 3 -and $b[0] -eq 0xEF -and $b[1] -eq 0xBB -and $b[2] -eq 0xBF) {
        $new = $b[3..($b.Length - 1)]
        [System.IO.File]::WriteAllBytes($p, $new)
        Write-Host ('Removed BOM: ' + $p)
    }
}