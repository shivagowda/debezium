/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.testing.openshift.tools.databases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.debezium.testing.openshift.tools.OpenShiftUtils;
import io.debezium.testing.openshift.tools.YAML;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.openshift.client.OpenShiftClient;

/**
 * @author Jakub Cechacek
 */
<<<<<<< HEAD:debezium-testing/debezium-testing-openshift/src/main/java/io/debezium/testing/openshift/tools/databases/DatabaseDeployer.java
public abstract class DatabaseDeployer<T extends DatabaseDeployer, C extends DatabaseController> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseDeployer.class);

    private final OpenShiftClient ocp;
    private final OpenShiftUtils ocpUtils;
    private final String dbType;
    private String project;
    private Deployment deployment;
    private List<Service> services;

    public DatabaseDeployer(String dbType, OpenShiftClient ocp) {
        this.dbType = dbType;
        this.ocp = ocp;
        this.ocpUtils = new OpenShiftUtils(ocp);
    }

    public T withProject(String project) {
        this.project = project;
        return getThis();
    }

    public T withDeployment(String yamlPath) {
        return withDeployment(YAML.fromResource(yamlPath, Deployment.class));
    }

    public T withDeployment(Deployment deployment) {
        this.deployment = deployment;
        return getThis();
    }

    public T withServices(String... yamlPath) {
        List<Service> services = Arrays.stream(yamlPath)
                .map(p -> YAML.fromResource(p, Service.class)).collect(Collectors.toList());
        return withServices(services);
    }

    public T withServices(Collection<Service> services) {
        this.services = new ArrayList<>(services);
        return getThis();
    }

    public C deploy() {
        if (deployment == null) {
            throw new IllegalStateException("Deployment configuration not available");
        }
        LOGGER.info("Deploying database");
        Deployment dep = ocp.apps().deployments().inNamespace(project).createOrReplace(deployment);

        ocpUtils.waitForPods(project, dep.getMetadata().getLabels());
=======
public abstract class AbstractOcpDatabaseDeployer<T> implements Deployer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOcpDatabaseDeployer.class);
    private final OpenShiftClient ocp;
    private final OpenShiftUtils ocpUtils;
    private final String project;
    private Deployment deployment;
    private List<Service> services;

    public AbstractOcpDatabaseDeployer(
                                       String project,
                                       Deployment deployment,
                                       List<Service> services,
                                       OpenShiftClient ocp) {
        this.ocp = ocp;
        this.ocpUtils = new OpenShiftUtils(ocp);
        this.project = project;
        this.deployment = deployment;
        this.services = services;
    }

    @Override
    public T deploy() {
        LOGGER.info("Deploying database");
        deployment = ocp.apps().deployments().inNamespace(project).createOrReplace(deployment);

        ocpUtils.waitForPods(project, deployment.getMetadata().getLabels());
>>>>>>> 8602f2e44 (DBZ-3566 Refactored database deployers and controllers):debezium-testing/debezium-testing-openshift/src/main/java/io/debezium/testing/openshift/tools/databases/AbstractOcpDatabaseDeployer.java

        List<Service> svcs = services.stream()
                .map(s -> ocp.services().inNamespace(project).createOrReplace(s))
                .collect(Collectors.toList());
        LOGGER.info("Database deployed successfully");

<<<<<<< HEAD:debezium-testing/debezium-testing-openshift/src/main/java/io/debezium/testing/openshift/tools/databases/DatabaseDeployer.java
        this.deployment = dep;
        this.services = svcs;

        return getController(dep, services, dbType, ocp);
    }

    public abstract T getThis();

    public abstract C getController(Deployment deployment, List<Service> services, String dbType, OpenShiftClient ocp);
=======
        this.services = svcs;

        return getController(deployment, services, ocp);
    }

    protected abstract T getController(Deployment deployment, List<Service> services, OpenShiftClient ocp);

    static abstract public class DatabaseBuilder<B extends DatabaseBuilder<B, D>, D extends AbstractOcpDatabaseDeployer<?>>
            implements Deployer.Builder<D> {

        protected String project;
        protected Deployment deployment;
        protected List<Service> services;
        protected OpenShiftClient ocpClient;

        public B withProject(String project) {
            this.project = project;
            return self();
        }

        public B withOcpClient(OpenShiftClient ocpClient) {
            this.ocpClient = ocpClient;
            return self();
        }

        public B withDeployment(String yamlPath) {
            this.deployment = YAML.fromResource(yamlPath, Deployment.class);
            return self();
        }

        public B withServices(String... yamlPath) {
            List<Service> services = Arrays.stream(yamlPath)
                    .map(p -> YAML.fromResource(p, Service.class))
                    .collect(Collectors.toList());
            return withServices(services);
        }

        public B withServices(Collection<Service> services) {
            this.services = new ArrayList<>(services);
            return self();
        }

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

    }
>>>>>>> 8602f2e44 (DBZ-3566 Refactored database deployers and controllers):debezium-testing/debezium-testing-openshift/src/main/java/io/debezium/testing/openshift/tools/databases/AbstractOcpDatabaseDeployer.java
}
