package com.expensejar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    public CategoryRequestDTO() {}

    public CategoryRequestDTO(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static CategoryRequestDTOBuilder builder() { return new CategoryRequestDTOBuilder(); }

    public static class CategoryRequestDTOBuilder {
        private String name;

        public CategoryRequestDTOBuilder name(String name) { this.name = name; return this; }

        public CategoryRequestDTO build() {
            return new CategoryRequestDTO(name);
        }
    }
}
