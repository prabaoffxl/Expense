package com.expensejar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {

    @NotBlank(message = "Name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Email format must be valid")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    public UserRequestDTO() {}

    public UserRequestDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public static UserRequestDTOBuilder builder() { return new UserRequestDTOBuilder(); }

    public static class UserRequestDTOBuilder {
        private String name;
        private String email;

        public UserRequestDTOBuilder name(String name) { this.name = name; return this; }
        public UserRequestDTOBuilder email(String email) { this.email = email; return this; }

        public UserRequestDTO build() {
            return new UserRequestDTO(name, email);
        }
    }
}
