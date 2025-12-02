package study_project.demo.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Sottoclasse: pagamento con carta (SINGLE_TABLE -> stessa tabella "payments")
@Entity
@DiscriminatorValue("CARD")
public class CardPayment extends Payment {
    private String cardMasked;
    private String circuit; // es. VISA/MC

    public String getCardMasked() { return cardMasked; }
    public void setCardMasked(String cardMasked) { this.cardMasked = cardMasked; }
    public String getCircuit() { return circuit; }
    public void setCircuit(String circuit) { this.circuit = circuit; }
}

