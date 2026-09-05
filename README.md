# Student Suggestion Box

Minimal Jakarta EE web application: students post suggestions, staff triage them.

- **View**: JSF 4.0 (Mojarra) + Facelets, CDI (Weld) on **Tomcat 10.1**
- **Persistence**: **plain Hibernate ORM 6** — native `Session` API, `hbm.xml` mappings, **no JPA**
- **Database**: PostgreSQL 16 (container)
- **Packaging**: WAR (`suggestionbox.war`), deployed as `ROOT.war`
- **Full CRUD entities**: `Category` and `Suggestion`

## Run

```bash
docker compose up -d --build
```

Then open <http://localhost:8080/>. **This works with no internet connection** — see
[Offline use](#offline-use).

| Command | Purpose |
|---|---|
| `mvn clean package` | build `target/suggestionbox.war` only |
| `mvn -o -Dmaven.repo.local=.m2repo clean package` | same, strictly offline |
| `docker compose up -d --build` | build images and start `app` + `db` |
| `docker compose logs -f app` | follow the Tomcat log |
| `docker compose down` | stop the stack |
| `docker compose down -v` | stop and erase the database volume |

Database: `suggestionbox` / user `suggestion` / password `suggestion`, exposed on `localhost:5432`.
The app receives `DB_URL`, `DB_USER`, `DB_PASSWORD` as environment variables (see
`docker-compose.yml`); `HibernateUtil` overrides `hibernate.cfg.xml` with them when present.

Three categories are seeded by `db/init.sql` the first time the volume is created.

## Offline use

The stack is built to run with no network at all.

| What could need the network | How it is removed |
|---|---|
| Maven downloading dependencies | All jars and plugins are vendored in `.m2repo/` (48 MB) and the Docker build runs `mvn -o` (strict offline). Every lifecycle plugin version is pinned in `pom.xml`, so the container's Maven resolves exactly the vendored artifacts. |
| Docker pulling base images | `pull_policy: missing` on both services and `build.pull: false`, so Docker uses the local image store and never contacts a registry. |
| Hibernate fetching the DTDs named in `hibernate.cfg.xml` / `*.hbm.xml` | Hibernate resolves them from the classpath (`hibernate-configuration-3.0.dtd` and `hibernate-mapping-3.0.dtd` ship inside `hibernate-core`). No HTTP request is made. |
| Tomcat / Weld / Mojarra fetching XSDs | Schema validation is off by default; nothing is fetched. |

Verified with `docker build --network=none --pull=false .` — the image builds with the
network completely disabled.

The only prerequisite is that these three base images already exist in your local Docker
image store:

```
maven:3.9-eclipse-temurin-21
tomcat:10.1-jdk21-temurin
postgres:16-alpine
```

They are there now. If you ever prune them, or want to move the project to a machine that
has never pulled them, export them once **while online**:

```bash
sh offline/save-images.sh        # writes offline/images.tar (~1 GB)
```

and on the offline machine:

```bash
sh offline/load-images.sh
docker compose up -d --build
```

PowerShell equivalents, if you prefer:

```powershell
docker save -o offline\images.tar maven:3.9-eclipse-temurin-21 tomcat:10.1-jdk21-temurin postgres:16-alpine
docker load -i offline\images.tar
```

Building the WAR on the host offline works the same way:

```bash
mvn -o -Dmaven.repo.local=.m2repo clean package
```

> `.m2repo/` is committed on purpose — it is what makes the offline build work. Do not
> add it to `.gitignore`.

## Layout

```
pom.xml                       WAR build
Dockerfile                    maven build stage -> tomcat:10.1 runtime stage
docker-compose.yml            app + db
db/init.sql                   schema + seed categories
.m2repo/                      vendored Maven dependencies (offline build)
offline/                      save-images.sh / load-images.sh
src/main/java/rw/ac/suggestionbox/
  model/       Category, Suggestion                POJOs, no JPA annotations
  persistence/ HibernateUtil                       SessionFactory from hibernate.cfg.xml
  dao/         CategoryDao, SuggestionDao          CRUD via native Hibernate Session
  web/         CategoryBean, SuggestionBean        @Named @ViewScoped backing beans
               StudentEmailValidator               custom validator
               UniqueCategoryNameValidator         custom validator (CDI managed)
               CategoryConverter                   drop-down <-> entity
src/main/resources/
  hibernate.cfg.xml
  rw/ac/suggestionbox/model/Category.hbm.xml, Suggestion.hbm.xml
src/main/webapp/
  template.xhtml index.xhtml categories.xhtml suggestions.xhtml
  resources/css/app.css       external CSS
  WEB-INF/web.xml beans.xml
docs/                         Word documentation + diagrams (regenerate with the two py scripts)
```

## Validation (three types)

1. **Standard JSF validators** — `required="true"`, `<f:validateLength/>` on the pages.
2. **Custom validators** — `StudentEmailValidator` (address must end in `@student.ac.rw`),
   `UniqueCategoryNameValidator` (injects the DAO to reject duplicate names).
3. **Bean Validation** — `@NotBlank`, `@Size`, `@Email` on the entities, applied automatically
   by JSF and again by Hibernate before flush.

HTML5 client-side constraints are added on top with JSF passthrough attributes (`pt:`).

## CSS (three types)

- **External** — `resources/css/app.css` via `<h:outputStylesheet/>`
- **Internal** — `<style>` block per page, injected through `<ui:insert name="pageStyle"/>`
- **Inline** — `style="..."` on individual elements

## Documentation

`docs/Suggestion_Box_Documentation.docx` — abstract, problem statement, scope, AS-IS, TO-BE,
business requirements, software qualities, class diagram, and the Part 2 implementation write-up.

Regenerate:

```bash
cd docs
python make_diagrams.py && python make_doc.py
```
