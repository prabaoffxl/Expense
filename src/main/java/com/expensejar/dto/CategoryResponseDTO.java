package com.expensejar.dto;

public class CategoryResponseDTO {
    private Long id;
    private String name;

    public CategoryResponseDTO() {}

    public CategoryResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static CategoryResponseDTOBuilder builder() { return new CategoryResponseDTOBuilder(); }

    public static class CategoryResponseDTOBuilder {
        private Long id;
        private String name;

        public CategoryResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public CategoryResponseDTOBuilder name(String name) { this.name = name; return this; }

        public CategoryResponseDTO build() {
            return new CategoryResponseDTO(id, name);
        }
    }
}
