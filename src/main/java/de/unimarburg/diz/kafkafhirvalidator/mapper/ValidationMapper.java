/* GNU AFFERO GENERAL PUBLIC LICENSE Version 3 (C)2024 Datenintegrationszentrum Fachbereich Medizin Philipps Universität Marburg */
package de.unimarburg.diz.kafkafhirvalidator.mapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import de.unimarburg.diz.kafkafhirvalidator.cfg.ValidationFhirContext;
import de.unimarburg.diz.kafkafhirvalidator.validator.FhirProfileValidator;
import java.io.File;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ValidationMapper {

  private final FhirContext ctx;
  private final FhirProfileValidator validator;
  private final String VALID = "valid";

  public ValidationMapper(@Value("${app.profile-location}") String profileSourceFolder) {
    this.ctx = ValidationFhirContext.getInstance();
    var localValidator = new FhirProfileValidator(ValidationFhirContext.getInstance());
    if (StringUtils.hasText(profileSourceFolder)
        && ValidationMapper.checkFolderReadble(profileSourceFolder)) {
      this.validator = localValidator.withResourcesFrom(profileSourceFolder, "*.json");
    } else this.validator = localValidator;
  }

  private static boolean checkFolderReadble(String path) {

    var file = new File(path);
    if (file.exists() && file.isDirectory()) {
      return true;
    }
    return false;
  }

  public String validate(String payload) {
    Bundle bundle;
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
