#SP-1 Movie backend
This backend fetches movies from the TMDb API, maps JSON to DTO's using jackson, then converts DTO's into entities. The entities are connected to PostgreSQL with JPA using DAO and a service layer.
IMPORANT:
requires TMDb API key and in this project the key is stored in an environment variable.

## Features
- Fetches movies
- Fetches credits
- Stores in database using JPA/hibernate (Movies, Actors, Directors and Genres)
- It avoids duplicates using TMBd Ids
- The methods are run in Main by using the ServiceManager class
## Project Structure

- `DTO/`  
  DTOs used for mapping TMDb JSON responses with Jackson

- `entities/`  
  JPA entities: `Movie`, `Actor`, `Director`, `Genre`

- `DAOs/`  
  Persistence layer (CRUD + queries), DAOs accept an `EntityManager`

- `ServiceManager`  
  Business logic / orchestration. Handles:
  - Transactions for write operations
  - Exception handling
  - Fetch + store workflows

- `Main`  
  Entry point for running a one-time fetch + store operation.
