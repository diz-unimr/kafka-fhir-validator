/* GNU AFFERO GENERAL PUBLIC LICENSE Version 3 (C)2024 Datenintegrationszentrum Fachbereich Medizin Philipps Universität Marburg */
package de.unimarburg.diz.kafkafhirvalidator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.stream.kafka.streams.binder.auto-create-topics=false"})
// Kafka autoconfiguration can be disabled like this
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
class ApplicationTests {

  @Test
  void contextLoads() {}
}
