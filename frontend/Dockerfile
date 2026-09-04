FROM node:20-alpine AS build
WORKDIR /app
RUN corepack enable && corepack prepare pnpm@latest --activate
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

FROM node:20-alpine
WORKDIR /app
RUN corepack enable && corepack prepare pnpm@latest --activate
COPY --from=build /app/dist ./dist
COPY --from=build /app/package.json .
RUN pnpm install --prod --frozen-lockfile
EXPOSE 4321
CMD ["node", "dist/server/entry.mjs"]
