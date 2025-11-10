function New-DbSnapshot {
  param([string]$Db='ru_facil',[string]$User='ru_user',[string]$Pass='ru_pass',[string]$Container='ru_mysql')

  $ts   = Get-Date -Format yyyyMMdd-HHmm
  $dumpDir = "dumps"
  $dump = Join-Path $dumpDir ("{0}-{1}.sql" -f $Db,$ts)

  New-Item -ItemType Directory -Force -Path $dumpDir | Out-Null

  Write-Host ">> Gerando dump em $dump ..." -ForegroundColor Cyan
  docker exec -i $Container sh -c "mysqldump -u$User -p$Pass --single-transaction --routines --triggers --no-tablespaces $Db" > $dump

  if (!(Test-Path $dump) -or ((Get-Item $dump).Length -le 0)) {
    Write-Host "!! Dump não gerado (arquivo vazio)." -ForegroundColor Red
    return
  }

  Get-Item $dump | Select-Object FullName, Length | Format-Table | Out-String | Write-Host

  $branch = (git branch --show-current)
  if (-not $branch) { $branch = "master" }

  Write-Host ">> Commitando $dump na branch $branch ..." -ForegroundColor Cyan
  git add -f $dump
  git commit -m ("chore(db): snapshot {0} ({1})" -f $Db,$ts) | Out-Null
  git push -u origin $branch
  Write-Host "✔ Snapshot commitado e enviado." -ForegroundColor Green
}
