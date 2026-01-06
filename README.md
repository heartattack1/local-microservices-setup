# Local microservices setup with Kubernetes, Kafka (KRaft), Spring Boot, and Docker

This repository provides a minimal Helm chart to spin up a **local development stack** on Kubernetes that includes:

- **Apache Kafka (KRaft mode, no ZooKeeper)**
- A sample **Spring Boot** service that publishes heartbeat messages to Kafka
- Docker-friendly defaults for fast local iteration with Minikube or Kind

The setup is intentionally **single-node and plaintext**, designed for **local development only**.

---

## Architecture overview

### Kafka
- Runs as a **single-node Kafka cluster in KRaft mode**
- Uses the official `apache/kafka` Docker image
- Combines **broker + controller** roles in one StatefulSet pod
- No ZooKeeper dependency
- Internal listener for in-cluster clients
- Optional external listener for local CLI access via port-forward

### Spring Boot
- Simple producer application
- Connects to Kafka via the internal listener (`kafka:9092`)
- Publishes a heartbeat message to a Kafka topic on a fixed interval

---

## Prerequisites

- Docker
- kubectl (configured for your local cluster)
- Helm 3.x
- A local Kubernetes cluster:
  - **Minikube** (recommended)
  - or **Kind**

---

## Quick start (Minikube)

### 1. Start Minikube

```bash
minikube start
minikube addons enable storage-provisioner
minikube addons enable default-storageclass
```


2. Build the Spring Boot image

Build the local Spring Boot producer image:

```bash
docker build -t local/spring-kafka-sender:latest ./spring-app
```

Load it into Minikube:

```bash
minikube image load local/spring-kafka-sender:latest
```

3. Install the Helm chart
```bash
helm install local-dev ./charts/local-dev \
  --namespace local-dev \
  --create-namespace
```


4. Verify that everything is running
```bash
kubectl get pods -n local-dev
```

Expected state:

kafka-0 → `Running`

spring-app-* → `Running`


5. Access the Spring Boot service
```bash
kubectl port-forward svc/spring-app 8080:8080 -n local-dev

curl http://localhost:8080/actuator/health
```

6. Produce messages to Kafka from your local machine (optional)

Forward the external Kafka listener:

```bash
kubectl port-forward pod/kafka-0 9094:9094 -n local-dev
```

Produce a message:

```bash
kafka-console-producer --broker-list localhost:9094 --topic demo
```

Chart structure
```text
charts/local-dev/
├── values.yaml
└── templates/
    ├── namespace.yaml
    ├── kafka.yaml        # Kafka StatefulSet (KRaft mode)
    └── spring-app.yaml   # Spring Boot Deployment + Service
```


## Kafka configuration details
- Kafka runs without ZooKeeper
- Uses KRaft mode with a single node
- Broker and controller run in the same pod
- Kafka Service is headless (clusterIP: None) for stable pod DNS
- Internal listener: INTERNAL://kafka-0.kafka.local-dev.svc.cluster.local:9092
- External listener (optional): EXTERNAL://localhost:9094
- Persistent storage is disabled by default for fast startup

Configuration

Override any value using `--set` or a custom values file.

#### Change the Spring Boot image

```bash
helm upgrade --install local-dev ./charts/local-dev \
  -n local-dev \
  --set images.springApp=my-registry/my-app:dev
  ```


#### Adjust Kafka topic defaults

```bash
helm upgrade --install local-dev ./charts/local-dev \
  -n local-dev \
  --set kafka.cfg.num\.partitions=3
```

#### Change Spring Boot service type

```bash
helm upgrade --install local-dev ./charts/local-dev \
  -n local-dev \
  --set springApp.service.type=NodePort
```


Cleanup

Remove all resources:

helm uninstall local-dev -n local-dev
kubectl delete namespace local-dev


If the namespace gets stuck in Terminating (common in local clusters):

kubectl delete apiservice v1beta1.metrics.k8s.io


## Notes & limitations

- Local development only
- All Kafka listeners use PLAINTEXT
- Single-node Kafka cluster (no replication)
- Kafka data is not persisted across pod restarts
- Spring Boot health checks use /actuator/health
- The sample app publishes heartbeat-<timestamp> messages to the demo topic