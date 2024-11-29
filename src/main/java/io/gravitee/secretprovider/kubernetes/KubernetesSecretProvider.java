package io.gravitee.secretprovider.kubernetes;

import io.gravitee.node.api.secrets.SecretProvider;
import io.gravitee.node.api.secrets.errors.SecretManagerException;
import io.gravitee.node.api.secrets.model.*;
import io.gravitee.secretprovider.kubernetes.client.api.K8sClient;
import io.gravitee.secretprovider.kubernetes.config.K8sSecretLocation;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class KubernetesSecretProvider implements SecretProvider {

    private static final Map<String, SecretMap.WellKnownSecretKey> DEFAULT_WELL_KNOW_KEY_MAP = Map.of(
        "tls.crt",
        SecretMap.WellKnownSecretKey.CERTIFICATE,
        "tls.key",
        SecretMap.WellKnownSecretKey.PRIVATE_KEY,
        "username",
        SecretMap.WellKnownSecretKey.USERNAME,
        "password",
        SecretMap.WellKnownSecretKey.PASSWORD
    );
    public static final String PLUGIN_ID = "kubernetes";

    private final K8sClient client;

    public KubernetesSecretProvider(K8sClient client) {
        this.client = client;
    }

    @Override
    public Maybe<SecretMap> resolve(SecretMount secretMount) {
        K8sSecretLocation k8sLocation = K8sSecretLocation.fromLocation(secretMount.location());
        return client
            .getSecret(k8sLocation)
            .map(k8sSecret -> {
                SecretMap sm = SecretMap.ofBase64(k8sSecret.getData());
                handleWellKnownSecretKeys(sm, secretMount);
                return sm;
            });
    }

    @Override
    public Flowable<SecretEvent> watch(SecretMount secretMount) {
        K8sSecretLocation k8sLocation = K8sSecretLocation.fromLocation(secretMount.location());
        return client
            .watchSecret(k8sLocation)
            .map(event -> {
                if (event.getType().equals("ERROR")) {
                    throw new SecretManagerException("Kubernetes watch on %s return a error: %s".formatted(k8sLocation, event));
                }
                if (event.getType().equals("DELETED")) {
                    return new SecretEvent(SecretEvent.Type.DELETED, new SecretMap(null));
                }
                SecretMap secretMap = SecretMap.ofBase64(event.getObject().getData());
                handleWellKnownSecretKeys(secretMap, secretMount);
                return new SecretEvent(SecretEvent.Type.UPDATED, secretMap);
            })
            .skip(1)
            .startWith(resolve(secretMount).map(secretMap -> new SecretEvent(SecretEvent.Type.CREATED, secretMap)));
    }

    @Override
    public SecretMount fromURL(SecretURL url) {
        K8sSecretLocation k8sSecretLocation = K8sSecretLocation.fromURL(url, client.config());
        return new SecretMount(url.provider(), new SecretLocation(k8sSecretLocation.asMap()), k8sSecretLocation.key(), url);
    }

    private void handleWellKnownSecretKeys(SecretMap secretMap, SecretMount secretMount) {
        secretMap.handleWellKnownSecretKeys(
            Optional
                .ofNullable(secretMount.secretURL())
                .map(SecretURL::wellKnowKeyMap)
                .filter(map -> !map.isEmpty())
                .orElse(DEFAULT_WELL_KNOW_KEY_MAP)
        );
    }
}
