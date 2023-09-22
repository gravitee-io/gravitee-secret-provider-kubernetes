package io.gravitee.secretprovider.kubernetes.config;

import static io.gravitee.node.api.secrets.util.ConfigHelper.getProperty;

import io.gravitee.node.api.secrets.SecretManagerConfiguration;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@FieldNameConstants(level = AccessLevel.PRIVATE)
public class K8sConfig implements SecretManagerConfiguration {

    private final boolean enabled;
    private String kubeConfigFile;
    private int timeoutMs;
    private String namespace;

    // called by introspection
    public K8sConfig(Map<String, Object> conf) {
        Objects.requireNonNull(conf);
        enabled = getProperty(conf, Fields.enabled, Boolean.class, false);
        if (!isEnabled()) {
            return;
        }
        kubeConfigFile = getProperty(conf, Fields.kubeConfigFile, String.class, "");
        timeoutMs = getProperty(conf, Fields.timeoutMs, Integer.class, 3000);
        namespace = getProperty(conf, Fields.namespace, String.class, "default");
    }

    public boolean isClusterBased() {
        return kubeConfigFile.isBlank();
    }
}
