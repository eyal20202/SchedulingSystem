package com.scheduling.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI schedulingSystemOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");
        
        Contact contact = new Contact();
        contact.setName("Scheduling System");
        contact.setEmail("support@scheduling-system.com");
        
        Info info = new Info()
            .title("Scheduling System API")
            .version("1.0.0")
            .description("Complete REST API for managing scheduled tasks with Quartz Scheduler")
            .contact(contact);
        
        return new OpenAPI()
            .info(info)
            .servers(List.of(localServer));
    }
}
