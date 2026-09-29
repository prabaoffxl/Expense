package com.expensejar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ExpenseJar – Personal Expense Tracker API")
                        .version("1.0.0")
                        .description("RESTful Backend Service for tracking personal expenses, setting monthly budgets, and analyzing spending trends.")
                        .contact(new Contact()
                                .name("College Assessment Team")
                                .email("student@college.edu"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")));
    }
}
