### teams
curl http://localhost:8080/teams
curl http://localhost:8080/teams/1
curl -X POST http://localhost:8080/teams -H "Content-Type: application/json" -d '{"name":"New Team"}'
curl -X PUT http://localhost:8080/teams/1 -H "Content-Type: application/json" -d '{"name":"Updated Team"}'
curl -X DELETE http://localhost:8080/teams/1
# Dev Instruction, never apply this to prod. 

### Use Case 1: Update schema (new fields, new tables, changes)
You’ve already got an existing DB with tables, but you want to apply changes.

```bash
# 1. Edit prisma/schema.prisma
#    (add new models, fields, relations, enums, etc.)

# 2. Generate a new migration
npx prisma migrate dev --name add-service-owner   # descriptive name

# 3. Prisma applies migration and updates client
```

>> If migration fails because of existing data, adjust manually:
>> Use prisma migrate dev --create-only to only create the SQL
>> Edit SQL migration script
>> Apply again with prisma migrate dev

### Use Case 2: Reset everything from scratch

You don’t care about old data → want a clean DB + seed fresh.

```bash
# 1. Drop everything and re-apply all migrations
npx prisma migrate reset

# 2. This:
#    - Drops schema
#    - Applies *all* migrations in prisma/migrations/
#    - Runs seed.js
```

### prod usecase

```bash
npx prisma migrate deploy
```