# Sauto Transport

A responsive transport workspace inspired by the vehicle, payments and reporting focus of the [Sauto Transport repository](https://github.com/sivasurya-root/Sauto-Transport). This project has two separate parts:

- `frontend/` is a dependency-free static app that can be published on GitHub Pages.
- `backend/` is a standalone Spring Boot API for account authentication and persistent, owner-scoped transport records.

## Frontend

The app opens in clearly labelled **Demo mode** with sample vehicles, drivers, reminders, payments and invoices. Add a vehicle photo directly in its form; photos are resized in the browser and saved with the vehicle record in Demo mode or the live API. Other actions such as filtering vehicles and marking payments as paid work against browser storage in Demo mode, so the demo needs no server or account. Reports can be downloaded as CSV files.

The supplied Sauto logo is included as `frontend/sauto-original-logo.png` and appears in the desktop navigation and mobile header.

To run it locally, serve the `frontend/` folder from any static HTTP server. For example:

```sh
python -m http.server 8000 --directory frontend
```

Then open `http://localhost:8000`. The frontend uses relative asset paths and has no build step, which makes it suitable for GitHub Pages project sites as well as custom domains.

### Publish on GitHub Pages

The included workflow at `.github/workflows/pages.yml` publishes only the `frontend/` directory when changes reach `main` or `master`.

1. Push this project to GitHub.
2. In the repository, open **Settings → Pages** and select **GitHub Actions** as the build and deployment source.
3. Push a change to `frontend/` or run the **Deploy Sauto frontend to GitHub Pages** workflow manually.

No API URL or secret is embedded in the published files. Users can select **Demo mode** in the top bar and connect to a separately hosted API when they have one.

## Backend

The Spring Boot 4 API targets Java 17 and can run independently of the static frontend. It supports account registration and sign-in, password hashing with BCrypt, expiring opaque bearer tokens stored as hashes, and user-scoped vehicles, drivers, documents, payments and invoices.

For local development, install Java 17 and Maven, then run:

```sh
cd backend
mvn spring-boot:run
```

The default local database is an in-memory H2 database. Set database variables to use a persistent MySQL database:

```text
SAUTO_DATABASE_URL=jdbc:mysql://db-host:3306/sauto
SAUTO_DATABASE_USERNAME=sauto_user
SAUTO_DATABASE_PASSWORD=change-this
SAUTO_CORS_ORIGINS=https://YOUR-ACCOUNT.github.io,http://localhost:8000
```

For a local API and persistent MySQL database in containers, copy `.env.example` to `.env`, replace both password values, then run from the repository root:

```sh
docker compose -f backend/compose.yaml up --build
```

The API is then available at `http://localhost:8080`. The database persists in a named Docker volume.

Set `SAUTO_CORS_ORIGINS` to the exact origin serving the Pages site and any local development origins. Do not add a path such as `/Sauto-Transport` to an origin. Deploy the API and MySQL database to a host that supports Java 17 or containers. To build and run the included container from the repository root:

```sh
docker build -f backend/Dockerfile -t sauto-api .
docker run --rm -p 8080:8080 \
  -e SAUTO_DATABASE_URL='jdbc:mysql://host.docker.internal:3306/sauto' \
  -e SAUTO_DATABASE_USERNAME='sauto_user' \
  -e SAUTO_DATABASE_PASSWORD='change-this' \
  -e SAUTO_CORS_ORIGINS='https://YOUR-ACCOUNT.github.io' \
  sauto-api
```

Use **Demo mode → Connect your workspace** in the frontend, enter the public API URL, then sign in or create a live account. The frontend keeps the returned bearer token in memory for the current page session. Its demo records remain local and are not uploaded.

## API routes

All routes except account creation and sign-in require `Authorization: Bearer <token>`.

| Method | Route | Purpose |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Create an account and sign in |
| `POST` | `/api/auth/login` | Sign in |
| `GET` | `/api/auth/me` | Read the current account |
| `POST` | `/api/auth/logout` | Revoke the current token |
| `GET`, `POST` | `/api/vehicles` | List or add vehicles |
| `PUT`, `DELETE` | `/api/vehicles/{id}` | Edit or delete a vehicle |
| `PATCH` | `/api/vehicles/{id}/status` | Change a vehicle's status |
| `GET`, `POST` | `/api/drivers` | List or add drivers |
| `PUT`, `DELETE` | `/api/drivers/{id}` | Edit or delete a driver |
| `GET`, `POST` | `/api/documents` | List or add expiry records |
| `GET`, `POST` | `/api/payments` | List or record supplier payments |
| `PATCH` | `/api/payments/{id}/paid` | Mark a payment settled |
| `GET`, `POST` | `/api/invoices` | List or create invoices |
| `PATCH` | `/api/invoices/{id}/paid` | Mark an invoice collected |
| `GET` | `/api/reports/summary` | Get an account-scoped operations summary |

The demo and API record money values as whole rupees. Payment records track settlements; they do not initiate bank or card transactions. Expiry reminders appear in the app and report data; email or SMS delivery needs a separately configured notification provider.
