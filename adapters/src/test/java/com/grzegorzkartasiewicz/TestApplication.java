package com.grzegorzkartasiewicz;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Testowa klasa konfiguracyjna Spring Boot.
 * Ta klasa jest niezbędna, aby testy "slice" (takie jak @DataJpaTest)
 * mogły znaleźć konfigurację @SpringBootConfiguration w projekcie wielomodułowym.
 * Powinna być umieszczona w pakiecie nadrzędnym w stosunku do testowanych komponentów,
 * aby skanowanie komponentów przez Springa mogło je odnaleźć.
 */
@SpringBootApplication
public class TestApplication {
    // Ta klasa może być pusta. Jej jedynym celem jest dostarczenie
    // niezbędnej adnotacji, aby kontekst testowy załadował się poprawnie.
}
