package study_project.demo.controllers.dto;

/**
 * DTO per la creazione utente.
 */
public class CreateUserRequest {
    private String name;
    private String email;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}