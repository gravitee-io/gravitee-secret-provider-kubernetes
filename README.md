# gravitee-secret-provider-kubernetes

## Category

`secret-provider`

## Compatibility matrix

| Plugin version | APIM            | AM              | Java version |
|----------------|-----------------|-----------------|--------------|
| 3.x            | 4.7.x to latest | 4.7.x to latest | 21           |
| 2.x            | 4.6.x to latest | 4.6.x to latest | 17           |
| 1.x            | 4.2.x to latest | 4.4.x to latest | 17           |

The **Java version** column is the bytecode level of the latest release of each line, which sets the minimum runtime able to load the artifact — both products run on Java 21 from 4.7 onwards. The top line is not released yet: it is the one this change prepares.

## Overview

Get secrets from Kubernetes.

User may provide a specific kube config file, or rely on local or finally use cluster information when Gravitee is deployed in kubernetes.

* supports of watch for TLS configuration
* equivalent to `kubernetes://secrets` except the URL-like syntax varies.

## Documentation:

TODO provide link to documentation.gravitee.io

