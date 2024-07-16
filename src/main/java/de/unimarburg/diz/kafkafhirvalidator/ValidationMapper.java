/* GNU AFFERO GENERAL PUBLIC LICENSE Version 3 (C)2024 Datenintegrationszentrum Fachbereich Medizin Philipps Universität Marburg */
package de.unimarburg.diz.kafkafhirvalidator;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Service;

@Service
public class ValidationMapper {

  private final FhirContext ctx;
  private final FhirProfileValidator validator;
  private final String VALID = "valid";

  public ValidationMapper() {
    this.ctx = ValidationFhirContext.getInstance();
    this.validator =
        new FhirProfileValidator(ValidationFhirContext.getInstance())
            .withResourcesFrom("node_modules", "*.json");
  }

  public String validate(String payload) {
    Bundle bundle = null;
    try {
      bundle = (Bundle) ctx.newJsonParser().parseResource(payload);
    } catch (Exception e) {
      return "COULD NOT PARSE BUNDLE!!: " + e.getMessage();
    }
    return bundle.getEntry().stream()
        .map(
            res -> {
              var sb = new StringBuilder();
              final ValidationResult validationResult =
                  validator.validateWithResult(res.getResource());
              if (validationResult.isSuccessful()) {

                return VALID;
              }

              validationResult.getMessages().stream()
                  .filter(
                      m ->
                          m.getSeverity().ordinal() >= ResultSeverityEnum.ERROR.ordinal()
                              && !m.getMessage().contains("HAPI-0702")
                              && !m.getMessage()
                                  .contains(
                                      "Unknown code 'http://fhir.de/CodeSystem/bfarm/icd-10-gm")
                              && !m.getMessage()
                                  .contains(
                                      "in-memory expansion of ValueSet 'http://hl7.org/fhir/ValueSet/diagnosis-role'"))
                  .forEach(
                      m -> {
                        sb.append("ERROR: ");
                        sb.append(m.getLocationString());
                        sb.append(" - ");
                        sb.append(m.getMessage());
                        sb.append("\n");
                      });
              if (sb.isEmpty()) return "valid - some codes could not be validated";

              sb.insert(
                  0, "validation result for entry %s \n".formatted(res.getResource().getId()));
              return sb.toString();
            })
        .collect(Collectors.joining("\n"));
  }
}
