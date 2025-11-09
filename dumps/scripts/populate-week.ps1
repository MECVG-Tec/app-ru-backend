<# 
Uso:
  pwsh -File scripts/populate-week.ps1 -StartDate 2025-11-10
Parâmetros opcionais:
  -BaseUrl http://localhost:8080
  -AuthPair admin:1234

Obs: Edite os cardápios em $Menus abaixo.
#>

param(
  [Parameter(Mandatory=$true)][string]$StartDate,            # ex: 2025-11-10 (segunda)
  [string]$BaseUrl = 'http://localhost:8080',
  [string]$AuthPair = 'admin:1234'
)

# ===== Auth header =====
$Headers = @{ Authorization = 'Basic ' + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($AuthPair)) }

# ===== Helpers =====
function Set-Menu {
  param(
    [Parameter(Mandatory=$true)][string]$Date,   # yyyy-MM-dd
    [Parameter(Mandatory=$true)][string]$Meal,   # ALMOCO | JANTAR
    [Parameter(Mandatory=$true)][array] $Items
  )
  $url  = ('{0}/api/v1/menu/{1}?meal={2}' -f $BaseUrl.TrimEnd('/'), $Date, $Meal)
  $json = @{ items = $Items } | ConvertTo-Json -Depth 6
  $tmp  = Join-Path $env:TEMP ("menu-{0}-{1}.json" -f $Date, $Meal)
  Set-Content -Path $tmp -Value $json -Encoding UTF8
  Invoke-RestMethod -Uri $url -Headers $Headers -Method PUT -ContentType 'application/json; charset=utf-8' -InFile $tmp
}

