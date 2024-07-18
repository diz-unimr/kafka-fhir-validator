/* GNU AFFERO GENERAL PUBLIC LICENSE Version 3 (C)2024 Datenintegrationszentrum Fachbereich Medizin Philipps Universität Marburg */
package de.unimarburg.diz.kafkafhirvalidator;

import de.unimarburg.diz.kafkafhirvalidator.mapper.ValidationMapper;
import java.util.function.Function;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class Processor {

  private final ValidationMapper validationMapper;

  @Autowired
  public Processor(ValidationMapper validationMapper) {
    this.validationMapper = validationMapper;
  }

  @Bean
  public Function<KStream<String, String>, KStream<String, String>> process() {
    return input ->
        input.mapValues(validationMapper::validate).filter((__, result) -> result != null);
  }
}
