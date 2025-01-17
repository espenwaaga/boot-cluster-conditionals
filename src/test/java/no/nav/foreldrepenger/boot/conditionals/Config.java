package no.nav.foreldrepenger.boot.conditionals;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Config {

    @ConditionalOnLocal
    @Bean
    public String local() {
        return "local";
    }

    @ConditionalOnDev
    @Bean
    public String dev() {
        return "dev";
    }

    @ConditionalOnSBS
    @Bean
    public String sbs() {
        return "sbs";
    }

    @ConditionalOnFSS
    @Bean
    public String fss() {
        return "fss";
    }

    @ConditionalOnClusters(clusters = Cluster.DEV_FSS, namespaces = "t4")
    @Bean
    public String fsst4() {
        return "fsst4";
    }

    @ConditionalOnClusters(clusters = Cluster.DEV_FSS, namespaces = "t1")
    @Bean
    public String fsst1() {
        return "fsst1";
    }
}
