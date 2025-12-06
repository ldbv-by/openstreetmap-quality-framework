package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public JtsJackson3Module jtsJackson3Module() {
        return new JtsJackson3Module();
    }

/*
    @Bean
    public JtsModule jtsModule() { return new JtsModule(); }

    @Bean
    public ObjectMapper objectMapper(JtsModule jtsModule) {
        return JsonMapper.builder()
                .addModule(jtsModule)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(JtsModule jtsModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(jtsModule);
        return objectMapper;
    }*/
}
