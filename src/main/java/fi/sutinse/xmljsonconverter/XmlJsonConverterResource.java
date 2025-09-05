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
        try {
            // Create InputStreams from the string content
            InputStream xmlStream = new ByteArrayInputStream(request.xmlContent.getBytes(StandardCharsets.UTF_8));
            InputStream jsonStream = new ByteArrayInputStream(request.jsonContent.getBytes(StandardCharsets.UTF_8));
            
            FileUploadForm form = new FileUploadForm();
            form.xmlFile = xmlStream;
            form.jsonFile = jsonStream;
            
            String result = xmlJsonService.convertXmlToJsonAndCompare(form);
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response convertAndCompareFiles(
            @FormParam("xml") InputStream xmlFile,
            @FormParam("json") InputStream jsonFile) {
        try {
            if (xmlFile == null || jsonFile == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Both XML and JSON files are required")
                        .build();
            }

            FileUploadForm form = new FileUploadForm();
            form.xmlFile = xmlFile;
            form.jsonFile = jsonFile;
            
            String result = xmlJsonService.convertXmlToJsonAndCompare(form);
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    public static class ConversionRequest {
        public String xmlContent;
        public String jsonContent;
    }
}