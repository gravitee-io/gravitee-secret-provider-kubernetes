# [2.0.0](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/1.0.1...2.0.0) (2024-12-30)


### Bug Fixes

* add provided deps back to pom ([0bef5a6](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/0bef5a671a5d4937b9a13feb99c1345f37ce2c9e))
* **deps:** bump gravitee-secret-api to 1.0.0 ([45d2d77](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/45d2d77db0e4afd12783295464e79684724fb881))


### Features

* change contract for api secrets ([#33](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/issues/33)) ([03a2b33](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/03a2b331177e57f63f1b4b8895df27e2bcfdd970))


### BREAKING CHANGES

* plugin interface has changed
* Use gravitee-secret-api (many classes moved)
* Remove SecretMount in favour of SecretURL
* Flowable and Maybe are empty when secret is not found
* Remove constraint on URL starting with provider plugin

# [2.0.0-alpha.3](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/2.0.0-alpha.2...2.0.0-alpha.3) (2024-12-30)


### Bug Fixes

* **deps:** bump gravitee-secret-api to 1.0.0 ([45d2d77](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/45d2d77db0e4afd12783295464e79684724fb881))

# [2.0.0-alpha.2](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/2.0.0-alpha.1...2.0.0-alpha.2) (2024-12-14)


### Bug Fixes

* add provided deps back to pom ([0bef5a6](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/0bef5a671a5d4937b9a13feb99c1345f37ce2c9e))

# [2.0.0-alpha.1](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/1.0.1...2.0.0-alpha.1) (2024-12-12)


### Features

* change contract for api secrets ([#33](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/issues/33)) ([03a2b33](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/03a2b331177e57f63f1b4b8895df27e2bcfdd970))


### BREAKING CHANGES

* plugin interface has changed
* Use gravitee-secret-api (many classes moved)
* Remove SecretMount in favour of SecretURL
* Flowable and Maybe are empty when secret is not found
* Remove constraint on URL starting with provider plugin

## [1.0.1](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/1.0.0...1.0.1) (2023-12-14)


### Bug Fixes

* add forgotten license and vertx as provided deps ([c378168](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/c378168ba948dfa3944ba6ce4f041f8a22385492))
* allow config to contain only strings ([6ddc975](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/6ddc975bb3149ed23c1a22e271d167cc3d3f13e4))
* apply changes required by latest node-api changes ([4e1817c](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/4e1817cc5b381b28af86ea3f023b1578717bad83))
* remove unused imports ([b56f3db](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/b56f3db420c7d7e823fa23333468123da9afacda))
* unify org and gio bom versions ([bdde07f](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/bdde07fdf0e095c07b743db924b674f37d613308))
* update kubernetes client to version 3.0.0 ([3c50cdf](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/3c50cdf086835f116c027975ea10513e5b7179da))
* use 4.0.0-alpha.1 version gravitee-node ([53bacb7](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/53bacb7b9a333ce5de1f5ec3d5b65b7f17b95104))
* use released version of node ([d6095ed](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/d6095ed41031037d2e4cb53837473d6fe240cace))

# [1.0.0-alpha.4](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/1.0.0-alpha.3...1.0.0-alpha.4) (2023-10-03)


### Bug Fixes

* update kubernetes client to version 3.0.0 ([3c50cdf](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/3c50cdf086835f116c027975ea10513e5b7179da))

# [1.0.0-alpha.3](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/1.0.0-alpha.2...1.0.0-alpha.3) (2023-10-02)


### Bug Fixes

* add forgotten license and vertx as provided deps ([c378168](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/c378168ba948dfa3944ba6ce4f041f8a22385492))

# [1.0.0-alpha.2](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/1.0.0-alpha.1...1.0.0-alpha.2) (2023-10-02)


### Bug Fixes

* use 4.0.0-alpha.1 version gravitee-node ([53bacb7](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/53bacb7b9a333ce5de1f5ec3d5b65b7f17b95104))
* use released version of node ([d6095ed](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/d6095ed41031037d2e4cb53837473d6fe240cace))

# 1.0.0-alpha.1 (2023-10-02)


### Bug Fixes

* allow config to contain only strings ([6ddc975](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/6ddc975bb3149ed23c1a22e271d167cc3d3f13e4))
* apply changes required by latest node-api changes ([4e1817c](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/4e1817cc5b381b28af86ea3f023b1578717bad83))
* remove unused imports ([b56f3db](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/b56f3db420c7d7e823fa23333468123da9afacda))
* unify org and gio bom versions ([bdde07f](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/bdde07fdf0e095c07b743db924b674f37d613308))


### Features

* secret provider plugin for Kubernetes ([01403fe](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/01403fe0610a6adbd4956796511fbfaecdd2411f))

## [1.0.1-alpha.1](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/compare/1.0.0...1.0.1-alpha.1) (2023-10-02)


### Bug Fixes

* allow config to contain only strings ([0c7633e](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/0c7633eb265fa661a9c0cf9070cea397b3596d60))
* apply changes required by latest node-api changes ([e7777dc](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/e7777dcb7cce66e7fcb56527f4a3e388b9d4dfcf))
* remove unused imports ([523b19b](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/523b19bf82e134df7509d046483c429691735baf))
* unify org and gio bom versions ([4f283d7](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/4f283d72c765ed313536e68bf92da6ae768c65df))

# 1.0.0 (2023-10-02)


### Features

* secret provider plugin for Kubernetes ([01403fe](https://github.com/gravitee-io/gravitee-secret-provider-kubernetes/commit/01403fe0610a6adbd4956796511fbfaecdd2411f))
