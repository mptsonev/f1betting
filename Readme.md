# F1 Betting API

A REST API for placing bets on Formula 1 sessions. Built as a backend home assignment for Sporty Group.

## Prerequisites

- Java 21
- Maven 3.8+

## How to Run

```bash
mvn spring-boot:run
```

The server starts on port `8080` by default. Override with the `SERVER_PORT` environment variable.

On startup, **100 users (IDs 1-100) are created with a balance of 1000 each**. No registration required — just start betting.

## API Endpoints

### List F1 Events

```
GET /api/v1/f1/events
```

Returns F1 sessions with a driver market (driver name, ID, odds) for each event. Sessions are fetched from the OpenF1 API and cached in memory. Drivers and odds are lazily loaded per session on first access.

**Query parameters** (all optional):

| Parameter     | Example   | Description                              |
| ------------- | --------- | ---------------------------------------- |
| `sessionType` | `Race`    | Practice, Qualifying, Race, Sprint, etc. |
| `year`        | `2024`    | 4-digit year                             |
| `countryName` | `Bahrain` | Country name (case-insensitive)          |

### Place a Bet

```
POST /api/v1/f1/bet
```

```json
{
  "userId": 1,
  "sessionKey": 9472,
  "driverId": 1,
  "amount": 50.0
}
```

- Users 1-100 are pre-loaded with a starting balance of 1000.
- Amount must be positive with up to 2 decimal places.
- Odds are taken from the driver market for the given session (call list events first).
- Balance is deducted immediately on bet placement.

### Simulate Event Outcome

```
POST /api/v1/f1/simulate
```

```json
{
  "sessionId": 9472,
  "winnerDriverId": 1
}
```

Settles all bets for the session. Users who bet on the winning driver receive `amount * odds`. All bets for the session are cleared after settlement.

### Check User Balance

```
GET /api/v1/balance/user/{userId}
```

Returns the current balance for the given user.

## Architecture

- **Spring Boot 3.4.9** with Java 21 records for DTOs and domain models
- **Interface-based design** for cache and client layers, allowing future swap of data provider or storage
- **In-memory caching** with `ConcurrentHashMap` for thread-safe lazy loading of driver markets per session
- **Scheduled cache refresh** via `@Scheduled` cron job at midnight UTC, with `@PostConstruct` for startup population
- **Rate limiting** for the OpenF1 API (3 req/s, 30 req/min) using a dual-semaphore approach with per-request delayed permit release
- **Validation** via Jakarta Bean Validation on request DTOs and custom filter validation in the service layer

## Caveats

- **In-memory only** — no persistence layer. All data (balances, bets, cached sessions) lives in memory and is lost on restart.
- **List events must be called before placing bets** to populate the events cache and generate odds for the driver market.
- **Odds are generated once** per session on first access (random value of 2, 3, or 4) and remain fixed for the lifetime of the cache.

## Future Improvements

- Persistent storage (e.g. PostgreSQL) for bets and user balances
- Idempotency keys on bet placement to prevent duplicate bets
- Transactional bet settlement with failure recovery
- Authentication and user management
- Bet history endpoint with won/lost status tracking
- Pagination on the events endpoint

## AI Usage During Implementation

Claude Code (Claude Opus) was used as a pair-programming assistant throughout development. I provided all technical direction and architectural decisions, while the AI accelerated implementation:

- **Developer-led design** — I defined the project structure, chose the interface-based architecture, decided on in-memory caching over persistence, and directed the separation of concerns (e.g. extracting rate limiting into its own class hierarchy)
- **Developer scaffolding** — key classes and interfaces were scaffolded by me, with the AI filling in implementations to match the provided contracts
- **Iterative refinement** — I reviewed AI-generated code at each step, catching issues (e.g. unreliable fixed-schedule rate limiting, missing balance checks, random odds on every request instead of once) and directing the AI toward the correct approach
- **AI-assisted implementation** — the AI handled boilerplate (DTOs, cache wiring, REST controller plumbing), wrote test stubs and integration tests, and generated the initial README structure
- **Code review by developer** — all AI output was reviewed, with me rejecting overengineered solutions (e.g. simplifying the rate limit config from a generic `List<RateLimit>` to two direct methods) and ensuring the codebase stayed lean
