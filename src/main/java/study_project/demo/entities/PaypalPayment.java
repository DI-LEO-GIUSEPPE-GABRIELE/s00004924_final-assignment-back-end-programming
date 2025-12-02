package study_project.demo.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Sottoclasse: pagamento Paypal (SINGLE_TABLE -> stessa tabella "payments")
@Entity
@DiscriminatorValue("PAYPAL")
public class PaypalPayment extends Payment {
    private String paypalTxnId;

    public String getPaypalTxnId() { return paypalTxnId; }
    public void setPaypalTxnId(String paypalTxnId) { this.paypalTxnId = paypalTxnId; }
}

