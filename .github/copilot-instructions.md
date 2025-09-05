# XML to JSON Converter Service

# XML to JSON Converter Service

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## ⚠️ Critical Prerequisites and Common Pitfalls

### Java Version Requirements
- **CRITICAL**: This project requires **Java 21** specifically
- **Common Error**: `error: release version 21 not supported` indicates wrong Java version
- **Solution Steps**:
  1. Check current version: `java -version`
  2. Install Java 21: `sudo apt install openjdk-21-jdk` (Ubuntu/Debian)
  3. Set JAVA_HOME: `export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`
  4. Verify: `java -version` should show 21.x.x

### Environment Setup Validation
Before any development work, **ALWAYS** validate:
```bash
# 1. Java version check
java -version  # Must show 21.x.x

# 2. Maven availability
./mvnw --version  # Should show Maven 3.8+ and Java 21

# 3. Initial build test
./mvnw clean compile  # Takes ~50 seconds first time

# 4. Test suite validation
./mvnw test  # Should pass all 4 tests in ~10 seconds
```

## Quick Start and Working Effectively

Bootstrap, build, and test the repository:
- Check Java version: `java -version` (requires Java 21)
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
├── pom.xml                            # Maven configuration, Java 21, Quarkus 3.26.2
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
- **Framework**: Quarkus 3.26.2 (Supersonic Subatomic Java)
- **Java Version**: 21 (OpenJDK Temurin 21.0.8+9)
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
- **Build Failures**: Ensure Java 21 is installed (`java -version`)
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

## 🔧 Advanced Error Handling and Debugging

### Common Build Error Scenarios

#### 1. Java Version Conflicts
**Symptoms**:
- `error: release version 21 not supported`
- `Unsupported class file major version 65`

**Diagnosis**:
```bash
java -version          # Check runtime version
javac -version         # Check compiler version
echo $JAVA_HOME        # Check environment variable
./mvnw --version       # Check Maven's Java detection
```

**Solutions**:
```bash
# Ubuntu/Debian - Install Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# Set environment permanently
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Verify installation
java -version && javac -version
```

#### 2. Maven Dependency Issues
**Symptoms**:
- `Could not resolve dependencies`
- `Failed to read artifact descriptor`
- Connection timeouts during build

**Diagnosis**:
```bash
# Check Maven settings
./mvnw help:system
ls -la ~/.m2/settings.xml
```

**Solutions**:
```bash
# Clear corrupted cache
rm -rf ~/.m2/repository/io/quarkus
rm -rf ~/.m2/repository/org/apache

# Force dependency refresh
./mvnw clean compile -U

# Check network connectivity
curl -I https://repo.maven.apache.org/maven2/
```

#### 3. Port Binding Issues
**Symptoms**:
- `Port 8080 already in use`
- `Address already in use: bind`

**Diagnosis**:
```bash
# Check what's using port 8080
lsof -i :8080
netstat -tulpn | grep 8080
```

**Solutions**:
```bash
# Kill process using port
kill -9 $(lsof -t -i:8080)

# Use alternative port
./mvnw quarkus:dev -Dquarkus.http.port=8081

# Configure permanently in application.properties
echo "quarkus.http.port=8081" >> src/main/resources/application.properties
```

### 4. Native Build Failures
**Symptoms**:
- `GraalVM native image build failed`
- `Could not find native-image`

**Diagnosis**:
```bash
# Check Docker availability
docker --version
docker info

# Check GraalVM installation (if not using container build)
native-image --version
```

**Solutions**:
```bash
# Use container-based build (recommended)
./mvnw package -Dnative -Dquarkus.native.container-build=true

# Install GraalVM locally (alternative)
curl -s "https://get.sdkman.io" | bash
sdk install java 21.0.0.r11-grl
sdk use java 21.0.0.r11-grl
```

## 🔒 Security Best Practices

### Development Security
1. **Dependency Security**:
   ```bash
   # Check for known vulnerabilities
   ./mvnw org.owasp:dependency-check-maven:check
   
   # Update dependencies regularly
   ./mvnw versions:display-dependency-updates
   ```

