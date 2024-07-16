/* GNU AFFERO GENERAL PUBLIC LICENSE Version 3 (C)2024 Datenintegrationszentrum Fachbereich Medizin Philipps Universität Marburg */
package de.unimarburg.diz.kafkafhirvalidator;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import java.util.List;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.ValueSet;

public class FhirProfileValidator {

  private final PrePopulatedValidationSupport customValidation;
  private final FhirContext fhirContext;
  private final FhirValidator validator;
  private final NpmPackageValidationSupport npmPackageSupport;

  public FhirProfileValidator(FhirContext ctx) {
    fhirContext = ctx;
    validator = ctx.newValidator();

    customValidation = new PrePopulatedValidationSupport(ctx);
    npmPackageSupport = new NpmPackageValidationSupport(ctx);

    var validationSupportChain =
        new ValidationSupportChain(
            customValidation,
            npmPackageSupport,
            new SnapshotGeneratingValidationSupport(ctx),
            new DefaultProfileValidationSupport(ctx),
            new BestEffortInMemoryTerminologyServerValidationSupport(ctx));

    var cache = new CachingValidationSupport(validationSupportChain);
    var validatorModule = new FhirInstanceValidator(cache);
    validator.registerValidatorModule(validatorModule);
  }

  public FhirProfileValidator withResourcesFrom(String inputPath, String fileNamePattern) {
    return withResourcesFrom(inputPath, fileNamePattern, List.of());
  }

  public FhirProfileValidator withResourcesFrom(
      String inputPath, String fileNamePattern, List<String> resourceTypes) {
    FhirResourceLoader.loadFromDirectory(fhirContext, inputPath, fileNamePattern, resourceTypes)
        .forEach(
            r -> {
              if (r instanceof StructureDefinition) {
                customValidation.addStructureDefinition(r);
              } else if (r instanceof CodeSystem) {
                customValidation.addCodeSystem(r);
              } else if (r instanceof ValueSet) {
                customValidation.addValueSet(r);
              }
            });

    return this;
  }

  public ValidationResult validateWithResult(IBaseResource theResource) {
    return validator.validateWithResult(theResource);
  }
}
