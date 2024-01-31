#build stage
FROM node:19-alpine as builder
#define a build time variable
ARG NEXT_PUBLIC_SERVER_HOSTNAME
#set build time var as env variable
ENV NEXT_PUBLIC_SERVER_HOSTNAME=$NEXT_PUBLIC_SERVER_HOSTNAME

WORKDIR /app
#copy over required data
COPY package.json package-lock.json ./
#install required dependencies
RUN npm install -legacy-peer-deps
#copy over data from installation of deps
COPY . .
#run build process to create optimized build directory
RUN npm run build

#production stage
FROM node:19-alpine
WORKDIR /app
#copy over data from build stage
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json
#expose port within container
EXPOSE 3000
#start application
CMD ["npm", "start"]
