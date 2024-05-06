package org.myeii.labs;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HolaJsonroute extends RouteBuilder {

    @ConfigProperty(name = "holajson.dbname")
    String dbName;

    @ConfigProperty(name = "holajson.collectionname")
    String collectionName;

    @ConfigProperty(name = "holajson.endpoint")
    String endpoint;

    @Override
    public void configure() throws Exception {

      from("timer://holajsonTimer?period=20000")
        .routeId("holajson-ROUTE")
            .log("Starting Camel route: ${routeId}")
        .setHeader("CamelHttpMethod", constant("GET"))
        .toD(endpoint + "?bridgeEndpoint=true&dbname=" + dbName + "&collectionName=" + collectionName)
        .convertBodyTo(String.class)
        .log("Received HTTP response: ${body}")
        //.unmarshal().json(JsonLibrary.Jackson)
        .process(exchange -> {
            Object body = exchange.getIn().getBody();
            System.out.println("Response from holajson endpoint: " + body);
      });

    }
}

