package no.nav.foreldrepenger.boot.conditionals;

import static java.lang.System.getenv;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.DEV;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.PROD;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public enum Cluster {
    LOCAL(EnvUtil.LOCAL),
    DEV_SBS(EnvUtil.DEV_SBS),
    DEV_FSS(EnvUtil.DEV_FSS),
    DEV_GCP(EnvUtil.DEV_GCP),
    PROD_GCP(EnvUtil.PROD_GCP),
    PROD_FSS(EnvUtil.PROD_FSS),
    PROD_SBS(EnvUtil.PROD_SBS);

    public static final String NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME";
    public static final String NAIS_NAMESPACE_NAME = "NAIS_NAMESPACE";

    private static final Logger LOG = LoggerFactory.getLogger(Cluster.class);

    private final String clusterName;

    Cluster(String clusterName) {
        this.clusterName = clusterName;
    }

    public String clusterName() {
        return clusterName;
    }

    public boolean isActive(Environment env, String... namespaceNames) {

        return isClusterActive(env)
                && isNamespaceActive(env, namespaceNames);
    }

    private boolean isNamespaceActive(Environment env, String... namespaceNames) {
        var namespace = namespace(env);
        if (namespaceNames.length == 0) {
            return true;
        }
        LOG.trace("Sjekker om current namespace {} er blant {}", namespace, Arrays.toString(namespaceNames));
        var aktiv = Arrays.stream(namespaceNames)
                .filter(n -> n.equals(namespace))
                .findAny();
        LOG.trace("Namespace {} i {} er {}", namespace, clusterName(), aktiv.isPresent() ? "aktivt" : "ikke aktivt");

        return aktiv.isPresent();
    }

    private static String namespace(Environment env) {
        return env.getProperty(NAIS_NAMESPACE_NAME);
    }

    public static String[] profiler() {
        return profilerFraCluster(getenv(NAIS_CLUSTER_NAME));
    }

    private static String[] profilerFraCluster(String cluster) {
        if (cluster == null) {
            LOG.info("NAIS cluster ikke detektert, antar {}", LOCAL);
            System.setProperty(NAIS_CLUSTER_NAME, EnvUtil.LOCAL);
            return new String[] { EnvUtil.LOCAL };
        }
        if (cluster.equals(EnvUtil.DEV_SBS)) {
            return new String[] { DEV, EnvUtil.DEV_SBS };
        }
        if (cluster.equals(EnvUtil.DEV_GCP)) {
            return new String[] { DEV, EnvUtil.DEV_GCP };
        }
        if (cluster.equals(EnvUtil.PROD_GCP)) {
            return new String[] { PROD, EnvUtil.PROD_GCP };
        }
        if (cluster.equals(EnvUtil.PROD_SBS)) {
            return new String[] { PROD, EnvUtil.PROD_SBS };
        }
        if (cluster.equals(EnvUtil.DEV_FSS)) {
            return new String[] { DEV, EnvUtil.DEV_FSS };
        }
        if (cluster.equals(EnvUtil.PROD_FSS)) {
            return new String[] { PROD, EnvUtil.PROD_FSS };
        }
        throw new IllegalArgumentException("Cluster " + cluster + " er ikke støttet");
    }

    public boolean isClusterActive(Environment env) {
        return env.getProperty(NAIS_CLUSTER_NAME, EnvUtil.LOCAL).equals(clusterName);
    }

    public static Cluster[] prodClusters() {
        return new Cluster[] { PROD_SBS, PROD_GCP, PROD_FSS };
    }

    public static Cluster[] devClusters() {
        return new Cluster[] { DEV_SBS, DEV_GCP, DEV_FSS };
    }

    public static Cluster[] sbsClusters() {
        return new Cluster[] { DEV_SBS, PROD_SBS };
    }

    public static Cluster[] fssClusters() {
        return new Cluster[] { DEV_FSS, PROD_FSS };
    }

    public static Cluster[] notProdClusters() {
        return new Cluster[] { DEV_SBS, DEV_GCP, DEV_FSS, LOCAL };
    }

    public static Cluster[] k8sClusters() {
        return new Cluster[] { DEV_SBS, DEV_FSS, DEV_GCP, PROD_FSS, PROD_SBS, PROD_GCP };
    }

    public static Cluster[] gcpClusters() {
        return new Cluster[] { DEV_GCP, PROD_GCP };
    }

    public static Cluster[] local() {
        return new Cluster[] { LOCAL };
    }

}
