# XML to JSON Converter Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.26.2-blue.svg)](https://quarkus.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](#building-and-testing)

A high-performance Quarkus application that converts XML to JSON and provides intelligent comparison capabilities with human-readable difference reporting in Markdown format.

## 🚀 Features

- **🔄 XML to JSON Conversion**: Efficiently converts XML to JSON using Jackson streaming parser
- **📊 Intelligent JSON Comparison**: Compares converted JSON with provided JSON, ignoring whitespace and field order
- **📝 Markdown Output**: Presents differences in clear, human-readable Markdown format with visual indicators
- **⚡ Memory Efficient**: Uses streaming for large files to minimize memory footprint
- **🌐 REST API**: Dual endpoints supporting both JSON payloads and multipart file uploads
- **🔧 Production Ready**: Built with Quarkus for supersonic startup times and low memory usage

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 21** (OpenJDK or Oracle JDK)
  ```bash
  java -version
  # Should show version 21.x.x
  ```
- **Maven 3.8+** (or use included Maven wrapper)
- **Docker** (optional, for containerized builds)

### ⚠️ Important: Java Version Requirement

This application **requires Java 21**. If you have a different Java version installed:

- **Ubuntu/Debian**: `sudo apt install openjdk-21-jdk`
- **macOS**: `brew install openjdk@21`
- **Windows**: Download from [Adoptium](https://adoptium.net/temurin/releases/?version=21)

Verify installation:
```bash
java -version
javac -version
```

## 🏃‍♂️ Quick Start

### 1. Clone and Build
```bash
git clone <repository-url>
cd sscodeagent04
./mvnw clean compile  # First build downloads dependencies (~45-60 seconds)
```

### 2. Run Tests
```bash
./mvnw test  # Runs in ~10 seconds
```

### 3. Start Development Server
```bash
./mvnw quarkus:dev  # Starts in ~30 seconds with live reload
# Application available at: http://localhost:8080
# Dev UI available at: http://localhost:8080/q/dev/
```

### 4. Test the API
```bash
# Test with example files
curl -X POST http://localhost:8080/convert/files \
  -F "xml=@example_person.xml" \
  -F "json=@example_person.json"

# Expected response: "✅ Comparison Result: MATCH"
```

## 🔌 API Endpoints

### 1. JSON Payload Endpoint
```
POST /convert/json
Content-Type: application/json
```

Request body:
```json
{
  "xmlContent": "<person><name>John</name><age>30</age></person>",
  "jsonContent": "{\"name\":\"John\",\"age\":\"30\"}"
}
```

### 2. Multipart File Upload Endpoint
```
POST /convert/files
Content-Type: multipart/form-data
```

Form parameters:
- `xml`: XML file (validated for XML content type)
- `json`: JSON file (validated for JSON content type)

**Response Examples:**
- **Match**: `✅ Comparison Result: MATCH`
- **Differences**: `❌ Comparison Result: DIFFERENCES FOUND` + detailed Markdown diff

## 💡 Usage Examples

### Using JSON Payload Endpoint
```bash
curl -X POST http://localhost:8080/convert/json \
  -H "Content-Type: application/json" \
  -d '{
    "xmlContent": "<person><name>John</name><age>30</age></person>",
    "jsonContent": "{\"name\":\"John\",\"age\":\"30\"}"
  }'
```

**Expected Response:**
```
✅ Comparison Result: MATCH
```

### Using File Upload Endpoint
```bash
curl -X POST http://localhost:8080/convert/files \
  -F "xml=@example_person.xml" \
  -F "json=@example_person.json"
```

**Expected Response:**
```
✅ Comparison Result: MATCH
```

### Testing Difference Detection
```bash
curl -X POST http://localhost:8080/convert/json \
  -H "Content-Type: application/json" \
  -d '{
    "xmlContent": "<person><name>John</name><age>30</age></person>",
    "jsonContent": "{\"name\":\"Jane\",\"age\":\"25\"}"
  }'
```

**Expected Response:**
```
❌ Comparison Result: DIFFERENCES FOUND

## Differences Found:
- **name**: Expected "John" but was "Jane"
- **age**: Expected "30" but was "25"
```

## 📁 Example Files

The repository includes sample files for testing:

**example_person.xml**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<person>
    <name>John Doe</name>
    <age>30</age>
    <address>
        <street>123 Main St</street>
        <city>Helsinki</city>
        <country>Finland</country>
    </address>
    <hobbies>
        <hobby>reading</hobby>
        <hobby>swimming</hobby>
        <hobby>coding</hobby>
    </hobbies>
</person>
```

**example_person.json**:
```json
{
  "name": "John Doe",
  "age": "30",
  "address": {
    "street": "123 Main St",
    "city": "Helsinki",
    "country": "Finland"
  },
  "hobbies": {
    "hobby": ["reading", "swimming", "coding"]
  }
}
```

## 🔧 Building and Running

### Development Mode (Recommended)
```bash
./mvnw quarkus:dev
```
- **Startup Time**: ~30 seconds (first run), ~10 seconds (subsequent)
- **Features**: Live reload, automatic test execution, Dev UI
- **URLs**: 
  - Application: http://localhost:8080
  - Dev UI: http://localhost:8080/q/dev/
- **Debug Port**: 5005

### Production JAR Build
```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```
- **Build Time**: ~25 seconds (after dependencies downloaded)
- **Startup Time**: ~2 seconds
- **Memory Usage**: Optimized for production

### Native Executable (GraalVM)
```bash
# Requires Docker
./mvnw clean package -Dnative -Dquarkus.native.container-build=true
./target/xml-json-converter-1.0.0-SNAPSHOT-runner
```
- **Build Time**: ~3-5 minutes ⚠️ (be patient!)
- **Startup Time**: <1 second
- **Memory Usage**: Minimal footprint

### Building and Testing
#### Run Tests
```bash
./mvnw test  # Executes 4 integration tests (~10 seconds)
```

#### Build Validation
```bash
./mvnw clean package  # Full build with tests
```

## 🛠️ Development Workflow

### Live Development
1. Start dev mode: `./mvnw quarkus:dev`
2. Make code changes - they're automatically reloaded
3. Tests run automatically on file changes
4. Access Dev UI for debugging: http://localhost:8080/q/dev/

### Code Quality
```bash
# Run tests
./mvnw test

# Style checking (if configured)
./mvnw checkstyle:check

# Package for production
./mvnw clean package
```

## 🐳 Docker Support

### Build JVM Container
```bash
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t xml-json-converter .
docker run -p 8080:8080 xml-json-converter
```

### Build Native Container
```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native -t xml-json-converter-native .
docker run -p 8080:8080 xml-json-converter-native
```

- **Framework**: Quarkus 3.26.2
- **Java Version**: 21
- **XML Processing**: Jackson XML mapper with streaming support
- **JSON Comparison**: JSONAssert with lenient comparison mode
- **Output Format**: Markdown with clear difference reporting

## Requirements Implemented

✅ REST API with multipart support  
✅ File type and MIME type validation  
✅ Efficient XML to JSON conversion using streaming  
✅ Memory-efficient processing for large files  
✅ JSON comparison with whitespace ignoring  
✅ Markdown output for differences  
✅ Proper error handling and validation  

---


## ⚡ Technical Details

### Architecture
- **Framework**: Quarkus 3.26.2 (Supersonic Subatomic Java)
- **Java Version**: 21 (LTS)
- **Build Tool**: Maven 3.8+
- **Packaging**: JAR / Native executable

### Key Dependencies
- **XML Processing**: Jackson XML Mapper with streaming support
- **JSON Comparison**: JSONAssert with lenient comparison mode
- **File Upload**: Apache Commons FileUpload2 Jakarta
- **Testing**: JUnit 5 + RestAssured
- **Output Format**: Markdown with visual difference indicators

### Performance Characteristics
| Metric | Development | Production JAR | Native |
|--------|-------------|----------------|---------|
| Startup Time | ~30s (first), ~10s | ~2s | <1s |
| Memory Usage | ~200MB | ~100MB | ~50MB |
| Build Time | ~5s | ~25s | ~3-5min |

## 🔍 Troubleshooting

### Common Issues

#### Java Version Mismatch
**Error**: `error: release version 21 not supported`
**Solution**: 
```bash
# Check current version
java -version

# Install Java 21 (Ubuntu/Debian)
sudo apt install openjdk-21-jdk

# Set JAVA_HOME if needed
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
```

#### Build Failures
**Error**: Dependencies not downloading
**Solution**: 
```bash
# Clear Maven cache and retry
rm -rf ~/.m2/repository/io/quarkus
./mvnw clean compile
```

#### Port Already in Use
**Error**: `Port 8080 already in use`
**Solution**:
```bash
# Use different port
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

#### Native Build Issues
**Error**: Native compilation fails
**Solution**:
```bash
# Ensure Docker is running
docker --version

# Use container build
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

### Performance Tips
- **Memory**: Use `-Xmx512m` for development, `-Xmx256m` for production
- **Startup**: Native builds start fastest, JAR builds have good balance
- **Development**: Use dev mode for fastest iteration cycles

## 📚 API Documentation

### Response Formats

#### Success Response (Match)
```
✅ Comparison Result: MATCH
```

#### Difference Response
```
❌ Comparison Result: DIFFERENCES FOUND

## Differences Found:
- **field_name**: Expected "expected_value" but was "actual_value"
- **nested.field**: Expected "value1" but was "value2"

## Additional Context:
- Total fields compared: X
- Fields with differences: Y
```

### Error Responses
- **400 Bad Request**: Invalid XML/JSON format
- **415 Unsupported Media Type**: Wrong file type uploaded
- **500 Internal Server Error**: Processing error

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Install Java 21
3. Run `./mvnw clean compile` to verify setup
4. Make changes and test with `./mvnw test`
5. Submit pull request

### Code Style
- Use standard Java conventions
- Write tests for new features
- Ensure all tests pass before submitting

### Testing
All endpoints must be tested:
```bash
# Run full test suite
./mvnw test

# Test specific API endpoints
curl -X POST http://localhost:8080/convert/json ...
curl -X POST http://localhost:8080/convert/files ...
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with [Quarkus](https://quarkus.io/) - Supersonic Subatomic Java Framework
- XML processing powered by [Jackson](https://github.com/FasterXML/jackson)
- JSON comparison using [JSONAssert](https://github.com/skyscreamer/JSONassert)

---

**⭐ Star this repository if you find it useful!**
