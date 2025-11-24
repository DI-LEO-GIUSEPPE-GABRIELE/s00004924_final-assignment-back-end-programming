package study_project.demo.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study_project.demo.dto.UserDetailsDto;
import study_project.demo.services.UserService;

@RestController
@RequestMapping("/demo/users")
public class RelationshipsController {
    private final UserService service;

    public RelationshipsController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<UserDetailsDto> details(@PathVariable UUID id) {
        return service.loadDetails(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
