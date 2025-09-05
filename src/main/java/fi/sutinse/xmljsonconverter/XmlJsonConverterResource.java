package fi.sutinse.xmljsonconverter;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Path("/convert")
@Produces(MediaType.TEXT_PLAIN)
public class XmlJsonConverterResource {

    @Inject
    XmlJsonService xmlJsonService;

    @POST
    @Path("/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response convertAndCompareJson(ConversionRequest request) {
        // Create InputStreams from the string content
        InputStream xmlStream = new ByteArrayInputStream(request.xmlContent().getBytes(StandardCharsets.UTF_8));
        InputStream jsonStream = new ByteArrayInputStream(request.jsonContent().getBytes(StandardCharsets.UTF_8));
        
        FileUploadForm form = new FileUploadForm(xmlStream, jsonStream);
        
        // Service now handles all errors internally and returns formatted result
        String result = xmlJsonService.convertXmlToJsonAndCompare(form);
        return Response.ok(result).build();
    }

    @POST
    @Path("/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response convertAndCompareFiles(
            @FormParam("xml") InputStream xmlFile,
            @FormParam("json") InputStream jsonFile) {
        if (xmlFile == null || jsonFile == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Both XML and JSON files are required")
                    .build();
        }

        FileUploadForm form = new FileUploadForm(xmlFile, jsonFile);
        
        // Service now handles all errors internally and returns formatted result
        String result = xmlJsonService.convertXmlToJsonAndCompare(form);
        return Response.ok(result).build();
    }

    /**
     * Record representing a conversion request with XML and JSON content.
     * Uses modern Java record syntax for immutable data transfer.
     * 
     * @param xmlContent the XML content as string
     * @param jsonContent the JSON content as string
     */
    public record ConversionRequest(String xmlContent, String jsonContent) {
        /**
         * Compact constructor with validation.
         */
        public ConversionRequest {
            if (xmlContent == null || xmlContent.trim().isEmpty()) {
                throw new IllegalArgumentException("XML content is required");
            }
            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                throw new IllegalArgumentException("JSON content is required");
            }
        }
    }
}