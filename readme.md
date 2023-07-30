# M3*071 bot

## Get started

---

#### Tg and Notion

Чтобы сконфигурировать поведение бота для конкретного tg-чата и notion'a,
задайте токены в `.env` файле. Пример входных данных можно найти в `.env.example`

#### Pipisa bot functions

Для конфигурации кулдауна /increase_dick команды использует класс `CleanPipisaCacheJob`

В качестве бд можно использовать **PostgreSQL** или in-memory db **H2**.
Для настройки DataSource'a используйте файл `db.properties`

