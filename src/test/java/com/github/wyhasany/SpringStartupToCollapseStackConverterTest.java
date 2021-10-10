package com.github.wyhasany;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class SpringStartupToCollapseStackConverterTest {

    private final SpringStartupToCollapseStackConverter converter = new SpringStartupToCollapseStackConverter();

    @Test
    @DisplayName("converts Spring Boot json to collapse stack from one simple event")
    void convertsSpringBootJsonToCollapseStack() throws IOException {
        // given
        var json = readClassPathResource("startup-one-event.json");

        // when
        var parsed = converter.parse(json);

        // then
        assertThat(parsed).isEqualTo("0_spring.boot.application.starting?mainApplicationClass=tech.viacom.neutron.Application 21545");
    }

    @Test
    @DisplayName("converts Spring Boot json to collapse stack from two root events")
    void convertsSpringBootJsonToCollapseStackFromTwoRootEvents() throws IOException {
        // given
        var json = readClassPathResource("startup-two-events.json");

        // when
        var parsed = converter.parse(json);

        // then
        assertThat(parsed).isEqualTo(
            """
            0_spring.boot.application.starting?mainApplicationClass=tech.viacom.neutron.Application 21545
            1_spring.boot.application.environment-prepared 324707\
            """
        );
    }

    @Test
    @DisplayName("converts Spring Boot json to nested events")
    void convertsSpringBootJsonToCollapseStackFromTenEvents() throws IOException {
        // given
        var json = readClassPathResource("startup-nested-events.json");

        // when
        var parsed = converter.parse(json);

        // then
        assertThat(parsed).isEqualTo(
            """
            4_spring.context.refresh 5004391
            4_spring.context.refresh;5_spring.context.beans.post-process 4004094\
            """
        );
    }

    @Test
    @DisplayName("converts Spring Boot json for sample events")
    void convertsSpringBootJsonToCollapseStackFromSampleEvents() throws IOException {
        // given
        var json = readClassPathResource("startup-simple.json");

        // when
        var parsed = converter.parse(json);

        // then
        assertThat(parsed).isEqualTo(
            """
            0_spring.boot.application.starting?mainApplicationClass=tech.viacom.neutron.Application 21545
            1_spring.boot.application.environment-prepared 324707
            2_spring.boot.application.context-prepared 447
            3_spring.boot.application.context-loaded 1596
            4_spring.context.refresh 5004391
            4_spring.context.refresh;5_spring.context.beans.post-process 392447
            4_spring.context.refresh;5_spring.context.beans.post-process;6_spring.beans.instantiate?beanName=org.springframework.context.annotation.internalConfigurationAnnotationProcessor&beanType=interface org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor 47076
            4_spring.context.refresh;5_spring.context.beans.post-process;6_spring.beans.instantiate?beanName=org.springframework.context.annotation.internalConfigurationAnnotationProcessor&beanType=interface org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;7_spring.beans.instantiate?beanName=org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory 1747
            4_spring.context.refresh;5_spring.context.beans.post-process;8_spring.context.beandef-registry.post-process?postProcessor=org.springframework.context.annotation.ConfigurationClassPostProcessor@749c877b 29668
            4_spring.context.refresh;5_spring.context.beans.post-process;8_spring.context.beandef-registry.post-process?postProcessor=org.springframework.context.annotation.ConfigurationClassPostProcessor@749c877b;9_spring.context.config-classes.parse?classCount=588 3533156\
            """
        );
    }

    private String readClassPathResource(String path) throws IOException {
        return new String(this.getClass().getClassLoader().getResourceAsStream(path).readAllBytes(), UTF_8);
    }
}
