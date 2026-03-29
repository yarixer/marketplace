# Marketplace

AWS-based marketplace platform for selling downloadable 3D model packs.

The project is designed to run on **Amazon Web Services**, using **Amazon EC2** for container hosting, **Amazon RDS MySQL** for the database, **Amazon S3** for file storage, **Amazon ECR** for container images, and **AWS Secrets Manager** for application secrets.

Built as a fullstack web application with a Java/Spring Boot backend and a SvelteKit frontend. Public pages use SSR, while account, seller, and admin areas use a SPA-like experience.

> [!NOTE]
> Live demo: https://terrawow.vip/
---

## AWS Infrastructure

Production environment is designed around:

- **Amazon ECR** — container image registry
- **Amazon EC2** — runtime host for frontend, backend, and reverse proxy containers
- **Amazon RDS MySQL** — relational database
- **Amazon S3** — product archives and preview images
- **AWS Secrets Manager** — application secrets and credentials

---

## Tech Stack

### Backend
- Java 21
- Spring Boot 4
- Maven
- MySQL
- Flyway
- JWT access token + refresh token
- S3-compatible object storage
- Docker

### Frontend
- SvelteKit
- Tailwind CSS
- SSR for public pages
- SPA-like private panels

---

## Features

### Public area
- landing page
- product catalog
- search by title prefix
- tag filters
- product details page

### Authentication & account
- register / login
- JWT authentication
- refresh token flow
- profile info
- display name update
- password change
- wallet balance

### Buyer area
- purchase history
- owned products library
- access to the latest approved downloadable revision

### Seller area
- become seller
- create and edit products
- upload ZIP archives
- upload preview images
- submit revisions for moderation
- seller dashboard
- sales statistics
- wallet balance

### Admin area
- user management
- product moderation
- approve / reject product revisions
- product state management
- revision file access
- manual wallet credit

---

## Backend Modules

The backend includes the following core domains:

- users and roles
- seller profiles
- products and product revisions
- revision images
- tags
- votes
- orders and entitlements
- wallet accounts and ledger entries
- refresh tokens

---

## Storage

Product archives and preview images are stored in object storage.

- local development: S3-compatible storage
- production: Amazon S3

---

## Local Development

Typical local environment includes:

- backend container
- frontend container
- MySQL
- S3-compatible object storage
- Flyway migrations on startup

---

## Project Status

The MVP backend is implemented and integrated with:

- authentication
- moderation flow
- seller product management
- buyer orders and downloads
- wallet and balance ledger
- object storage
- admin tools

The project is prepared for AWS-based deployment and production-style containerized runtime.
