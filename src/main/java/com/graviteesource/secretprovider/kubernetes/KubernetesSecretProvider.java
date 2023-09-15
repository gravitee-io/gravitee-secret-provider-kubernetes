package com.graviteesource.secretprovider.kubernetes;

import com.graviteesource.secretprovider.kubernetes.client.api.K8sClient;
import com.graviteesource.secretprovider.kubernetes.config.K8sSecretLocation;
import io.gravitee.node.api.secrets.SecretProvider;
import io.gravitee.node.api.secrets.errors.SecretManagerConfigurationException;
import io.gravitee.node.api.secrets.errors.SecretManagerException;
import io.gravitee.node.api.secrets.model.*;
import io.kubernetes.client.openapi.models.V1Secret;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

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
        try {
            K8sSecretLocation k8sLocation = K8sSecretLocation.fromLocation(secretMount.location());
            Optional<V1Secret> k8sSecret = client.getSecret(k8sLocation);
            return Maybe
                .fromOptional(k8sSecret.map(V1Secret::getData).map(SecretMap::of))
                .doOnSuccess(map -> handleWellKnowSecretKey(map, secretMount));
        } catch (Exception e) {
            return Maybe.error(new SecretManagerException(e));
        }
    }

    @Override
    public Flowable<SecretEvent> watch(SecretMount secretMount) {
        K8sSecretLocation k8sLocation = K8sSecretLocation.fromLocation(secretMount.location());
        return client
            .watchSecret(k8sLocation.namespace(), k8sLocation.secret())
            .flatMapMaybe(resp -> {
                if (resp.type() != SecretEvent.Type.DELETED) {
                    return Maybe
                        .fromOptional(
                            Optional.ofNullable(resp.v1Secret().getData()).map(data -> new SecretEvent(resp.type(), SecretMap.of(data)))
                        )
                        .doOnSuccess(event -> handleWellKnowSecretKey(event.secretMap(), secretMount));
                } else {
                    return Maybe.just(new SecretEvent(resp.type(), new SecretMap(null)));
                }
            });
    }

    @Override
    public SecretMount fromURL(SecretURL url) {
        if (!url.provider().equals(KubernetesSecretProvider.PLUGIN_ID)) {
            throw new SecretManagerConfigurationException(
                "URL is not valid for Kubernetes Secret Provider plugin. Should be %s%s//<secret>[:<data field>] but was: '%s'".formatted(
                        PLUGIN_URL_SCHEME,
                        PLUGIN_ID,
                        url
                    )
            );
        }

        K8sSecretLocation k8sSecretLocation = K8sSecretLocation.fromURL(url, client.config());

        return new SecretMount(url.provider(), new SecretLocation(k8sSecretLocation.asMap()), k8sSecretLocation.key(), url);
    }

    private void handleWellKnowSecretKey(SecretMap secretMap, SecretMount secretMount) {
        secretMap.handleWellKnownSecretKeys(
            Optional
                .ofNullable(secretMount.secretURL())
                .map(SecretURL::wellKnowKeyMap)
                .filter(MapUtils::isNotEmpty)
                .orElse(DEFAULT_WELL_KNOW_KEY_MAP)
        );
    }
}
