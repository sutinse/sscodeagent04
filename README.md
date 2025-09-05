# XML to JSON Converter Service

This Quarkus application converts XML to JSON and compares it with a provided JSON, outputting the differences in Markdown format.

## Features

- **XML to JSON Conversion**: Efficiently converts XML to JSON using Jackson streaming
- **JSON Comparison**: Compares converted JSON with provided JSON, ignoring whitespace and field order
- **Markdown Output**: Presents differences in a clear, human-readable Markdown format
- **Memory Efficient**: Uses streaming for large files to minimize memory usage
- **REST API**: Provides both JSON payload and multipart file upload endpoints

## API Endpoints

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
- `xml`: XML file
- `json`: JSON file

## Usage Examples

### Using JSON endpoint:
```bash
curl -X POST http://localhost:8080/convert/json \
  -H "Content-Type: application/json" \
  -d '{
    "xmlContent": "<person><name>John</name><age>30</age></person>",
    "jsonContent": "{\"name\":\"John\",\"age\":\"30\"}"
  }'
```

### Using file upload endpoint:
```bash
curl -X POST http://localhost:8080/convert/files \
  -F "xml=@example_person.xml" \
  -F "json=@example_person.json"
```

## Example Files

The repository includes example files:
- `example_person.xml`: Sample XML file
- `example_person.json`: Corresponding JSON file

## Running the Application

### Development Mode
```bash
./mvnw quarkus:dev
```

### Production Mode
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

### Testing
```bash
./mvnw test
```

## Technical Details

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

## Original Quarkus Documentation

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/xml-json-converter-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