# ===== Cardápios da semana (edite à vontade) =====
# Índices: 0=Seg,1=Ter,2=Qua,3=Qui,4=Sex
$Menus = @{
  ALMOCO = @(
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Carne de panela' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Frango grelhado ao alho' },
      @{ slot='LEVE_SABOR';         title='Peito de frango com legumes' },
      @{ slot='SELECT';             title='Escondidinho de charque' },
      @{ slot='VEGETARIANO';        title='Quibe de abóbora' },
      @{ slot='GUARINCAO';          title='Arroz branco / feijão carioca / macarrão alho e óleo' },
      @{ slot='SALADA_CRUA';        title='Alface, tomate e cenoura' },
      @{ slot='SALADA_COZIDA';      title='Beterraba cozida' },
      @{ slot='SOBREMESA';          title='Mamão' },
      @{ slot='SUCO';               title='Acerola / Laranja' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Suíno agridoce com abacaxi' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Peixe ao forno com ervas' },
      @{ slot='LEVE_SABOR';         title='Frango cozido com legumes' },
      @{ slot='SELECT';             title='Arrumadinho' },
      @{ slot='VEGETARIANO';        title='Ratatouille' },
      @{ slot='GUARINCAO';          title='Arroz colorido / feijão preto / farofa simples' },
      @{ slot='SALADA_CRUA';        title='Americana e repolho' },
      @{ slot='SALADA_COZIDA';      title='Abobrinha salteada' },
      @{ slot='SOBREMESA';          title='Mousse de maracujá' },
      @{ slot='SUCO';               title='Maracujá / Goiaba' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Frango xadrez' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Carne assada ao molho' },
      @{ slot='LEVE_SABOR';         title='Tilápia grelhada' },
      @{ slot='SELECT';             title='Virado paulista' },
      @{ slot='VEGETARIANO';        title='Panqueca de espinafre' },
      @{ slot='GUARINCAO';          title='Arroz / feijão mulatinho / batata sauté' },
      @{ slot='SALADA_CRUA';        title='Acelga e cenoura ralada' },
      @{ slot='SALADA_COZIDA';      title='Chuchu com ervas' },
      @{ slot='SOBREMESA';          title='Maçã' },
      @{ slot='SUCO';               title='Caju / Acerola' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Carne ao molho barbecue' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Frango ao curry' },
      @{ slot='LEVE_SABOR';         title='Filé de peixe com limão' },
      @{ slot='SELECT';             title='Escondidinho de frango' },
      @{ slot='VEGETARIANO';        title='Berinjela à parmegiana' },
      @{ slot='GUARINCAO';          title='Arroz integral / feijão carioca / legumes salteados' },
      @{ slot='SALADA_CRUA';        title='Alface, rúcula e tomate' },
      @{ slot='SALADA_COZIDA';      title='Vagem cozida' },
      @{ slot='SOBREMESA';          title='Pudim' },
      @{ slot='SUCO';               title='Goiaba / Maracujá' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Frango ao molho branco com mostarda' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Cupim acebolado' },
      @{ slot='LEVE_SABOR';         title='Peito de frango grelhado' },
      @{ slot='SELECT';             title='Arrumadinho' },
      @{ slot='VEGETARIANO';        title='Ratatouille' },
      @{ slot='GUARINCAO';          title='Arroz / feijão preto / farofa de cebola' },
      @{ slot='SALADA_CRUA';        title='Americana, tomate e cenoura' },
      @{ slot='SALADA_COZIDA';      title='Abóbora cozida' },
      @{ slot='SOBREMESA';          title='Melão' },
      @{ slot='SUCO';               title='Limão / Acerola' }
    )
  );
  JANTAR = @(
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Bife acebolado' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Frango ao molho mostarda' },
      @{ slot='LEVE_SABOR';         title='Filé de frango grelhado' },
      @{ slot='SELECT';             title='Talharim ao sugo' },
      @{ slot='VEGETARIANO';        title='Grão-de-bico ao molho' },
      @{ slot='GUARINCAO';          title='Purê de batata' },
      @{ slot='SOPA';               title='Canja' },
      @{ slot='SALADA_CRUA';        title='Mix de folhas' },
      @{ slot='SOBREMESA';          title='Gelatina' },
      @{ slot='SUCO';               title='Uva / Caju' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Strogonoff de frango' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Carne ao molho madeira' },
      @{ slot='LEVE_SABOR';         title='Frango grelhado com pimentões' },
      @{ slot='SELECT';             title='Nhoque ao sugo' },
      @{ slot='VEGETARIANO';        title='Almôndega de lentilha' },
      @{ slot='GUARINCAO';          title='Cuscuz temperado' },
      @{ slot='SOPA';               title='Sopa de legumes' },
      @{ slot='SALADA_CRUA';        title='Alface roxa e tomate' },
      @{ slot='SOBREMESA';          title='Banana' },
      @{ slot='SUCO';               title='Manga / Limão' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Iscas de carne aceboladas' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Frango gratinado' },
      @{ slot='LEVE_SABOR';         title='Peito de frango grelhado' },
      @{ slot='SELECT';             title='Macarronada bolonhesa' },
      @{ slot='VEGETARIANO';        title='Refogado de soja' },
      @{ slot='GUARINCAO';          title='Purê de macaxeira' },
      @{ slot='SOPA';               title='Sopa de feijão' },
      @{ slot='SALADA_CRUA';        title='Folhas com vinagrete' },
      @{ slot='SOBREMESA';          title='Melancia' },
      @{ slot='SUCO';               title='Laranja / Uva' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Bife à parmegiana' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Frango ao forno com ervas' },
      @{ slot='LEVE_SABOR';         title='Frango grelhado com legumes' },
      @{ slot='SELECT';             title='Fusilli ao pesto' },
      @{ slot='VEGETARIANO';        title='Quibe de quinoa' },
      @{ slot='GUARINCAO';          title='Purê de abóbora' },
      @{ slot='SOPA';               title='Caldo verde' },
      @{ slot='SALADA_CRUA';        title='Mix de folhas e pepino' },
      @{ slot='SOBREMESA';          title='Laranja' },
      @{ slot='SUCO';               title='Caju / Manga' }
    ),
    @(
      @{ slot='PRATO_PRINCIPAL_1'; title='Carne guisada' },
      @{ slot='PRATO_PRINCIPAL_2'; title='Frango ao roti' },
      @{ slot='LEVE_SABOR';         title='Iscas de frango grelhadas' },
      @{ slot='SELECT';             title='Talharim à bolonhesa' },
      @{ slot='VEGETARIANO';        title='Escondidinho de lentilha' },
      @{ slot='GUARINCAO';          title='Macarrão alho e óleo' },
      @{ slot='SOPA';               title='Sopa de macarrão' },
      @{ slot='SALADA_CRUA';        title='Folhas com cenoura' },
      @{ slot='SOBREMESA';          title='Banana' },
      @{ slot='SUCO';               title='Uva / Goiaba' }
    )
  )
}

# ===== Execução =====
$d0 = [datetime]::ParseExact($StartDate,'yyyy-MM-dd',$null)
for ($i = 0; $i -lt 5; $i++) {
  $dStr = $d0.AddDays($i).ToString('yyyy-MM-dd')
  Write-Host "Populando $dStr (ALMOCO/JANTAR)..." -ForegroundColor Cyan
  Set-Menu -Date $dStr -Meal 'ALMOCO' -Items $Menus.ALMOCO[$i]
  Set-Menu -Date $dStr -Meal 'JANTAR' -Items $Menus.JANTAR[$i]
}
Write-Host "OK! Semana populada a partir de $StartDate." -ForegroundColor Green
