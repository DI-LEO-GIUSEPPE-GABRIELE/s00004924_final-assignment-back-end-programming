package study_project.demo.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study_project.demo.entities.Aircraft;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, UUID> {}
