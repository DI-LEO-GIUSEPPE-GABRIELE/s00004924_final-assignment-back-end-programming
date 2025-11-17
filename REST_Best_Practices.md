# REST API: Best Practices e Procedure

## Principi
- Risorse come sostantivi plurali: `users`, `orders`, `products`.
- URI descrittivi e stabili: evitare verbi (`/users/1/activate` → preferire un campo o una risorsa di stato).
- Metodi HTTP con semantica chiara: `GET` (read), `POST` (create), `PUT` (replace), `PATCH` (partial update), `DELETE` (remove).
- Idempotenza: `GET`, `PUT`, `DELETE` devono essere idempotenti; `POST` normalmente no.
- Stateless: ogni richiesta deve contenere tutto ciò che serve al server per rispondere.

## Naming e URI
- Plurale per collezioni: `/users`.
- Risorsa singola con ID: `/users/{id}`.
- Relazioni: `/users/{id}/orders`.
- Kebab-case per path: `/user-profiles`.
- Query per filtri/ordinamento/paginazione: `?page=0&size=20&sort=name,asc`.

## Status Code
- 200 OK: letture e aggiornamenti riusciti.
- 201 Created: creazione riuscita, includere `Location` con URI della risorsa.
- 204 No Content: cancellazione riuscita.
- 400 Bad Request: input non valido.
- 401 Unauthorized / 403 Forbidden: autenticazione/autorizzazione.
- 404 Not Found: risorsa inesistente.
- 409 Conflict: conflitti di stato (es. concorrenza ottimistica).
- 422 Unprocessable Entity: validazione semantica fallita.
- 500/503: errori server/servizio non disponibile.

## Payload e Formati
- JSON come default; usare `Content-Type: application/json`.
- Date ISO-8601 in UTC: `2025-11-17T15:00:00Z`.
- Campi null vs assenti: definire la convenzione e mantenerla.
- Evitare inviare dati non necessari; stabilire contratti chiari.

## Paginazione, Filtro, Ordinamento
- Paginazione: `page` (0-based), `size` (limite). Restituire metadati (`total`, `page`, `size`).
- Ordinamento: `sort=campo,asc|desc`.
- Filtri: query params espliciti (`status=active`) o DSL semplice (`filter=name:like:mario`).

## Versionamento
- URI (`/v1/users`) o header (`Accept: application/vnd.api+json;version=1`).
- Mantenere compatibilità retroattiva; evitare breaking changes senza una nuova versione.

## Caching e Concorrenza
- `ETag` + `If-None-Match` per cache dei `GET`.
- `If-Match` per `PUT`/`PATCH` con ETag: concorrenza ottimistica.
- `Cache-Control` e `Expires` per risorse cacheabili.

## Error Handling
- Struttura errore standard (es. RFC 7807 Problem Details):
  - `type`, `title`, `status`, `detail`, `instance`, `errors`.
- Non esporre stack trace; includere `trace_id`/`correlation_id` per diagnostica.
- Usare un gestore centralizzato (`@ControllerAdvice`) per mappare eccezioni → risposte.

## Validazione
- Bean Validation (`jakarta.validation`) su DTO: `@NotNull`, `@Email`, `@Size`.
- `@Valid` sui parametri del controller; restituire errori coerenti (422 o 400).

## Sicurezza
- Solo HTTPS.
- Autenticazione: JWT/OAuth2; non mettere segreti nei log.
- Autorizzazione per risorsa/azione.
- Rate limiting, input validation, protezione da injection.

## Documentazione e Contratti
- OpenAPI/Swagger per descrivere endpoints, payload, status, sicurezza.
- Generare client/server da OpenAPI quando sensato.

## Osservabilità
- Logging strutturato con `correlation_id`.
- Metriche (Actuator/Micrometer): tempi risposta, error rate, throughput.
- Tracing distribuito (OpenTelemetry).

## Performance
- Ridurre payload: usare campi necessari, compressione (`gzip`).
- Evitare N+1 (nel livello dati); usare paginazione.
- Timeouts e retry con backoff esponenziale sul client.

