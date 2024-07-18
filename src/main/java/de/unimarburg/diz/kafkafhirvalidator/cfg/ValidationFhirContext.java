/* GNU AFFERO GENERAL PUBLIC LICENSE Version 3 (C)2024 Datenintegrationszentrum Fachbereich Medizin Philipps Universität Marburg */
package de.unimarburg.diz.kafkafhirvalidator.cfg;

import org.springframework.stereotype.Component;

@Component
public class ValidationFhirContext {

  private static final ca.uhn.fhir.context.FhirContext fhirContext =
      ca.uhn.fhir.context.FhirContext.forR4();

  public static ca.uhn.fhir.context.FhirContext getInstance() {
    return fhirContext;
  }
}
