# Infra MIЯROR

## Developer Setup

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Backend
```bash
cd backend
npm install
# (Optional) Initialize database and seed data
# npx prisma migrate dev
# npx prisma db seed
npm run start
```

## Production (Docker)

### Build and Run as Container
Docker image is published to GitHub Container Registry on every merge to the `release` branch.

#### Pull and Run
```bash
docker pull ghcr.io/<your-org-or-user>/<repo>:latest
```

#### Using Docker Compose
```bash
cd docker
docker-compose up -d
```

## GitHub Actions
Docker image is automatically built and pushed to GitHub Container Registry (`ghcr.io`) when changes are merged to the `release` branch. The image is public and can be pulled by anyone.

## Notes
- The frontend is built and served from the backend container's `/public` directory.
- Backend serves API at `/api` and static files for SPA frontend.
- Database initialization and seeding are optional for development.

## APIS REF
- [Public APIs List](https://github.com/public-apis/public-apis?tab=readme-ov-file#calendar)

