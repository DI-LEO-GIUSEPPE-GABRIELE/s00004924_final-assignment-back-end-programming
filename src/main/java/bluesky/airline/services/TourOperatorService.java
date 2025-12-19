package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.TourOperator;
import bluesky.airline.repositories.TourOperatorRepository;

@Service
public class TourOperatorService {
    @Autowired
    private TourOperatorRepository operators;
    @Autowired
    private UserService users;

    public Page<TourOperator> findAll(Pageable pageable) {
        return operators.findAll(pageable);
    }

    public TourOperator create(bluesky.airline.dto.touroperator.TourOperatorReqDTO body) {
        TourOperator op = new TourOperator();
        updateOperatorFromDTO(op, body);
        return operators.save(op);
    }

    public TourOperator update(UUID id, bluesky.airline.dto.touroperator.TourOperatorReqDTO body) {
        TourOperator op = findById(id);
        if (op == null) {
            throw new bluesky.airline.exceptions.NotFoundException("Tour Operator not found: " + id);
        }
        updateOperatorFromDTO(op, body);
        return operators.save(op);
    }

    private void updateOperatorFromDTO(TourOperator op, bluesky.airline.dto.touroperator.TourOperatorReqDTO body) {
        op.setCompanyName(body.getCompanyName());
        op.setVatNumber(body.getVatNumber());
        if (body.getUserId() != null) {
            bluesky.airline.entities.User u = users.findById(body.getUserId())
                    .orElseThrow(() -> new bluesky.airline.exceptions.NotFoundException(
                            "User not found: " + body.getUserId()));
            op.setUser(u);
        }
    }

    public TourOperator findById(UUID id) {
        return operators.findById(id).orElse(null);
    }

    public TourOperator save(TourOperator operator) {
        return operators.save(operator);
    }

    public void delete(UUID id) {
        operators.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return operators.existsById(id);
    }
}
