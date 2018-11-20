package com.rewedigital.composer.architecture;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoCycles;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyAnalyzer;

public class DependenciesTest {

    private final AnalyzerConfig config = AnalyzerConfig.maven().main();

    @Test
    @Ignore
    public void noCycles() {
        assertThat(new DependencyAnalyzer(config).analyze(), hasNoCycles());
    }
}
