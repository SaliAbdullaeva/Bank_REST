# Система управления банковскими картами


Backend-приложение на Java (Spring Boot) для управления банковскими картами.

## Основные возможности

* Аутентификация и авторизация через Spring Security с JWT
* Управление пользователями и ролями (ADMIN, USER)
* CRUD операции по банковским картам
* Переводы между своими картами
* Просмотр и фильтрация карт с пагинацией
* Блокировка/активация карт
* Маскирование номеров карт
* Валидация запросов и обработка ошибок
* Безопасное хранение паролей (BCrypt)
* Работа с PostgreSQL/MySQL через Spring Data JPA
* Миграции базы через Liquibase
* Документация API через Swagger/OpenAPI
* Развёртывание через Docker Compose
* Юнит-тесты для ключевой бизнес-логики

## Структура проекта

* `controller` — REST-контроллеры
* `service` — бизнес-логика
* `repository` — интерфейсы для доступа к БД
* `entity` — сущности JPA
* `dto` — объекты для передачи данных
* `exception` — обработка ошибок и исключений
* `config` — конфигурационные классы (безопасность, Swagger)
* `resources/db/migration` — миграции Liquibase
* `docs` — документация API

## Технологии

Java 17+, Spring Boot, Spring Security, JWT, Spring Data JPA, PostgreSQL/MySQL, Liquibase, Docker, Swagger/OpenAPI.
