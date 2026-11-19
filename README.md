# Корпоративный справочник сотрудников

Backend-приложение на **Java 17 + Spring Boot** с простым веб-интерфейсом.
Помогает быстро найти нужного сотрудника предприятия: по ФИО, должности, отделу, телефону или email.

## Для кого и зачем
Сотрудникам крупной организации сложно понять, кому звонить по тому или иному вопросу.
Справочник собирает контакты в одном месте, а доступ к данным разграничен по ролям.

## Роли
| Роль  | Возможности |
|-------|-------------|
| ADMIN | всё, что у HR + управление отделами и пользователями |
| HR    | добавление, изменение и удаление контактов сотрудников |
| USER  | просмотр, поиск и фильтрация контактов, личное избранное |

## Функционал
- Вход по логину и паролю (Spring Security, Basic Auth, пароли хешируются BCrypt)
- Поиск по ФИО, должности, телефону, email + фильтр по отделу
- CRUD контактов (HR, ADMIN), CRUD отделов и пользователей (ADMIN)
- Избранные контакты у каждого пользователя
- Веб-страница `index.html`, работающая поверх REST API

## Технологии
Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA / Hibernate, Spring Security, H2 (или PostgreSQL), Maven, Lombok.

## Структура базы данных
- `users` (id, username, password, role)
- `department` (id, name)
- `contact` (id, full_name, position, phone, email, **department_id** -> department)
- `favorite` (id, **user_id** -> users, **contact_id** -> contact)

Связи: отдел 1—N контакты; пользователь N—M контакты через `favorite`.

## Запуск
Нужны JDK 17+ и Maven.
```bash
git clone <ссылка на ваш репозиторий>
cd staff-directory
mvn spring-boot:run
```
Открыть: http://localhost:8080

Тестовые пользователи (создаются при старте):
`admin / admin123`, `hr / hr123`, `user / user123`.

## Настройка базы данных
По умолчанию используется **H2 в памяти** — ничего устанавливать не нужно.
Данные сбрасываются при перезапуске. Консоль БД: http://localhost:8080/h2-console
(JDBC URL: `jdbc:h2:mem:directory`, пользователь `sa`, пароль пустой).

Для **PostgreSQL**:
1. Создайте БД: `CREATE DATABASE directory;`
2. В `pom.xml` замените зависимость H2 на `org.postgresql:postgresql` (scope runtime).
3. В `application.properties` раскомментируйте блок PostgreSQL и закомментируйте блок H2, укажите свой логин и пароль.

## REST API
Авторизация: заголовок Basic Auth (в Postman — вкладка Authorization).

| Метод | URL | Роль |
|-------|-----|------|
| GET | `/api/me` | любая |
| GET | `/api/contacts?search=&departmentId=` | любая |
| GET | `/api/contacts/{id}` | любая |
| POST / PUT / DELETE | `/api/contacts`, `/api/contacts/{id}` | HR, ADMIN |
| GET | `/api/departments` | любая |
| POST / PUT / DELETE | `/api/departments`, `/api/departments/{id}` | ADMIN |
| GET / POST / PUT / DELETE | `/api/users`, `/api/users/{id}` | ADMIN |
| GET | `/api/favorites` | любая |
| POST / DELETE | `/api/favorites/{contactId}` | любая |

Пример создания контакта (`POST /api/contacts`, пользователь hr):
```json
{
  "fullName": "Орлов Павел Андреевич",
  "position": "Аналитик",
  "phone": "+7 900 555-55-55",
  "email": "orlov@company.ru",
  "department": { "id": 1 }
}
```

## Презентация
Файл презентации находится в папке `docs/`.
