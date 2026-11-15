package co.edu.uni.rodar.cuentas;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/** Verifica la regla de oro de la arquitectura hexagonal sobre el bytecode compilado. */
@AnalyzeClasses(packages = "co.edu.uni.rodar.cuentas", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaTest {

    @ArchTest
    static final ArchRule nucleoEsJavaPuro = noClasses()
            .that().resideInAnyPackage("..dominio..", "..aplicacion..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..", "lombok..")
            .because("dominio y aplicacion deben ser Java puro");

    @ArchTest
    static final ArchRule dominioNoDependeDeNadie = noClasses()
            .that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat().resideInAnyPackage("..aplicacion..", "..infraestructura..")
            .because("el dominio es el centro: no conoce ninguna otra capa");

    @ArchTest
    static final ArchRule aplicacionNoDependeDeInfraestructura = noClasses()
            .that().resideInAPackage("..aplicacion..")
            .should().dependOnClassesThat().resideInAPackage("..infraestructura..")
            .because("la aplicacion solo conoce puertos; los adaptadores viven en infraestructura");
}
