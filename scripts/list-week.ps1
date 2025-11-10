<# 
Uso:
  pwsh -File scripts/list-week.ps1 -StartDate 2025-11-10
Parâmetros opcionais:
  -BaseUrl http://localhost:8080
#>

param(
  [Parameter(Mandatory=$true)][string]$StartDate,
  [string]$BaseUrl = 'http://localhost:8080'
)

function Show-Day {
  param([string]$Date, [string]$Meal)
  $url = ('{0}/api/v1/menu/{1}?meal={2}' -f $BaseUrl.TrimEnd('/'), $Date, $Meal)
  $resp = Invoke-RestMethod -Uri $url -Method GET
  Write-Host "=== $Date $Meal ===" -ForegroundColor Yellow
  if ($resp -and $resp.slots) {
    foreach ($s in $resp.slots) {
      "{0,-20} | {1}" -f $s.slot, $s.title
    }
  } else {
    "(sem itens)"
  }
  ""
}

$d0 = [datetime]::ParseExact($StartDate,'yyyy-MM-dd',$null)
for ($i = 0; $i -lt 5; $i++) {
  $dStr = $d0.AddDays($i).ToString('yyyy-MM-dd')
  Show-Day -Date $dStr -Meal 'ALMOCO'
  Show-Day -Date $dStr -Meal 'JANTAR'
}
