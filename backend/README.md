# Backend 

## Prisma Migrations
- `schema.prisma` = your desired state (infra code).
- `migrations/` = your migration history
- `migrate deploy` = applies only unapplied migrations

## Populate Seed Data:

```bash
npx prisma db seed
```

### Development Workflow
- When you change schema: `npx prisma migrate dev --name add_services_table`
    - Creates a folder prisma/migrations/20250821_add_services_table/migration.sql
    - Applies it to your local DB
    - Updates the Prisma client

>> Note: Never edit old migration folders once committed. Treat them like git commits.

### Production Workflow
- On prod you never use `migrate dev`. Only run `npx prisma migrate deploy`.
    - Reads the migration history
    - Applies unapplied scripts in order
    - Doesn’t try to diff schema vs DB (safer!)

### Generating SQL Script for Review
- Generate a SQL script of all unapplied migrations

```bash
npx prisma migrate diff \
  --from-url "$PROD_DB_URL" \
  --to-schema-datamodel prisma/schema.prisma \
  --script > migration_patch.sql
```


