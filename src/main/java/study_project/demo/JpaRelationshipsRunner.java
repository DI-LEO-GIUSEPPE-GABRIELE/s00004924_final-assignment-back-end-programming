package study_project.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import study_project.demo.entities.User;
import study_project.demo.entities.Profile;
import study_project.demo.entities.Order;
import study_project.demo.entities.Role;
import study_project.demo.repositories.UserRepository;
import study_project.demo.repositories.ProfileRepository;
import study_project.demo.repositories.OrderRepository;
import study_project.demo.repositories.RoleRepository;
import study_project.demo.repositories.PaymentRepository;
import study_project.demo.entities.Payment;
import study_project.demo.entities.CardPayment;
import study_project.demo.entities.PaypalPayment;

// Runner didattico: crea dati di esempio per le relationships JPA
@Component
public class JpaRelationshipsRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(JpaRelationshipsRunner.class);

    private final UserRepository users;
    private final ProfileRepository profiles;
    private final OrderRepository orders;
    private final RoleRepository roles;
    private final PaymentRepository payments;

    public JpaRelationshipsRunner(UserRepository users, ProfileRepository profiles, OrderRepository orders, RoleRepository roles, PaymentRepository payments) {
        this.users = users;
        this.profiles = profiles;
        this.orders = orders;
        this.roles = roles;
        this.payments = payments;
    }

    @Override
    public void run(String... args) {
        // Creiamo/recuperiamo un utente di prova
        User u = users.findByEmailIgnoreCase("relationships@example.com").orElseGet(() -> {
            User nu = new User(null, "RelUser", "relationships@example.com");
            return users.save(nu); // salva e ritorna con ID
        });

        // One-to-One: creiamo un profilo e lo colleghiamo all'utente
        if (u.getProfile() == null) {
            Profile p = new Profile();
            p.setBio("Profilo dimostrativo");
            u.setProfile(p); // owner è User (FK profile_id)
            users.save(u);   // grazie a cascade ALL su OneToOne, salva anche Profile
        }

        // One-to-Many: creiamo due ordini e li colleghiamo all'utente
        if (u.getOrders().isEmpty()) {
            Order o1 = new Order(); o1.setCode("ORD-001"); o1.setUser(u);
            Order o2 = new Order(); o2.setCode("ORD-002"); o2.setUser(u);
            orders.save(o1); orders.save(o2); // owner è Order (FK user_id)
        }

        // Many-to-Many: creiamo ruoli e li associamo all'utente
        if (u.getRoles().isEmpty()) {
            Role rAdmin = new Role(); rAdmin.setName("ADMIN");
            Role rUser = new Role(); rUser.setName("USER");
            rAdmin = roles.save(rAdmin);
            rUser = roles.save(rUser);
            u.getRoles().add(rAdmin);
            u.getRoles().add(rUser);
            users.save(u); // owner è User (tabella di join user_roles)
        }

        // Log di riepilogo
        log.info("User {} con profile_id={} ordini={} ruoli={}",
                u.getId(),
                u.getProfile() != null ? u.getProfile().getId() : null,
                u.getOrders().size(),
                u.getRoles().size());

        // Inheritance demo: SINGLE_TABLE
        if (payments.count() == 0) {
            CardPayment cp = new CardPayment();
            cp.setReference("REF-CARD-001");
            cp.setAmount(new java.math.BigDecimal("19.99"));
            cp.setCardMasked("4111********1111");
            cp.setCircuit("VISA");

            PaypalPayment pp = new PaypalPayment();
            pp.setReference("REF-PP-001");
            pp.setAmount(new java.math.BigDecimal("9.50"));
            pp.setPaypalTxnId("TXN-ABC-123");

            payments.save(cp);
            payments.save(pp);
        }
    }
}
