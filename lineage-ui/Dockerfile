FROM node:18.10.0-alpine3.15 as builder
WORKDIR /app
COPY . .
RUN rm -rf node_modules && yarn
RUN npm install
RUN npm run build

FROM nginx:1.23.1-alpine
COPY --chown=nginx:nginx nginx-ui.conf /etc/nginx/conf.d/default.conf
COPY --chown=nginx:nginx --from=builder /app/build /var/www/html/