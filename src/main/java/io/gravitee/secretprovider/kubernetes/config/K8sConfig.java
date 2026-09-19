/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.secretprovider.kubernetes.config;

import static io.gravitee.secrets.api.util.ConfigHelper.getProperty;

import io.gravitee.secrets.api.plugin.SecretManagerConfiguration;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
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
        namespace = getProperty(conf, Fields.namespace, String.class, "");
    }

    public boolean isClusterBased() {
        return kubeConfigFile.isBlank();
    }
}
