package study_project.demo.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study_project.demo.entities.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}
