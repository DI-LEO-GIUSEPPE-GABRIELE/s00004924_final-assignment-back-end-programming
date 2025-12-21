package bluesky.airline.config;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import bluesky.airline.entities.Role;
import bluesky.airline.repositories.RoleRepository;
import bluesky.airline.repositories.CompartmentRepository;
import bluesky.airline.entities.Compartment;
import bluesky.airline.entities.enums.CompartmentCode;
import bluesky.airline.entities.enums.RoleType;

// Component to initialize roles and compartments on application startup
@Component
public class DataInitializer {
    @org.springframework.beans.factory.annotation.Autowired
    private RoleRepository roles;
    @org.springframework.beans.factory.annotation.Autowired
    private CompartmentRepository compartments;

    // Initialize roles and compartments on application startup
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        for (RoleType roleType : RoleType.values()) {
            ensureRole(roleType);
        }

        for (CompartmentCode code : CompartmentCode.values()) {
            ensureCompartment(code);
        }
    }

    // Ensure a role exists and create it if not exists
    private void ensureRole(RoleType roleType) {
        Role r = roles.findByNameIgnoreCase(roleType.name()).orElse(new Role());
        if (r.getId() == null) {
            r.setName(roleType.name());
        }
        r.setRoleCode(roleType.getCode());
        roles.save(r);
    }

    // Ensure a compartment exists and create it if not exists
    private void ensureCompartment(CompartmentCode code) {
        compartments.findByCompartmentCode(code.name()).orElseGet(() -> {
            Compartment c = new Compartment();
            c.setCompartmentCode(code.name());
            c.setDescription(code.name().toLowerCase().replace("_", " "));
            return compartments.save(c);
        });
    }
}
