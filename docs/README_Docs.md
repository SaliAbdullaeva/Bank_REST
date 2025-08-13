# API Documentation (OpenAPI)

Файлы:
- `openapi.yaml` — OpenAPI 3.0 спецификация для проекта.

Как использовать:
1. Положи `openapi.yaml` в `docs/`.
2. Открой Swagger UI (если подключён в проекте) и укажи этот файл, или:
    - Запусти `swagger-ui` локально,
    - Или импортируй `openapi.yaml` в Postman / Insomnia.

Что включено:
- Эндпоинты: /api/auth, /api/cards, /api/transfers, /api/users
- Схемы DTO: CardRequest/CardResponse, TransferRequest/TransferResponse, AuthRequest/AuthResponse, UserRegisterRequest/UserResponse
- Security: bearerAuth (JWT) для защищённых эндпоинтов

Дальше:
- После запуска приложения и миграций добавь примеры реальных ответов (200, 400, 401, 404).
- Обнови схемы под реальные поля (если поменяешь имена в коде).
- Опиши поля ошибок (ErrorResponse) и конкретные коды статусов для ошибок бизнес-логики.

Примечание:
Это минимальная рабочая спецификация — достаточно для ручного тестирования через Swagger/Postman и для генерации клиент-сниппетов.
