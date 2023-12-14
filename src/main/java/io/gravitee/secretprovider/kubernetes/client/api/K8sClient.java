package io.gravitee.secretprovider.kubernetes.client.api;

import io.gravitee.kubernetes.client.model.v1.Event;
import io.gravitee.kubernetes.client.model.v1.Secret;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;
import io.gravitee.secretprovider.kubernetes.config.K8sSecretLocation;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface K8sClient {
    Maybe<Secret> getSecret(K8sSecretLocation location);

    Flowable<Event<Secret>> watchSecret(K8sSecretLocation location);

    K8sConfig config();
}
