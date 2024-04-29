package org.myeii.labs;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Consumes;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.jboss.logging.Logger;

@Path("/putajson")
public class MyMongoDBApp {

    @Inject
    MongoClient mongoClient;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putJson(List<Map<String, Object>> documents,
                            @QueryParam("dbname") String dbName,
                            @QueryParam("collectionName") String collectionName, 
                            @QueryParam("health") String healthCheck) {

        final Logger LOG = Logger.getLogger(MyMongoDBApp.class);
        final Jsonb jsonb = JsonbBuilder.create();

        if ( healthCheck != null) {
            return Response.ok().build();  // Simple health check returns HTTP 200
        }

        if (dbName == null || collectionName == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Database and collection name must be provided.").build();
        }

        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<ObjectId> insertedIds = new ArrayList<>();

        if (documents.size() == 1) {
            String json = jsonb.toJson(documents);
            LOG.debugf("Received PUT request with payload: %s", json);
            Document doc = new Document(documents.get(0));
            collection.insertOne(doc);
            insertedIds.add(doc.getObjectId("_id"));
        } else {
            List<Document> docs = documents.stream().map(Document::new).collect(Collectors.toList());
            collection.insertMany(docs);
            insertedIds.addAll(docs.stream().map(d -> d.getObjectId("_id")).collect(Collectors.toList()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Data inserted successfully into " + dbName + "." + collectionName);
        response.put("insertedCount", insertedIds.size());
        response.put("insertedIds", insertedIds);

        return Response.ok(response).build();
    }
}