## Procedure con Spring Boot
- Struttura a strati:
  - `Controller` per mapping HTTP e `ResponseEntity`.
  - `Service` per logica di business e orchestrazione.
  - `Repository`/accesso dati (se usi DB).
  - DTO separati da entità.
- Validazione: annota DTO e usa `@Valid` nei controller.
- Errori: centralizza con `@ControllerAdvice` e mappa in JSON coerente.
- ResponseEntity: gestisci esplicitamente status, header (`Location` su 201).
- Logging: log breve, senza segreti; aggiungi IDs di correlazione.

### Esempi nel progetto
- Lista utenti: controller in `src/main/java/study_project/demo/RestCrudDemo.java:119`.
- Dettaglio: `src/main/java/study_project/demo/RestCrudDemo.java:124`.
- Creazione con `201 Created` e `Location`: `src/main/java/study_project/demo/RestCrudDemo.java:131-136`.
- Aggiornamento: `src/main/java/study_project/demo/RestCrudDemo.java:139-144`.
- Cancellazione con `204` o `404`: `src/main/java/study_project/demo/RestCrudDemo.java:147-150`.

### Curl di riferimento (porta 8081)
- `curl -s http://localhost:8081/users`
- `curl -s -X POST http://localhost:8081/users -H 'Content-Type: application/json' -d '{"name":"Mario","email":"mario@example.com"}'`
- `curl -s http://localhost:8081/users/1`
- `curl -s -X PUT http://localhost:8081/users/1 -H 'Content-Type: application/json' -d '{"name":"Mario Rossi","email":"m.rossi@example.com"}'`
- `curl -i -X DELETE http://localhost:8081/users/1`

## Checklist rapida
- Definisci risorse e URI chiari, stabili.
- Scegli status code coerenti e usa `ResponseEntity`.
- Valida input con Bean Validation; gestisci errori centralmente.
- Documenta con OpenAPI; mantieni contratti stabili.
- Aggiungi paginazione/ordinamento e limiti ragionevoli.
- Implementa sicurezza (HTTPS, authn/authz) e rate limiting.
- Osservabilità: log, metriche, tracing.
- Prevedi caching e concorrenza ottimistica quando utile.

## Setup Spring Web
- Aggiungi la dipendenza nel `pom.xml` dentro `<dependencies>`:
  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  ```
- Il progetto corrente è già configurato con `spring-boot-starter-web` (`pom.xml`).

## Avviare il server
- Avvio standard (porta 8080):
  - `./mvnw spring-boot:run`
- Specificare una porta diversa (es. 8081):
  - `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`
- Build del jar ed esecuzione:
  - `./mvnw -DskipTests package`
  - `java -jar target/demo-0.0.1-SNAPSHOT.jar`
- Stop del server avviato dal terminale:
  - `Ctrl + C`

## DevTools (restart automatico)
- Aggiungi la dipendenza nel `pom.xml` (già presente in questo progetto):
  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
  </dependency>
  ```
- Effetto: al salvataggio/ricompilazione il server si riavvia automaticamente.
- Con `spring-boot:run` il riavvio avviene quando Maven ricompila le classi (salva il file e il plugin ricompila).
- In esecuzione da jar, DevTools è escluso automaticamente dal packaging di produzione.
- Proprietà utili:
  - `spring.devtools.restart.enabled=true`
  - `spring.devtools.restart.exclude=static/**,public/**` (escludi risorse non rilevanti)
- Suggerimento: usa una porta non occupata, es. `--server.port=8081`.

## Testare con curl
- Lista utenti:
  - `curl -s http://localhost:8081/users`
- Creazione utente:
  - `curl -s -X POST http://localhost:8081/users -H 'Content-Type: application/json' -d '{"name":"Mario","email":"mario@example.com"}'`
- Dettaglio utente:
  - `curl -s http://localhost:8081/users/1`
- Aggiornamento utente:
  - `curl -s -X PUT http://localhost:8081/users/1 -H 'Content-Type: application/json' -d '{"name":"Mario Rossi","email":"m.rossi@example.com"}'`
- Cancellazione utente:
  - `curl -i -X DELETE http://localhost:8081/users/1`