package study_project.demo.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study_project.demo.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}

