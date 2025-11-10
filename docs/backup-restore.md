## 🧰 Backup & Restore

> Documentação completa: **[`docs/backup-restore.md`](docs/backup-restore.md)**

**One-liners úteis**

```powershell
# Backup (Windows/PowerShell)
$ts = Get-Date -Format yyyyMMdd-HHmm; mkdir dumps -ea 0 | Out-Null
docker exec -i ru_mysql sh -c "mysqldump -uru_user -pru_pass --single-transaction --routines --triggers ru_facil" > "dumps/ru_facil-$ts.sql"
bash
Copiar código
# Restore (Linux/macOS)
latest=$(ls -1t dumps/ru_facil-*.sql | head -n 1)
docker exec -i ru_mysql sh -c "mysql -uru_user -pru_pass ru_facil" < "$latest"
yaml
Copiar código

---

### 2) docs/backup-restore.md — guia completo, organizadomd
# 🔐 Backup & Restore — MySQL

> **Pré-requisitos**
>
> - Docker com o container **`ru_mysql`** rodando  
> - Banco **`ru_facil`** com usuário **`ru_user`** / senha **`ru_pass`** (ajuste se for diferente)

---

## ⚡️ Quick Start

**Backup (Windows/PowerShell)**
```powershell
$ts   = Get-Date -Format yyyyMMdd-HHmm
$dump = "dumps\ru_facil-$ts.sql"
New-Item -ItemType Directory -Force -Path dumps | Out-Null
docker exec -i ru_mysql sh -c "mysqldump -uru_user -pru_pass --single-transaction --routines --triggers ru_facil" > $dump
Get-Item $dump | Select-Object FullName, Length, LastWriteTime
Backup (Linux/macOS)

---

bash
Copiar código
ts=$(date +%Y%m%d-%H%M)
mkdir -p dumps
docker exec -i ru_mysql sh -c "mysqldump -uru_user -pru_pass --single-transaction --routines --triggers ru_facil" > "dumps/ru_facil-$ts.sql"
ls -lh "dumps/ru_facil-$ts.sql"
Restore (Windows/PowerShell)

---

powershell
Copiar código
$latest = Get-ChildItem dumps -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1
docker exec -i ru_mysql sh -c "mysql -uru_user -pru_pass ru_facil" < $latest.FullName
Restore (Linux/macOS)

bash
Copiar código
latest=$(ls -1t dumps/ru_facil-*.sql | head -n 1)
docker exec -i ru_mysql sh -c "mysql -uru_user -pru_pass ru_facil" < "$latest"
🔎 Conferência rápida
powershell
Copiar código
docker exec -it ru_mysql sh -c "mysql -uru_user -pru_pass -e 'USE ru_facil; SHOW TABLES;'"
docker exec -it ru_mysql sh -c "mysql -uru_user -pru_pass -e 'USE ru_facil; SELECT COUNT(*) total FROM daily_menu_entries;'"
🧹 Rotação de dumps
Manter só os 5 mais recentes

powershell
Copiar código
Get-ChildItem dumps -File | Sort-Object LastWriteTime -Descending | Select-Object -Skip 5 | Remove-Item
bash
Copiar código
ls -1t dumps/ru_facil-*.sql | tail -n +6 | xargs -r rm -f
📝 Notas & Dicas
Acentos no Windows (UTF-8):

powershell
Copiar código
chcp 65001 > $null
$OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::new($false)
Versionar dumps no Git:

Ignorar todos:

bash
Copiar código
/dumps/
Manter apenas dumps “semanais” (ex.: gerados domingo 00:00):

bash
Copiar código
/dumps/
!/dumps/ru_facil-*-0000.sql
Erros comuns:

Access denied ... PROCESS privilege no mysqldump → use o usuário de backup correto (ou o root) ou remova flags que exijam privilégios.

unknown database no restore → garanta que ru_facil exista ou crie antes:

bash
Copiar código
docker exec -it ru_mysql sh -c "mysql -uroot -prootpass -e 'CREATE DATABASE IF NOT EXISTS ru_facil;'"
