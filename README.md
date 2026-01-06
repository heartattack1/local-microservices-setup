# Local microservices setup with Kubernetes, Kafka, Spring Boot, and Docker

This repository provides a minimal Helm chart to spin up a local development stack that includes:

- Apache Kafka with Zookeeper
- A Spring Boot service that can publish/consume messages
- Docker-friendly defaults so you can build and run your own image locally

Use this as a starting point for experimenting with microservices locally via Kind, Minikube, or any other Kubernetes cluster.

## Prerequisites

- Docker (for building/pushing your Spring Boot image)
- kubectl configured to talk to your local cluster
- Helm 3.x
- A local Kubernetes cluster (Kind or Minikube works well)

## Quick start

1. **Build your Spring Boot image** (from your service repo) and tag it with a registry your cluster can pull from:

   ```bash
   docker build -t ghcr.io/example/spring-boot-kafka:latest .
   # If using Kind, load it directly without a registry push
   kind load docker-image ghcr.io/example/spring-boot-kafka:latest
   ```

2. **Install the chart** into a dedicated namespace:

   ```bash
   helm install local-dev ./charts/local-dev \
     --create-namespace \
     --namespace local-dev \
     --set images.springApp=ghcr.io/example/spring-boot-kafka:latest
   ```

3. **Verify pods are healthy**:

   ```bash
   kubectl get pods -n local-dev
   ```

4. **Access the Spring Boot service** (port-forward from your machine):

   ```bash
   kubectl port-forward svc/spring-app 8080:8080 -n local-dev
   curl http://localhost:8080/actuator/health
   ```

5. **Use Kafka locally**. Expose the external listener and connect a local CLI/producer:

   ```bash
   kubectl port-forward svc/kafka 9094:9094 -n local-dev
   kafka-console-producer --broker-list localhost:9094 --topic demo
   ```

## Chart structure

- `charts/local-dev/values.yaml` – default images and configuration.
- `charts/local-dev/templates/namespace.yaml` – creates the target namespace.
- `charts/local-dev/templates/zookeeper.yaml` – single-node Zookeeper StatefulSet and headless service.
- `charts/local-dev/templates/kafka.yaml` – single-broker Kafka StatefulSet with both cluster-internal (9092) and local-forwarding (9094) listeners.
- `charts/local-dev/templates/spring-app.yaml` – Spring Boot Deployment and service wired to Kafka via environment variables.

## Configuration

Override any value with `--set` or a custom YAML file. Common tweaks:

- **Change the Spring Boot image**:

  ```bash
  helm upgrade --install local-dev ./charts/local-dev \
    -n local-dev \
    --set images.springApp=my-registry/my-app:dev
  ```

- **Adjust Kafka replication/partitions for tests**:

  ```bash
  helm upgrade --install local-dev ./charts/local-dev \
    -n local-dev \
    --set kafka.cfg.num\.partitions=3
  ```

- **Use NodePort for the Spring Boot service** (for direct access without port-forwarding):

  ```bash
  helm upgrade --install local-dev ./charts/local-dev \
    -n local-dev \
    --set springApp.service.type=NodePort
  ```

## Cleanup

Remove all resources when finished:

```bash
helm uninstall local-dev -n local-dev
kubectl delete namespace local-dev
```

## Notes

- The chart intentionally uses plaintext listeners for simplicity; do not use this configuration in production.
- Persistent volumes are requested for Kafka so that broker restarts keep data across pod restarts on most local clusters.
- The Spring Boot app expects `/actuator/health` for probes; adjust the path in `spring-app.yaml` or via values if your service differs.
