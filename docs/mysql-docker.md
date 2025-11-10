
````markdown
# RU Fácil — Backend (Dev Quickstart)

Guia rápido para subir o **MySQL (Docker)**, iniciar a **API (Spring Boot)**, **popular** a semana, **consultar** cardápios e **fazer backup/restore**.

> **Stack**: Java 17 • Spring Boot • Spring Data JPA • MySQL 8 (Docker)  
> **Perfis**: `dev` (local)

---

## Pré-requisitos

- **Docker** e **Docker Compose** (testado com Compose v2)
- **Java 17+** (JDK)  
- **Maven Wrapper** (já no repo: `mvnw` / `mvnw.cmd`)
- **PowerShell** (Windows) ou **bash** (Linux/macOS)

---

## 1) Subir o banco (MySQL via Docker)

```bash
docker compose up -d
docker ps   # deve listar o container ru_mysql UP (porta 3307->3306)
````

Credenciais padrão (arquivo `docker-compose.yml`):

* DB: `ru_facil`
* User/Pass: `ru_user` / `ru_pass`
* Root: `root` / `rootpass`

---

## 2) Rodar a API (perfil `dev`)

**Windows (PowerShell):**

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
.\mvnw.cmd spring-boot:run
```

**Linux/macOS (bash):**

```bash
./mvnw -Dspring-boot.run.profiles=dev spring-boot:run
```

Aguarde a mensagem: `Started RuFacilApplication`

**Health check**

```bash
curl http://localhost:8080/health
# OK
```

---

## 3) Endpoints principais

### GET — cardápio do dia

```
GET /api/v1/menu/{yyyy-mm-dd}?meal=ALMOCO|JANTAR
```

Exemplos:

```bash
curl "http://localhost:8080/api/v1/menu/2025-11-10?meal=ALMOCO"
curl "http://localhost:8080/api/v1/menu/2025-11-10?meal=JANTAR"
```

### PUT — sobrescrever cardápio do dia

* Protegido com **Basic Auth**: `admin` / `1234`
* Corpo esperado:

```json
{
  "items": [
    { "slot": "PRATO_PRINCIPAL_1", "title": "Frango ao molho branco com mostarda", "notes": null }
  ]
}
```

**Windows (PowerShell)** — usando arquivo UTF-8 (evita erro de acentos):

```powershell
$hdrs = @{ Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("admin:1234")) }
$tmp = "$env:TEMP\menu.json"
@{ items = @(@{ slot='PRATO_PRINCIPAL_1'; title='Frango ao molho branco com mostarda' }) } |
  ConvertTo-Json -Depth 6 | Set-Content -Path $tmp -Encoding UTF8

Invoke-RestMethod "http://localhost:8080/api/v1/menu/2025-11-10?meal=ALMOCO" `
  -Headers $hdrs -Method PUT -ContentType 'application/json; charset=utf-8' -InFile $tmp
```

**Linux/macOS (bash + curl):**

```bash
printf '%s' '{"items":[{"slot":"PRATO_PRINCIPAL_1","title":"Frango ao molho branco com mostarda"}]}' > /tmp/menu.json
curl -u admin:1234 -H "Content-Type: application/json; charset=utf-8" \
     -X PUT --data-binary "@/tmp/menu.json" \
     "http://localhost:8080/api/v1/menu/2025-11-10?meal=ALMOCO"
```

---

## 4) Scripts úteis (PowerShell)

> Pasta: `scripts/`

* **Popular semana** (usa os exemplos da semana atual que colocamos):

  ```powershell
  ./scripts/populate-week.ps1
  ```
* **Listar semana** (imprime os dias/slots):

  ```powershell
  ./scripts/list-week.ps1
  ```
* **Dump e commit automático** (gera SQL em `dumps/` e comita):

  ```powershell
  ./scripts/dump-and-commit.ps1
  ```

> Dicas:
>
> * Se o PowerShell bloquear execução:
>   `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`
> * Caso seu terminal não esteja em UTF-8, antes de popular:
>
>   ```powershell
>   chcp 65001 > $null
>   $OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::new($false)
>   ```

---

## 5) Backup & Restore

Documento detalhado: **[`docs/backup-restore.md`](./backup-restore.md)**

Atalho rápido (PowerShell):

**Backup**

```powershell
$ts = Get-Date -Format yyyyMMdd-HHmm
New-Item -ItemType Directory -Force -Path dumps | Out-Null
docker exec -i ru_mysql sh -c "mysqldump -uru_user -pru_pass --single-transaction --routines --triggers ru_facil" > "dumps/ru_facil-$ts.sql"
```

**Restore (dump mais recente)**

```powershell
$latest = Get-ChildItem dumps -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1
docker exec -i ru_mysql sh -c "mysql -uru_user -pru_pass ru_facil" < $latest.FullName
```

---

## 6) Modelo de dados (resumo)

Tabela **`daily_menu_entries`**
Chave única: **(date, meal, slot)**

Campos principais:

* `date` (YYYY-MM-DD)
* `meal` (`ALMOCO` | `JANTAR`)
* `slot` (ex.: `PRATO_PRINCIPAL_1`, `LEVE_SABOR`, `GUARINCAO`, …)
* `title` (texto do item)
* `notes` (opcional)

---

## 7) Erros comuns (troubleshooting)

* **401 Unauthorized no PUT** → faltou **Basic Auth** (`admin:1234`).
* **400 Bad Request com acento** → envie **UTF-8** e, no Windows, prefira `-InFile` com arquivo `.json`.
* **Porta 8080 ocupada** → encerre o processo:
  Windows: `Stop-Process -Id <PID> -Force`
  Linux/macOS: `lsof -i :8080` / `kill -9 <PID>`
* **MySQL não conecta** → recrie os volumes:
  `docker compose down -v && docker compose up -d`

---

## 8) Estrutura do repositório (recorte)

```
.
├─ docker-compose.yml
├─ docs/
│  ├─ README.md              ← este arquivo
│  └─ backup-restore.md
├─ dumps/                    ← snapshots SQL (gitignored por padrão)
├─ scripts/
│  ├─ populate-week.ps1
│  ├─ list-week.ps1
│  └─ dump-and-commit.ps1
└─ src/main/java/com/ru/facil/ru_facil/
   ├─ menu/dto/IntervalMenuResponse.java
   ├─ repositories/DailyMenuEntryRepository.java
   ├─ resources/DailyMenuResource.java
   └─ config/SecurityConfig.java
```

---

## 9) Próximos passos sugeridos

* Documentar endpoints com **OpenAPI/Swagger**
* **Flyway** para versionar schema
* Consulta por **intervalo de datas**
* Permissões refinadas e usuários persistentes

