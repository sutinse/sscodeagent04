# XML to JSON Converter Service

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Quick Start and Working Effectively

Bootstrap, build, and test the repository:
- Check Java version: `java -version` (requires Java 17)
- `./mvnw clean compile` -- initial compilation takes ~50 seconds for dependency download, subsequent builds ~5 seconds. NEVER CANCEL.
- `./mvnw clean package` -- full build takes ~25 seconds after dependencies downloaded. NEVER CANCEL.
- `./mvnw test` -- test suite takes ~10 seconds. NEVER CANCEL. Set timeout to 30+ seconds.

Run the application:
- Development mode: `./mvnw quarkus:dev` -- starts in ~30 seconds, enables live reload
- Production JAR: `java -jar target/quarkus-app/quarkus-run.jar` -- starts in ~2 seconds
- Native executable: `./target/xml-json-converter-1.0.0-SNAPSHOT-runner` -- starts in <1 second

## Build and Deployment Options

### Standard JVM Build
- `./mvnw clean package` -- produces `target/quarkus-app/quarkus-run.jar`
- Time: ~25 seconds (after initial dependency download)
- **NEVER CANCEL**: Set timeout to 60+ seconds for initial builds

### Native Build (GraalVM)
- `./mvnw clean package -Dnative -Dquarkus.native.container-build=true`
- Time: ~3 minutes (165+ seconds) - **NEVER CANCEL**
- **CRITICAL**: Set timeout to 300+ seconds (5+ minutes) minimum
- Produces native executable: `target/xml-json-converter-1.0.0-SNAPSHOT-runner`

### Docker Builds
All require `./mvnw package` first:
- JVM Docker: `docker build -f src/main/docker/Dockerfile.jvm -t quarkus/xml-json-converter-jvm .` -- takes ~11 seconds
- Run Docker: `docker run -i --rm -p 8080:8080 quarkus/xml-json-converter-jvm`

## API Testing and Validation

### Required Validation After Changes
**ALWAYS** test both API endpoints after making any code changes:

1. **JSON Payload Endpoint Test**:
   ```bash
   curl -X POST http://localhost:8080/convert/json \
     -H "Content-Type: application/json" \
     -d '{"xmlContent": "<person><name>John</name><age>30</age></person>", "jsonContent": "{\"name\":\"John\",\"age\":\"30\"}"}'
   ```
   Expected: Should return "✅ Comparison Result: MATCH"

2. **File Upload Endpoint Test**:
   ```bash
   curl -X POST http://localhost:8080/convert/files \
     -F "xml=@example_person.xml" \
     -F "json=@example_person.json"
   ```
   Expected: Should return "✅ Comparison Result: MATCH"

3. **Difference Detection Test**:
   ```bash
   curl -X POST http://localhost:8080/convert/json \
     -H "Content-Type: application/json" \
     -d '{"xmlContent": "<person><name>John</name><age>30</age></person>", "jsonContent": "{\"name\":\"Jane\",\"age\":\"25\"}"}'
   ```
   Expected: Should return "❌ Comparison Result: DIFFERENCES FOUND"

### Complete End-to-End Validation
1. Start application: `./mvnw quarkus:dev`
2. Wait for "started in" message (~30 seconds)
3. Run all three API tests above
4. Verify expected responses are returned
5. Stop application with Ctrl+C

## Development Workflow

### Live Development
- `./mvnw quarkus:dev` -- enables hot reload, runs tests on change
- Access Dev UI: http://localhost:8080/q/dev/
- Application runs on: http://localhost:8080
- Debug port: 5005
- **Note**: First startup downloads dependencies (~30 seconds)

### Code Quality and Validation
- Test execution: `./mvnw test` -- 4 tests, completes in ~10 seconds
- Style checking: `./mvnw org.apache.maven.plugins:maven-checkstyle-plugin:3.3.0:check`
- **Always** run tests before committing changes

## Project Structure and Navigation

### Key Files and Locations
```
/
├── README.md                           # Comprehensive project documentation
├── pom.xml                            # Maven configuration, Java 17, Quarkus 3.8.1
├── mvnw, mvnw.cmd                     # Maven wrapper scripts
├── example_person.xml                 # Sample XML for testing
├── example_person.json                # Sample JSON for testing
├── src/main/java/fi/sutinse/xmljsonconverter/
│   ├── XmlJsonConverterResource.java  # REST endpoints (/convert/json, /convert/files)
│   ├── XmlJsonService.java           # Core conversion and comparison logic
│   └── FileUploadForm.java           # Form data model
├── src/test/java/fi/sutinse/xmljsonconverter/
│   └── XmlJsonConverterResourceTest.java # API integration tests
├── src/main/docker/                   # Docker configurations
│   ├── Dockerfile.jvm                # JVM container build
│   ├── Dockerfile.native             # Native container build
│   └── Dockerfile.legacy-jar         # Legacy JAR container
└── target/                           # Build output directory
```

### Core Functionality
- **XML to JSON Conversion**: Uses Jackson streaming for memory efficiency
- **JSON Comparison**: JSONAssert with lenient mode (ignores whitespace/order)
- **Markdown Output**: Human-readable difference reports
- **REST Endpoints**: Both JSON payload and multipart file upload support

## Common Tasks and Reference

### Dependency Information
- **Framework**: Quarkus 3.8.1 (Supersonic Subatomic Java)
- **Java Version**: 17 (OpenJDK Temurin 17.0.16+8)
- **Key Dependencies**: Jackson XML, JSONAssert, Commons FileUpload
- **Test Framework**: JUnit 5 with RestAssured

### Ports and URLs
- Application: http://localhost:8080
- Dev UI: http://localhost:8080/q/dev/
- Debug Port: 5005 (in dev mode)
- Test Mode: http://localhost:8081

### Build Artifacts
- JAR Application: `target/quarkus-app/quarkus-run.jar`
- Native Executable: `target/xml-json-converter-1.0.0-SNAPSHOT-runner`
- Test Reports: `target/surefire-reports/`

### Example Usage Scenarios
All examples use provided sample files:
- XML: `example_person.xml` (John Doe with address and hobbies)
- JSON: `example_person.json` (corresponding JSON structure)

## Troubleshooting

### Common Issues
- **Build Failures**: Ensure Java 17 is installed (`java -version`)
- **Dependency Download**: First build may take 60+ seconds for Maven dependencies
- **Native Build**: Requires Docker, takes 3+ minutes - never cancel
- **Port Conflicts**: Default port 8080, use `quarkus.http.port` to change

### Build Time Expectations
- **First Compile**: 45-60 seconds (dependency download)
- **Subsequent Compiles**: 5-10 seconds
- **Full Package**: 20-30 seconds
- **Test Suite**: 8-12 seconds
- **Native Build**: 160-200 seconds (3+ minutes)
- **Docker Build**: 10-15 seconds

**CRITICAL TIMEOUTS**:
- Set Maven command timeouts to 300+ seconds (5+ minutes) for native builds
- Set Maven command timeouts to 60+ seconds for standard builds
- **NEVER CANCEL** long-running builds - they are normal for Quarkus

## Integration with External Tools

### Docker Integration
- All Dockerfile variants supported
- Container builds work with `-Dquarkus.native.container-build=true`
- JVM containers ready for production deployment

### CI/CD Considerations
- Tests run automatically during package phase
- Native builds require Docker/Podman
- No additional linting setup required - Quarkus handles code generation