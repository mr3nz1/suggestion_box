# Student Suggestion Box

Minimal Spring Boot web application: students post suggestions, staff triage them.

- **Framework**: Spring Boot 3.5 (Java 21), executable jar with embedded Tomcat
- **Web**: Spring MVC controllers + Thymeleaf templates, Post/Redirect/Get with flash messages
- **Persistence**: Spring Data JPA (Hibernate) repositories, transactional service layer
- **Database**: PostgreSQL 16 (container); H2 in-memory for tests
- **Full CRUD entities**: `Category` and `Suggestion`

## Run

```bash
docker compose up -d --build
```

Then open <http://localhost:8080/>.

| Command | Purpose |
|---|---|
| `mvn spring-boot:run` | run locally against PostgreSQL on `localhost:5432` |
| `mvn clean verify` | build `target/suggestionbox.jar` and run the tests (no database needed) |
| `java -jar target/suggestionbox.jar` | run the built jar |
| `docker compose up -d --build` | build the image and start `app` + `db` |
| `docker compose up -d db` | start only PostgreSQL, for `mvn spring-boot:run` |
| `docker compose logs -f app` | follow the application log |
| `docker compose down -v` | stop and erase the database volume |

Database: `suggestionbox` / user `suggestion` / password `suggestion`, exposed on `localhost:5432`.
`application.yml` reads `DB_URL`, `DB_USER`, `DB_PASSWORD` and falls back to those local
defaults. Three categories are seeded by `db/init.sql` the first time the volume is created.

Health check: <http://localhost:8080/actuator/health>.

## Layout

```
src/main/java/rw/ac/suggestionbox/
  SuggestionBoxApplication          @SpringBootApplication entry point
  domain/      Category, Suggestion, SuggestionStatus    JPA entities + enum
  repository/  CategoryRepository, SuggestionRepository  Spring Data JPA interfaces
  service/     CategoryService, SuggestionService        @Transactional business logic
               ResourceNotFoundException (-> 404), CategoryInUseException
  dto/         CategoryForm, SuggestionForm              form backing objects + validation rules
  validation/  @StudentEmail, @UniqueCategoryName        custom Bean Validation constraints
  web/         HomeController, CategoryController, SuggestionController
src/main/resources/
  application.yml
  templates/   fragments/layout.html, index.html, categories/index.html, suggestions/index.html
  static/css/app.css
src/test/java/...                   validator unit test, @WebMvcTest slice, full-stack H2 test
```

### Endpoints

| Method | Path | Action |
|---|---|---|
| GET | `/categories` | list + new form |
| GET | `/categories/{id}/edit` | list + edit form |
| POST | `/categories` | create |
| POST | `/categories/{id}` | update |
| POST | `/categories/{id}/delete` | delete (refused while suggestions use it) |
| GET | `/suggestions?status=NEW` | list, optionally filtered by status |
| GET/POST | `/suggestions/...` | same pattern as categories |

## Validation (three types)

1. **Built-in Bean Validation** — `@NotBlank`, `@Size`, `@Email`, `@NotNull` on the form DTOs,
   triggered by `@Valid` in the controllers; errors render next to each field via `th:errors`.
2. **Custom constraints** — `@StudentEmail` (address must end in `@student.ac.rw`) and the
   class-level `@UniqueCategoryName`, whose validator is a Spring-managed bean that injects
   `CategoryService`. On update, the `{id}` path variable binds to `form.id`, so a category
   can keep its own name.
3. **Business rules in the service** — `CategoryService.delete` throws
   `CategoryInUseException` when suggestions still reference the category; the controller
   turns it into a flash error.

HTML5 client-side constraints (`required`, `minlength`, `maxlength`, `type="email"`) sit on top.

## CSS (three types)

- **External** — `static/css/app.css`, linked from `fragments/layout.html`
- **Internal** — a `<style>` block per page, passed into the layout's `head(pageStyle)` fragment
- **Inline** — `style="..."` on individual elements

## Documentation

`docs/Suggestion_Box_Documentation.docx` — abstract, problem statement, scope, AS-IS, TO-BE,
business requirements, software qualities, class diagram, and the implementation write-up.
It still describes the earlier JSF / native-Hibernate version.
