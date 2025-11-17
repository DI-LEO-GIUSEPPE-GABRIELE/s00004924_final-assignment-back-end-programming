package study_project.demo;

// Questo file è una DEMO completa per capire:
// - Cosa sono i BEAN di Spring (oggetti gestiti dal contenitore)
// - Come funzionano i QUALIFIERS per scegliere tra più implementazioni dello stesso tipo
// - Come agisce @Primary come bean di default quando non specifichi un qualifier

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// Interfaccia di servizio: i bean concreti implementano questa API.
// Avendo più implementazioni, Spring può iniettare quella corretta tramite @Primary o @Qualifier.
interface Gateway {
    String process(String payload);
}

// BEAN 1: registrato con @Component e un NOME ESPLICITO "fastGateway".
// Questo nome sarà usato da @Qualifier("fastGateway") per selezionarlo.
@Component("fastGateway")
class FastGateway implements Gateway {
    @Override
    public String process(String payload) {
        // Simuliamo una logica "veloce": trasformiamo il testo in maiuscolo
        return payload.toUpperCase();
    }
}

// BEAN 2: anche questo è un @Component con nome "safeGateway".
// In più è marcato @Primary: se un punto di iniezione chiede un Gateway
// senza specificare @Qualifier, Spring userà questo bean come PREDEFINITO.
@Primary
@Component("safeGateway")
class SafeGateway implements Gateway {
    @Override
    public String process(String payload) {
        // Simuliamo una logica "sicura": aggiungiamo un tag
        return payload + " [safe]";
    }
}

// SERVICE che riceve un Gateway SENZA qualifier.
// Grazie a @Primary, verrà iniettato automaticamente SafeGateway.
@Service
class PrimaryPaymentService {
    private final Gateway gateway;

    PrimaryPaymentService(Gateway gateway) {
        this.gateway = gateway;
    }

    String pay(String ref) {
        return gateway.process(ref);
    }
}

// SERVICE che riceve un Gateway CON qualifier.
// Specificando @Qualifier("fastGateway") scegliamo esplicitamente
// l'implementazione FastGateway,
// ignorando il @Primary.
@Service
class QualifiedPaymentService {
    private final Gateway gateway;

    QualifiedPaymentService(@Qualifier("fastGateway") Gateway gateway) {
        this.gateway = gateway;
    }

    String pay(String ref) {
        return gateway.process(ref);
    }
}

// Runner di avvio: esegue codice subito dopo che l'applicazione parte.
// Qui stampiamo nei log il risultato dei due servizi, per vedere chi viene
// iniettato.
@Component
class BeansQualifiersRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(BeansQualifiersRunner.class);
    private final PrimaryPaymentService primaryService;
    private final QualifiedPaymentService qualifiedService;

    BeansQualifiersRunner(PrimaryPaymentService primaryService, QualifiedPaymentService qualifiedService) {
        this.primaryService = primaryService;
        this.qualifiedService = qualifiedService;
    }

    @Override
    public void run(String... args) {
        // Usa il service senza qualifier: verrà usato SafeGateway perché @Primary
        log.info("PrimaryPaymentService: {}", primaryService.pay("ref-123"));
        // Usa il service con qualifier: forza l'uso di FastGateway
        log.info("QualifiedPaymentService: {}", qualifiedService.pay("ref-123"));

        // Stampa direttamente su console per visibilità anche senza leggere i log
        System.out.println("PrimaryPaymentService: " + primaryService.pay("ref-123"));
        System.out.println("QualifiedPaymentService: " + qualifiedService.pay("ref-123"));
    }
}

// Esercizi rapidi

// - Rimuovi @Primary da SafeGateway per vedere l’errore di ambiguità quando
// PrimaryPaymentService non specifica @Qualifier .
// - Aggiungi @Qualifier("safeGateway") a PrimaryPaymentService per scegliere
// esplicitamente l’implementazione “sicura”.
// - Inverti i ruoli mettendo @Primary su FastGateway per cambiare il default.