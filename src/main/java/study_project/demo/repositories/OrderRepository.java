package study_project.demo.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study_project.demo.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    java.util.List<Order> findByUserId(UUID userId);
}
