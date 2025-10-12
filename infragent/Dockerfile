FROM node:22 AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:22-alpine AS production
WORKDIR /app
COPY package*.json ./
RUN npm install --production
COPY --from=builder /app/dist ./dist
RUN mkdir -p /app/configs
ENV NODE_ENV=production
EXPOSE 8081
CMD ["node", "dist/index.js"]

LABEL org.opencontainers.image.title="Infragent"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.source="https://github.com/VibhuviOiO/inframirror"