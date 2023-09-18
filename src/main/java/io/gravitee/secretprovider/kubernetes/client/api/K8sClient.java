package io.gravitee.secretprovider.kubernetes.client.api;

import io.gravitee.secretprovider.kubernetes.client.K8sSecretWatchResult;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;
import io.gravitee.secretprovider.kubernetes.config.K8sSecretLocation;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Secret;
import io.reactivex.rxjava3.core.Flowable;
import java.util.Optional;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface K8sClient {
    Optional<V1Secret> getSecret(K8sSecretLocation location) throws ApiException;

    Flowable<K8sSecretWatchResult> watchSecret(String namespace, String secret);

    K8sConfig config();
}
