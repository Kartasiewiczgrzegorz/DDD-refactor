package com.grzegorzkartasiewicz;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

  private final JavaClasses importedClasses = new ClassFileImporter()
      .importPackages("com.grzegorzkartasiewicz");

  @Test
  void domain_should_not_have_dependencies_on_other_layers() {
    LayeredArchitecture arch = layeredArchitecture().consideringAllDependencies()
        .layer("Adapters").definedBy("..adapters..")
        .layer("Application").definedBy("..app..")
        .layer("Domain").definedBy("..domain..")
        .whereLayer("Adapters").mayNotBeAccessedByAnyLayer()
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapters");
    arch.check(importedClasses);
  }
}