# React app image
FROM node:18-alpine as build
# Setting up the work directory
WORKDIR /react-app

# Installing dependencies
COPY ./package*.json /react-app/

RUN npm install

# Copying all the files in our project
COPY . .

# Pass environment variables during build
ARG REACT_APP_HOST
ARG REACT_APP_WEB_SOCKET_URL
ENV REACT_APP_HOST=$REACT_APP_HOST
ENV REACT_APP_WEB_SOCKET_URL=$REACT_APP_WEB_SOCKET_URL

# Building the React app
RUN npm run build

RUN npm install -g serve || true
RUN npm install -g env-cmd || true

CMD ["npm","run","deploy"]
