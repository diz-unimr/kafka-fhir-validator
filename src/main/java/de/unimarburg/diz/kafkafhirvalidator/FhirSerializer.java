/* GNU AFFERO GENERAL PUBLIC LICENSE Version 3 (C)2024 Datenintegrationszentrum Fachbereich Medizin Philipps Universität Marburg */
package de.unimarburg.diz.kafkafhirvalidator;

import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.Serializer;
import org.hl7.fhir.r4.model.Resource;

public class FhirSerializer<T extends Resource> implements Serializer<T> {

  @Override
  public byte[] serialize(String topic, T data) {
    if (data == null) {
      return null;
    }

    return ValidationFhirContext.getInstance()
        .newJsonParser()
        .encodeResourceToString(data)
        .getBytes(StandardCharsets.UTF_8);
  }
}