2. **File Upload Security**:
   - Maximum file size limits are enforced
   - MIME type validation is performed
   - No executable content is accepted

3. **Input Validation**:
   - XML inputs are validated against XXE attacks
   - JSON inputs are sanitized
   - All endpoints have proper error handling

### Production Security Checklist
- [ ] Disable Dev UI in production (`quarkus.dev-ui.enabled=false`)
- [ ] Configure proper logging levels
- [ ] Set up health checks (`/q/health`)
- [ ] Enable metrics monitoring (`/q/metrics`)
- [ ] Configure CORS policies if needed
- [ ] Use HTTPS in production environments

## 📊 Performance Optimization and Monitoring

### Performance Monitoring
```bash
# Enable metrics
echo "quarkus.micrometer.enabled=true" >> src/main/resources/application.properties

# Access metrics endpoint
curl http://localhost:8080/q/metrics

# JVM monitoring
curl http://localhost:8080/q/metrics/application
```

### Memory Optimization
```bash
# Development mode memory settings
./mvnw quarkus:dev -Dquarkus.dev.jvm-args="-Xmx512m -Xms256m"

# Production JVM tuning
java -Xmx256m -Xms128m -XX:+UseG1GC -jar target/quarkus-app/quarkus-run.jar

# Monitor memory usage
curl http://localhost:8080/q/health/ready
```

### Build Performance Tips
1. **Faster Builds**:
   ```bash
   # Skip tests for faster builds (development only)
   ./mvnw clean package -DskipTests
   
   # Parallel builds
   ./mvnw clean package -T 1C
   ```

2. **Native Build Optimization**:
   ```bash
   # Optimize for size
   ./mvnw package -Dnative -Dquarkus.native.additional-build-args=--optimize-for-size
   
   # Optimize for speed
   ./mvnw package -Dnative -Dquarkus.native.additional-build-args=--optimize-for-speed
   ```

## 🚀 Deployment Strategies

### Production Deployment Options

#### 1. Traditional JAR Deployment
```bash
# Build production JAR
./mvnw clean package

# Deploy with systemd service
sudo cp target/quarkus-app/quarkus-run.jar /opt/xml-json-converter/
sudo systemctl start xml-json-converter
```

#### 2. Container Deployment
```bash
# Build and deploy JVM container
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t xml-json-converter:latest .
docker run -d -p 8080:8080 --name xml-converter xml-json-converter:latest

# Build and deploy native container
./mvnw package -Dnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native -t xml-json-converter:native .
docker run -d -p 8080:8080 --name xml-converter-native xml-json-converter:native
```

#### 3. Kubernetes Deployment
```bash
# Generate Kubernetes manifests
./mvnw clean package -Dquarkus.kubernetes.deploy=true

# Apply to cluster
kubectl apply -f target/kubernetes/
```

### Environment Configuration
```bash
# Development
./mvnw quarkus:dev

# Staging
java -Dquarkus.profile=staging -jar target/quarkus-app/quarkus-run.jar

# Production
java -Dquarkus.profile=prod -jar target/quarkus-app/quarkus-run.jar
```

## 💻 Code Quality and Development Guidelines

### Code Style and Standards
1. **Java Conventions**:
   - Use Java 21 language features appropriately
   - Follow standard Oracle naming conventions
   - Maximum line length: 120 characters
   - Use meaningful variable and method names

2. **Quarkus Best Practices**:
   - Use CDI annotations (@ApplicationScoped, @Inject)
   - Leverage Quarkus configuration annotations
   - Implement proper exception handling
   - Use Quarkus logging framework

### Testing Guidelines
```bash
# Run specific test class
./mvnw test -Dtest=XmlJsonConverterResourceTest

# Run tests with coverage
./mvnw clean test jacoco:report

# Integration testing
./mvnw verify -Dquarkus.profile=test
```

### Code Review Checklist
- [ ] All tests pass (`./mvnw test`)
- [ ] Code follows project conventions
- [ ] No security vulnerabilities introduced
- [ ] Performance impact considered
- [ ] Documentation updated if needed
- [ ] API contracts maintained