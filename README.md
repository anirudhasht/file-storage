# S3 User Document Storage Service

A cloud-based document storage REST API built with **Java 17, Spring Boot, and AWS S3**. The application allows users to upload, search, list, and download documents stored in Amazon S3, with files organized using user-specific folder prefixes.

**Live Application:** https://file-storage-u4dz.onrender.com  
**Swagger API Documentation:** https://file-storage-u4dz.onrender.com/swagger-ui/index.html  
**GitHub Repository:** https://github.com/anirudhasht/file-storage

---

## Features

- **User-based storage:** Organizes documents under individual user prefixes in an S3 bucket.
- **File upload:** Upload documents using multipart form data.
- **File listing:** Retrieve files belonging to a particular user.
- **Recursive search:** Search filenames across nested folders.
- **Pagination:** Supports cursor-based pagination and configurable result limits.
- **File download:** Download documents directly from S3.
- **Input validation:** Validates request parameters and uploaded files.
- **Centralized exception handling:** Returns structured JSON error responses.
- **API documentation:** Interactive Swagger UI documentation.
- **Cloud deployment:** Containerized deployment using Docker and Render.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot | REST API development |
| AWS S3 | Cloud object storage |
| AWS SDK | S3 integration |
| Maven | Build and dependency management |
| Docker | Application containerization |
| Render | Cloud deployment |
| Springdoc OpenAPI | API documentation |

## Architecture

```text
              Client
                |
                v
          REST Controllers
                |
                v
          Service Layer
                |
                v
          Storage Service
                |
                v
          AWS S3 Bucket
          /     |      \
       User1  User2   User3
        /       |       \
     Files    Files    Files
```

Documents are stored in a single S3 bucket using user-specific prefixes.

Example storage structure:

```text
flie-storage-anirudh-2026/
|
├── sandy/
|   ├── document1.pdf
|   ├── report.txt
|   └── reports/
|       └── 2026/
|           └── decision-flow.pdf
|
└── another-user/
    └── document2.pdf
```

## REST API Endpoints

Base URL:

```text
https://file-storage-u4dz.onrender.com
```

### 1. List and Search Files

**GET**
```http
/api/users/{userName}/files
```

Query parameters:

| Parameter | Description | Default |
|---|---|---|
| search | Filename search term | Optional |
| limit | Maximum number of results | 50 |
| cursor | Pagination cursor | Optional |

Example:

```http
GET /api/users/sandy/files?search=report&limit=50
```

### 2. Upload a File

**POST**
```http
/api/users/{userName}/files
```

Content-Type: `multipart/form-data`

| Parameter | Type | Required |
|---|---|---|
| file | Multipart file | Yes |
| folder | String | No |

Example:

```http
POST /api/users/sandy/files
```

Upload a file with an optional folder destination.

### 3. Download a File

**GET**
```http
/api/users/{userName}/files/download
```

Query parameter:

| Parameter | Description |
|---|---|
| path | File path within the user's storage prefix |

Example:

```http
GET /api/users/sandy/files/download?path=reports/2026/decision-flow.pdf
```

## Local Setup

### Prerequisites

- Java 17
- Maven
- AWS account
- An S3 bucket
- AWS IAM credentials with the required S3 permissions

### 1. Clone the Repository

```bash
git clone https://github.com/anirudhasht/file-storage.git
cd file-storage
```

### 2. Configure AWS

Configure the following properties in:

`src/main/resources/application.properties`

```properties
aws.region=eu-north-1
aws.s3.bucket=flie-storage-anirudh-2026
```

Set your AWS credentials as environment variables:

```text
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_REGION=eu-north-1
```

Never commit AWS credentials or secret keys to GitHub.

### 3. Run the Application

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The application runs on the configured server port.

### 4. Access Swagger UI

```text
http://localhost:10000/swagger-ui/index.html
```

## Docker Deployment

Build the Docker image:

```bash
docker build -t file-storage .
```

Run the container:

```bash
docker run -p 10000:10000 file-storage
```

Configure AWS credentials as environment variables when running the container.

## Testing

The project includes automated tests covering application functionality.

Test execution:

```bash
./mvnw test
```

**Latest test results:** 22 tests passed, with 0 failures and 0 errors.

The deployed APIs were also manually tested through Swagger UI, including file upload, recursive search, and download operations against AWS S3.

## Deployment

The application is deployed on Render using Docker.

- Application: https://file-storage-u4dz.onrender.com
- Swagger UI: https://file-storage-u4dz.onrender.com/swagger-ui/index.html

AWS S3 is used as the persistent document storage backend.

## Technical Decisions

1. **Amazon S3 for object storage:** Provides scalable cloud-based storage without requiring files to be stored on the application server.
2. **User-specific prefixes:** Organizes documents by user while maintaining a simple bucket structure.
3. **Storage service abstraction:** Separates storage operations from REST API handling, making the storage implementation easier to maintain and extend.
4. **Cursor-based pagination:** Supports efficient navigation through file listings.
5. **Docker-based deployment:** Provides a consistent runtime environment across local development and cloud deployment.

## Security Considerations

- AWS credentials are supplied through environment variables.
- The S3 bucket should remain private.
- IAM permissions should follow the principle of least privilege.
- File upload size is limited to 10 MB.
- Authentication and authorization are not currently implemented; user names in API paths are not an access-control mechanism.

---

**Built with Java, Spring Boot, and AWS S3.**
