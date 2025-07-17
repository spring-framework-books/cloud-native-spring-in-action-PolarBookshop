# Polar Bookshop: A cloud native application
Polar Bookshop is a specialized bookshop whose mission is to spread knowledge and
information about the North Pole and the Arctic, where the bookshop is located: the Arctic’s history, geography, animals, and so on. The organization managing the
bookshop, Polarsophia, has decided to start selling its books online to spread them
worldwide, but that is only the beginning. The project is very ambitious, and the vision
includes a suite of software products to fulfill the Polarsophia mission. After the successful pilot project earlier in this chapter, the organization has decided to embark on
a cloud native journey.

The management plans to deliver new
features in short iterations, reducing the time to market and getting early feedback
from users. Their goal is to bring the bookshop close to everyone, everywhere, so the
application should be highly scalable. With a worldwide audience, such a system needs
to be highly available, so resilience is essential.
Polarsophia is a small organization, and they need to optimize costs, especially
those regarding infrastructure. They can’t afford to build their own data center, so
they have decided to rent IT hardware from a third party.

Books will be available for sale through the application. When a customer purchases a book, they should be able to check on the status of their order. Two categories of people will use the Polar Bookshop application:
* Customers can browse books in the catalog, buy some, and check their orders.
* Employees can manage books, update existing ones, and add new items to the
catalog.

Next figure describes the architecture of the Polar Bookshop cloud native system. As
you can see, it’s made up of several services. Some will implement the business logic of
the system to provide the functionality already mentioned. Other services will implement shared concerns like centralized configuration. For clarity, the figure doesn’t
show the services responsible for the security and observability concerns. 
![alt text](image.png)

## patterns and technologies used in the project
Here I’ll give you an overview of
the main concerns we will address and the technologies and patterns we’ll use to
accomplish them.

### WEB AND INTERACTIONS
Polar Bookshop comprises several services that will have to communicate with each
other to provide their functionality. You’ll build RESTful services that interact synchronously over HTTP, both in a blocking way (using traditional servlets) and nonblocking ways (using reactive programming). Spring MVC and Spring WebFlux (on
top of Project Reactor) will be your main tools for accomplishing such a result.
When building cloud native applications, you should design loosely coupled services and consider how you can keep data consistent in a distributed system context.
Synchronous communications can generate problems when multiple services are
involved in accomplishing a function. That’s why event-driven programming is getting very popular in the cloud: it allows you to overcome the issues of synchronous
communications.
I’ll show you how to use events and messaging systems to decouple services and
ensure data consistency. You’ll use Spring Cloud Stream to implement data streams
between services and Spring Cloud Function to define message handlers as functions.
The latter approach could naturally evolve in serverless applications deployed to platforms like Azure Functions, AWS Lambda, or Knative. 
### DATA
Data is a crucial part of software systems. In Polar Bookshop you’ll use the PostgreSQL
relational database to permanently store the data processed by the application. I’ll
show you how to integrate an application with a data source using Spring Data JDBC
(imperative) and Spring Data R2DBC (reactive). You’ll then learn how to evolve a
data source and manage schema migrations with Flyway.
Cloud native applications should be stateless, but the state needs to be stored
somewhere. In Polar Bookshop you’ll use Redis to externalize the session storage to a
data store and keep applications stateless and scalable. Spring Session makes it easy to
implement clustered user sessions. In particular, I’ll show you how to use Spring Session Data Redis to integrate the application session management with Redis.
Besides persisted data and session data, you’ll also deal with messages to implement event-driven programming patterns. Spring AMQP and RabbitMQ will be the
technologies you’ll use to do so.
Locally, you’ll run these data services in Docker containers. In production, you’ll
rely on the managed services offered by a cloud provider like DigitalOcean or Azure,
which take care of critical concerns like high availability, clustering, storage, and data
replication. 
### CONFIGURATION
Throughout the book I’ll show you how to configure the services in Polar Bookshop
in different ways. I’ll start by exploring the options offered by Spring Boot properties
and profiles, and when to use them. You’ll then learn how to apply external configuration by using environment variables when running a Spring application as a JAR and
as a container. Then you’ll see how to centralize the configuration management through
a configuration server with Spring Cloud Config. Finally, I’ll teach you how to use
ConfigMaps and Secrets in Kubernetes. 
### ROUTING
Polar Bookshop, being a distributed system, will need some routing configuration.
Kubernetes has a built-in service discovery feature that can help you decouple services
from their physical addresses and hostnames. Cloud native applications are scalable,
so any interactions between them should take that into account: which instance
should you call? Once again, Kubernetes offers you a load-balancing feature natively,
so you don’t need to implement anything in your application.
Using Spring Cloud Gateway, I’ll guide you through implementing a service that
will act as an API gateway to shield the outside from any internal API changes. It will
also be an edge service that you’ll use to address cross-cutting concerns, like security
and resilience, in one place. Such a service will be the entry point to Polar Bookshop, and it will have to be highly available, performant, and fault-tolerant. 
### OBSERVABILITY
The services in the Polar Bookshop system should be observable to be defined as
cloud native. I’ll show you how to use Spring Boot Actuator to set up health and info
endpoints and expose metrics with Micrometer to be fetched and processed by Prometheus. You’ll then use Grafana to visualize the most critical metrics in informative
dashboards.
Requests can be handled by more than one service, so you’ll need distributed tracing functionality to follow the request flow from one service to another. You’ll set that
up with OpenTelemetry. Then Grafana Tempo will fetch, process, and visualize the
traces to give you a complete picture of how the system accomplishes its functions.
Finally you’ll need a logging strategy in place. We should handle logs as event
streams, so you’ll make your Spring applications stream log events to the standard output without considering how they are processed or stored. Fluent Bit will take care of
collecting logs from all services, Loki will store and process them, and Grafana will let
you browse them. 
### RESILIENCE
Cloud native applications should be resilient. For the Polar Bookshop project, I’ll
show you various techniques for making an application resilient using Project Reactor,
Spring Cloud Circuit Breaker, and Resilience4J to implement circuit breakers, retries,
timeouts, and other patterns. 
### SECURITY
Security is a vast subject that I won’t be able to cover deeply in this book. Still, I recommend exploring the topic, since it’s one of the most critical software concerns nowadays. It’s a pervasive concern that should be addressed continuously from the very
beginning of the project.
For Polar Bookshop, I’ll show you how to add authentication and authorization
functionality to cloud native applications. You’ll see how to secure communications between services, and between users and applications. OAuth 2.1 and OpenID Connect
will be the standards you’ll rely on to implement such functionality. Spring Security
supports those standards and integrates seamlessly with external services to provide
authentication and authorization. You’ll use Keycloak for identity and access control
management.
Also, I’ll introduce the concepts of secrets management and encryption. I won’t be
able to go too deep into those topics, but I’ll show you how to manage secrets to configure Spring Boot applications. 
### TESTING
Automated testing is paramount for the success of a cloud native application. A few
levels of automated tests will cover the Polar Bookshop application. I’ll show you how
to use JUnit5 to write unit tests. Spring Boot adds many convenient utilities that
improve integration testing, and you will use them to ensure the quality of your services. You’ll write tests for the various features used in Polar Bookshop, including
REST endpoints, messaging streams, data integrations, and security.
Keeping parity across environments is essential to ensure the quality of your applications. This is especially true when it comes to backing services. In production, you
will use services such as PostgreSQL and Redis. During testing, you should use similar
services rather than mocks or test-specific tools like the H2 in-memory database. The
Testcontainers framework will help you use real services as containers in your automated tests. 
### BUILD AND DEPLOYMENT
Polar Bookshop’s primary services will use Spring. You’ll see how to package a Spring
application, run it as a JAR file, containerize it with Cloud Native Buildpacks, run it with
Docker, and finally deploy containers with Kubernetes. You’ll also see how to compile
Spring applications to native images using Spring Native and GraalVM, and use them in
serverless architectures, taking advantage of their instant startup time, instant peak performance, reduced memory consumption, and reduced image size. Then you’ll deploy
them on a serverless platform built on top of Kubernetes with Knative.
I’ll show you how to automate the build stage by setting up a deployment pipeline
with GitHub Actions. The pipeline will build the application at every commit, run the
tests, and package it ready for deployment. Such automation will support a continuous delivery culture to bring value to customers quickly and reliably. In the end, you’ll
also automate the deployment of Polar Bookshop to the production Kubernetes cluster using GitOps practices and Argo CD. 
### UI
This book is focused on backend technologies, so I won’t teach you any frontend subjects. Of course, your application will need a frontend for the users to interact with. In
the case of Polar Bookshop, you’ll rely on a client application using the Angular
framework. 

## requirements

### Catalog Service
Catalog Service will be responsible for supporting the following use cases:
* View the list of books in the catalog.
* Search books by their International Standard Book Number (ISBN).
* Add a new book to the catalog.
* Edit information for an existing book.
* Remove a book from the catalog.
#### REST API

| Endpoint	      | Method   | Req. body  | Status | Resp. body     | Description    		   	     |
|:---------------:|:--------:|:----------:|:------:|:--------------:|:-------------------------------|
| `/books`        | `GET`    |            | 200    | Book[]         | Get all the books in the catalog. |
| `/books`        | `POST`   | Book       | 201    | Book           | Add a new book to the catalog. |
|                 |          |            | 422    |                | A book with the same ISBN already exists. |
| `/books/{isbn}` | `GET`    |            | 200    | Book           | Get the book with the given ISBN. |
|                 |          |            | 404    |                | No book with the given ISBN exists. |
| `/books/{isbn}` | `PUT`    | Book       | 200    | Book           | Update the book with the given ISBN. |
|                 |          |            | 200    | Book           | Create a book with the given ISBN. |
| `/books/{isbn}` | `DELETE` |            | 204    |                | Delete the book with the given ISBN. |

### Order Service
Order Service will provide functionality for purchasing books. Like Catalog Service, it
will expose a REST API and store data in a PostgreSQL database. Unlike Catalog Ser-
vice, it will use the reactive programming paradigm to improve scalability, resilience,
and cost-effectiveness.

Order Service will also interact with Catalog Service through its REST API to fetch
details about books and check their availability. 

Orders can go through different phases. If the requested book is available in the catalog, then the order is accepted. If not, it’s rejected. Once the order is accepted, it can be
dispatched
#### REST API
Order Service will expose a REST API to retrieve existing book orders and submit new ones. Each order can be
related to one book only, and up to five copies. 
| Endpoint	      | Method   | Req. body    | Status | Resp. body     | Description    		   	                               |
|:---------------:|:--------:|:------------:|:------:|:--------------:|:-------------------------------------------------------|
| `/orders`       | `POST`   | OrderRequest | 200    | Order          |Submits a new order for a given book in a given quantity| 
| `/orders`       | `GET`    |              |200     | Order[]        |Retrieves all the orders                               | 

### Edge Service
In Edge Service, we’ll use Redis as the data service backing the request rate limiter implementation provided by Spring Cloud Gateway.

### Dispatcher Service
Whenever an order is accepted, Dispatcher
Service should be responsible for packing and labeling the order, and for notifying
interested parties (in this case, the Order Service) once the order has been dis-
patched. For simplicity, let’s assume that both the pack and label actions are performed
by the application itself

The two actions to be performed as part of dispatching an order could be repre-
sented as functions:

* The pack function takes the identifier of an accepted order as input, packs the
order (in the example, the processing is represented by a log message), and
returns the order identifier as output, ready to be labeled.
* The label function takes the identifier of a packed order as input, labels the
order (in the example, the processing is represented by a log message), and
returns the order identifier as output, completing the dispatch.

![The business logic for Dispatcher Service is implemented as a composition of two functions: pack and label.](docs/business-logic-for-dispatcher-service.png)

the pack function. The input of the function should provide the
identifier of an order that has previously been accepted. We can model this data via a
simple DTO.

The output of the function can be the simple identifier of the packed order repre-
sented as a Long object.

### Configuration

For the Polar Bookshop system, we’ll use ConfigMaps and Secrets instead of the
Config Service to configure applications in Kubernetes environments. Still, all the
work we’ve done so far on Config Service would make including it in the overall
deployment of Polar Bookshop on Kubernetes straightforward. 

#### Refreshing configuration at runtime
For Polar Bookshop, we’ll use the Rolling restart(Changing a ConfigMap or a Secret can be followed by a rolling
restart of all the Pods affected, making the applications reload all the configuration data. With this option, Kubernetes Pods would remain immutable.
) and rely on Kustomize to trigger a
restart of the applications whenever a new change is applied to a ConfigMap or a
Secret.
### Messaging
In the Polar Bookshop system, we need to implement an event-driven solution to allow
different applications to communicate with each other asynchronously while reducing
their coupling. These are the requirements:
* When an order is accepted:
    * Order Service should notify interested consumers of the event.
    * Dispatcher Service should execute some logic to dispatch the order.
* When an order is dispatched:
    * Dispatcher Service should notify consumers interested in such an event.
    * Order Service should update the order status in the database.

The requirements don’t specify which
applications Order Service should notify upon order creation. In our example, only
the new Dispatcher Service application will be interested in those events. Still, more
applications might subscribe to the order creation events in the future. The beauty of
this design is that you can evolve a software system and add more applications without
affecting the existing ones at all. For example, you could add a Mail Service that sends
an email to users whenever an order they made has been accepted, and Order Service
wouldn’t even be aware of it.

![Order Service and Dispatcher Service communicate asynchronously and indirectly by producing and consuming events that are collected and distributed by an event broker (RabbitMQ)](docs/order-flow.png)

RabbitMQ will be the event-
processing platform responsible for collecting, routing, and distributing messages to
consumers.

![In the Polar Bookshop system, Order Service and Dispatcher Service communicate  asynchronously based on events distributed by RabbitMQ](docs/event-driven-part-of-Polar-Bookshop-system.png)

### Security
Polar Bookshop has two types of users: customers and employees.

* Customers can browse books and purchase them.
* Employees can also add new books to the catalog, modify the existing ones, and delete them.

In Polar Bookshop, Edge Service will initiate the user login flow, but then it will delegate the actual authentication step to Keycloak via the OIDC protocol (supported out
of the box by Spring Security).

In the case of Polar Bookshop, Edge Service can delegate the authentication step to
Keycloak. After authenticating the user successfully, Keycloak will send a JWT to Edge
Service with information about the newly authenticated user (ID Token). Edge Ser-
vice will validate the JWT through its signature and inspect it to retrieve data (claims)
about the user. Finally, it will establish an authenticated session with the user’s browser
based on a session cookie, whose identifier is mapped to the JWT.

For the Catalog Service application, we want to enforce the following security policies:

* GET requests to fetch books should be allowed without authentication.
* All other requests should require authentication.
* The application should be configured as an OAuth2 Resource Server and use
JWT authentication.
* The flow for handling JWT authentication should be stateless.

The Catalog Service endpoints should be protected according to the following policies:

* All GET requests sent to the /, /books or /books/{isbn} endpoints should be
allowed, even without authentication.
* Any other request should require both user authentication and the employee role.

For the Order Service application, we want to enforce the following security policies:
* All requests should require authentication.
* The application should be configured as an OAuth2 Resource Server and use
JWT authentication.
* The flow for handling JWT authentication should be stateless.

Besides data auditing, Order Service has one additional critical requirement. Users
should be able to access only their own orders. We need to ensure the privacy of all
that data.ensuring that order data is only
accessed by the users who created the orders. No user should be able to see the orders
from another person.

### Logging
For the Polar Bookshop system, we’ll use Fluent Bit, Fluent Bit will collect logs from all running containers and forward them to Loki,
which will store them and make them searchable. Finally, Grafana will use Loki as a data source and provide log visualization features. 
![Logging architecture for cloud native applications based on the Grafana stack](docs/Logging-architecture-using-the-Grafana-stack.png)

Fluent Bit can be configured to collect logs from different sources. For Polar Book-
shop we’ll rely on the Fluentd driver available in Docker to collect logs automatically
from running containers. The Docker platform itself listens to the log events from
each container and routes them to the specified service. In Docker, a logging driver
can be configured directly on a container. For example, update the Catalog Service
configuration in Docker Compose to use the Fluentd logging driver, which will send
the logs over to the Fluent Bit container.
### Health
for protecting the Spring Boot Actuator endpoints. For simplic-
ity, in the Polar Bookshop system, we’ll keep the Actuator endpoints unauthenticated
from inside the Kubernetes cluster and block any access to them from the outside.

### Managing traces with Tempo and Grafana
we’ll use OpenTelemetry (also called OTel ), a CNCF-incubating project that is quickly
becoming the de facto standard for distributed tracing and aims at unifying the collec-
tion of telemetry data (https://opentelemetry.io).

Next we will use OpenTelemetry directly (with the Open-Telemetry Java instrumentation) instead of relying on a façade that instruments the code in a
vendor-neutral way

the distributed tracing backend of choice is Tempo

We will complete the Grafana observability setup with
Tempo and use it to collect and store traces. Then We’ll use the OpenTelemetry Java instrumentation in Spring Boot applications to generate and send
traces to Tempo. Finally, we’ll query traces from Grafana.

For simplicity, we’ll enable OpenTelemetry only when running applications in containers and rely on environment variables to configure it.

### SPA
we’ll add an Angular application that will
be the frontend of the system. It will be served by an NGINX container and be accessi-ble via the gateway provided by Edge Service. 

Like the other applications in the Polar Bookshop system, we don’t want the Angular
application to be accessible directly from the outside. Instead, we want to make it
accessible via the gateway provided by Edge Service. We can do that by adding a new
route for Spring Cloud Gateway to forward any requests for static resources to the
Polar UI application.
### Deployment
#### Kubernetes
After collecting the multiple manifests needed to deploy an application, we are faced with additional challenges. How can we change the values in a ConfigMap depending on the environment? How can we change the container image version? What about Secrets and volumes? Is it possible to update the health probe’s configuration? Many tools have been introduced in the last few years to improve how we configure and deploy workloads in Kubernetes. 

For the Polar Bookshop system, we would like a tool that lets us handle multiple Kubernetes manifests as a single entity and
customize parts of the configuration depending on the environment where the application is deployed

For the Polar Bookshop system, we’ll use the k8s folder in each application project as
a base and define overlays in the polar-deployment repository

## Versioning release candidates for continuous delivery
For Polar Bookshop, we’ll adopt a simple solution and use the Git commit hash
directly to identify our release candidates. Therefore, we’ll ignore the version number
configured in the Gradle project (which could instead be used as the display version
name). For example, a release candidate for Catalog Service would be ghcr.io/<your_github_username>/catalog-service:<commit-hash>.
## Run
Add GitHub Container Registry

docker login ghcr.io

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
### Postgres
docker run -d \
 --name polar-postgres \
 -e POSTGRES_USER=user \
 -e POSTGRES_PASSWORD=password \
 -e POSTGRES_DB=polardb_catalog \
 -p 5432:5432 postgres

docker stop polar-postgres
docker start polar-postgres
docker rm -fv polar-postgres
#### Database Commands

Start an interactive PSQL console:

```bash
docker exec -it polar-postgres psql -U user -d polardb_catalog
```

| PSQL Command	              | Description                                    |
|:---------------------------|:-----------------------------------------------|
| `\list`                    | List all databases.                            |
| `\connect polardb_catalog` | Connect to specific database.                  |
| `\dt`                      | List all tables.                               |
| `\d book`                  | Show the `book` table schema.                  |
| `\quit`                    | Quit interactive psql console.                 |

From within the PSQL console, you can also fetch all the data stored in the `book` table.

```bash
select * from book;
```

## Development
### Dockarize
#### Using Dockerfile
cd catalog-service
mvn clean package spring-boot:repackage
docker build -t catalog-service .

#### Using Buildpacks

```console

./mvnw package
./mvnw spring-boot:build-image -DskipTests

./mvnw -pl config-service spring-boot:build-image -DskipTests

./mvnw -pl edge-service spring-boot:build-image -DskipTests

./mvnw -pl catalog-service spring-boot:build-image -DskipTests

./mvnw -pl order-service spring-boot:build-image -DskipTests

./mvnw -pl dispatcher-service spring-boot:build-image -DskipTests

./gradlew bootBuildImage -DskipTests

```

SPA

cd polar-ui/dist

./gradlew buildAngular

```shell
pack build polar-ui \
  --buildpack paketo-buildpacks/web-servers \
  --builder paketobuildpacks/builder-jammy-base \
  --env BP_WEB_SERVER_ROOT=public
```
### Publish
 ./mvnw spring-boot:build-image \
 -DimageName=ghcr.io/galkzaz/catalog-service \
 -Dspring-boot.build-image.publish=true \
 -Ddocker.publishRegistry.url=ghcr.io \
 -Ddocker.publishRegistry.username=galkzaz \
 -Ddocker.publishRegistry.password=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
### Run services locally
cd polar-deployment/docker

docker-compose up -d

if you want to test specific service like edge-service
 
docker-compose up -d polar-postgres polar-redis polar-rabbitmq polar-keycloak config-service edge-service catalog-service order-service

./mvnw -pl config-service spring-boot:run

./mvnw -pl edge-service spring-boot:run

./mvnw -pl catalog-service spring-boot:run

./mvnw -pl order-service spring-boot:run

./mvnw -pl dispatcher-service spring-boot:run

RabbitMQ management console http:/ /localhost:15672
#### Test OAuth2 with keycloak
cd polar-deployment/docker

docker-compose up -d polar-redis polar-keycloak

./mvnw -pl edge-service spring-boot:run

open a browser window,
and head to http://localhost:9000/user. You should be redirected to a login page served
by Keycloak, where you can authenticate as one of the users we created previously
#### Run SPA
docker-compose up -d polar-ui polar-redis polar-keycloak

./mvnw -pl edge-service spring-boot:run

open an incognito browser window and navigate to http://localhost:9000
 
use  (bjorn/password) or (Isabelle/password) for testing
### Run using Docker compose

echo "127.0.0.1 host.docker.internal" | sudo tee -a /etc/hosts

cd polar-deployment/docker

docker compose up -d

docker-compose down
#### Test Logging
./mvnw -pl catalog-service spring-boot:run -Dspring-boot.run.profiles=testdata

docker-compose up -d catalog-service grafana

http :9001/books

open a browser window, head to Grafana (http://localhost:3000), and use the
credentials configured in Docker Compose to log in (user/password) . Then select
the Explore page from the left menu, choose Loki as the data source, choose Last 1
Hour from the time drop-down menu, and run the following query to search for all
the logs produced by the catalog-service container:

{container_name="/catalog-service"}
#### Test Health
cd polar-deployment/docker

docker-compose up -d config-service polar-postgres polar-keycloak

./mvnw -pl catalog-service spring-boot:run -Dspring-boot.run.profiles=testdata

http :9001/actuator/health

http :9001/actuator/health/liveness

http :9001/actuator/health/readiness
##### Test Monitoring

cd polar-deployment/docker

docker-compose up -d polar-keycloak polar-postgres

./mvnw -pl catalog-service spring-boot:run

http :9001/actuator/metrics

http :9001/actuator/prometheus

**Testing grafana**

cd polar-deployment/docker

docker-compose up -d polar-keycloak polar-postgres

docker-compose up -d grafana

docker-compose up -d catalog-service

send a few requests to Catalog Service (http :9001/books),

ab -n 30 -c 10 -m GET http://localhost:9001/books

and then open a browser window and go to Grafana at http://localhost:3000 (user/password). In the Explore
section, you can query metrics like you browsed logs. Choose Prometheus as the data
source, select Last 5 Minutes from the time drop-down menu, and query the metrics
related to the JVM memory used by the application as follows

jvm_memory_used_bytes{application="catalog-service"}
##### Test Tracing
cd polar-deployment/docker

docker-compose up -d catalog-service

docker-compose up -d edge-service

http :9001/books

http :9000/books

docker logs edge-service

docker logs catalog-service

http://localhost:3000

Open a browser window, go to Grafana (http:/ /localhost:3000), and log in with
the credentials configured in Docker Compose (user/password). On the Explore
page, check the logs for Catalog Service ({container_name="/catalog-service"}),
much like we did earlier. Next, click on the most recent log message to get more
details. You’ll see a Tempo button next to the trace identifier associated with that log
message. If you click that, Grafana redirects you to the related trace using data from
Tempo, all in the same view
### Run using Kubernetes
On linux
echo "$(minikube ip --profile polar) polar-keycloak" | sudo tee -a /etc/hosts

On Windows you must map the polar-keycloak hostname to 127.0.0.1 in the
hosts file. Open a PowerShell window as an administrator, and run the following
command:

Add-Content C:\Windows\System32\drivers\etc\hosts "127.0.0.1 polar-keycloak"

#### Using kubectl
minikube start --cpus 2 --memory 4g --driver docker --profile polar


enable the ingress add-on, which will make sure that Ingress NGINX is deployed to our local cluster

minikube addons enable ingress --profile polar

get information about the different components deployed with Ingress NGINX as follows:

kubectl get all -n ingress-nginx

kubectl get nodes
minikube profile list

cd polar-deployment/kubernetes/platform/development

kubectl apply -f services

minikube image load edge-service --profile polar
minikube image load config-service --profile polar
minikube image load catalog-service --profile polar
minikube image load order-service --profile polar

cd catalog-service/

kubectl apply -f k8s/deployment.yml
kubectl apply -f k8s/service.yml

clean up your cluster by removing all the resources you have
created so far. 

First, open a Terminal window, navigate to the catalog-service folder
where you defined the Kubernetes manifests, and delete all the objects created for
Catalog Service:

kubectl delete -f k8s
Finally, go to your polar-deployment repository, navigate to the kubernetes/plat-
form/development folder, and delete the PostgreSQL installation: 

kubectl delete -f services
#### Using Tilt
##### manually
minikube start --profile polar

minikube addons enable ingress --profile polar

make sure you have a PostgreSQL instance up and running in your local Kubernetes cluster. 
cd polar-deployment/kubernetes/platform/development

kubectl apply -f services

##### using script
cd polar-deployment/kubernetes

chmod +x create-cluster.sh

./create-cluster.sh

##### Setting up the Grafana observability stack on Kubernetes
cd polar-deployment/kubernetes

chmod +x deploy-grafana-observability-stack.sh

./deploy-grafana-observability-stack.sh

Then 

cd polar-deployment/kubernetes/applications/development
tilt up

open http://localhost:10350
##### verify
you can verify
that all the backing services are ready and available with the following command:

kubectl get deploy

verify that the application is working correctly:

http :9000/books

Using ingress

on linux first 
minikube ip --profile polar

then use this IP to call system
http 192.168.49.2/books

keycloak: http://polar-keycloak/admin/

To stop the Tilt process in the Catalog Service project and run the following command to undeploy the application: 

navigate to the kubernetes/platform/development folder, and delete
the PostgreSQL installation with 

kubectl delete -f services

Finally, stop the cluster as follows:

minikube stop --profile polar

### Visualizing your Kubernetes workloads 

minikube --profile polar dashboard

=== Query DB
kubectl exec -it polar-postgres-d6474bb84-f8kks  -- psql  -U user -d polardb_catalog
### stop and delete the local Kubernetes cluster 
minikube stop --profile polar

minikube delete --profile polar

or using script
cd polar-deployment/kubernetes

chmod +x destroy-cluster.sh

./destroy-cluster.sh
## Deployment
## static code analysis
### vulnerability scanner
curl -sSfL https://raw.githubusercontent.com/anchore/grype/main/install.sh | sudo sh -s -- -b /usr/local/bin

./mvnw install -DskipTests

grype .

grype catalog-service


## Tests
mvn  test -pl catalog-service  -Dit.test=BookRepositoryJdbcTests
mvn  test -pl catalog-service  -Dtest=CatalogServiceApplicationTests

**Using testdata profile**

mvn spring-boot:run -pl catalog-service -Dspring-boot.run.profiles=testdata

## Deploy
## Kubernetes
### using Kustomize
**testing one service** 

```cd polar-deployment/kubernetes```

``./create-cluster.sh``

package the application as a container image and load it into the cluster

```./mvnw -pl catalog-service spring-boot:build-image -DskipTests```

```minikube image load catalog-service --profile polar```

apply Kustomization

```cd catalog-service```

```kubectl apply -k k8s```

verify when Catalog Service is available and ready to accept requests with this command:

```kubectl get deploy -l app=catalog-service```

verify that the ConfigMap has been created correctly with this command:

```kubectl get cm -l app=catalog-service```

forward traffic from your local machine to the Kubernetes cluster by running the following command:

```kubectl port-forward service/catalog-service 9001:80```

verify that the polar.greeting value specified in the ConfigMap is used instead of the default one:

```http :9001/```

to test automatic refresh of application.yml

update the value for the polar.greeting property
in the application.yml file used by Kustomize to generate the ConfigMap.
[source,yml,attributes]
----
polar:
  greeting: Welcome to the book catalog from a development Kubernetes environment! using Kustomize
----

Then apply the Kustomization again (kubectl apply -k k8s).

stop the port-forwarding process (Ctrl-C) and undeploy Catalog Service (kubectl delete -k k8s)

### Managing Kubernetes configuration for multiple environments with Kustomize
**testing one service** 

```cd polar-deployment/kubernetes```

``./create-cluster.sh``

apply Kustomization

```cd polar-deployment/kubernetes/applications/catalog-service/staging```

```kubectl apply -k .```

verify when Catalog Service is available and ready to accept requests with this command:

```kubectl get deploy -l app=catalog-service```

it will list 2 replicas

verify that the ConfigMap has been created correctly with this command:

```kubectl get cm -l app=catalog-service```

forward traffic from your local machine to the Kubernetes cluster by running the following command:

```kubectl port-forward service/catalog-service 9001:80```

verify that the polar.greeting value specified in the ConfigMap is used instead of the default one:

```http :9001/```

you can also try to update the application-staging.yml file, apply the Kustomization to the cluster again (kubectl apply -k .), and see how the Catalog Service Pods are restarted one after the other (rolling restarts) to load the new ConfigMap with zero downtime. 

stop the port-forwarding process (Ctrl-C) and undeploy Catalog Service (kubectl delete -k .) or  delete the cluster with ./destroy-cluster.sh
###  vulnerability scanner
cd catalog-service/
mvn install
grype .
grype catalog-service
## Test
### Local 
#### Test Config Server
http :8888/catalog-service/default

http :8888/catalog-service/prod
 
#### test Catalog service
*Books*

http :9001/

http :9001/books

http POST :9001/books author="Lyra Silverstar" title="Northern Lights" isbn="1234567891" price=9.90

http :9001/books/1234567891

http POST :9001/books author="Jon Snow" title="" isbn="123ABC456Z" price=9.90

#### test orders service
http POST :9002/orders isbn=1234567891 quantity=3
#### test order Messaging using RabbitMQ
add a new book to the catalog:

http POST :9001/books author="Jon Snow" \
 title="All I don't know about the Arctic" isbn="1234567897" \
 price=9.90 publisher="Polarsophia"

order three copies of that book:

http POST :9002/orders isbn=1234567897 quantity=3

Fetch the order from Order Service:

http :9002/orders

The status should be DISPATCHED:

#### test using Gateway
http :9000/books
http :9000/orders

#### testing gateway circuit breakers 
docker compose stop catalog-service

docker compose stop order-service

./mvnw -pl edge-service clean spring-boot:run

run 21 sequential POST requests (-n 21 -c 1 -m POST) to the /orders endpoint(POST
requests have no retry configuration, and order-route has no fallback, so the result
will only be affected by the timeout and circuit breaker)

ab -n 21 -c 1 -m POST http://localhost:9000/orders

From the ab output, you can see that all the requests returned an error:

Complete requests: 21

Non-2xx responses: 21

The circuit breaker is configured to trip to the open state when at least 50% of the
calls in a 20-sized time window fails. Since you have just started the application, the cir-
cuit will transition to the open state after 20 requests. In the application logs, you can
analyze how the requests have been handled. All the requests failed, so the circuit
breaker registers an ERROR event for each of them:

Event ERROR published: CircuitBreaker 'orderCircuitBreaker'
 recorded an error.

At the 20th request, a FAILURE_RATE_EXCEEDED event is recorded because it exceeded
the failure threshold. That will result in a STATE_TRANSITION event that will open
the circuit:
Event ERROR published: CircuitBreaker 'orderCircuitBreaker'
 recorded an error.
Event FAILURE_RATE_EXCEEDED published: CircuitBreaker 'orderCircuitBreaker'
 exceeded failure rate threshold.
Event STATE_TRANSITION published: CircuitBreaker 'orderCircuitBreaker'
 changed state from CLOSED to OPEN

 The 21st request will not even try contacting Order Service: the circuit is open, so it
cannot go through. A NOT_PERMITTED event is registered to signal why the request
failed:
Event NOT_PERMITTED published: CircuitBreaker 'orderCircuitBreaker'
 recorded a call which was not permitted.

call a GET endpoint for which both retries and fallback have been configured.

rerun the edge application so you can start with a clear circuit breaker state 

ab -n 21 -c 1 -m GET http://localhost:9000/books

If you check the application logs, you’ll see how the circuit breaker behaves precisely
like before: 20 allowed requests (closed circuit), followed by a non-permitted request
(open circuit). However, the result of the previous command shows 21 requests com-
pleted with no errors:
Complete requests: 21
Failed requests: 0
This time, all requests have been forwarded to the fallback endpoint, so the client
didn’t experience any errors.
#### testing gateway  rate-limiting.
http :9000/books

The HTTP headers of the response shows the rate limiter’s configuration and the number of remaining requests allowed within the time window (1 second):
    HTTP/1.1 200 OK
    Content-Type: application/json
    Date: Wed, 18 Jun 2025 15:22:51 GMT
    X-RateLimit-Burst-Capacity: 20
    X-RateLimit-Remaining: 19
    X-RateLimit-Replenish-Rate: 10
    X-RateLimit-Requested-Tokens: 1

ab -n 25 -c 25 -m GET http://localhost:9000/books
### Local Kubernetes

### Test Config Server
http :8888/catalog-service/default

http :8888/catalog-service/prod
 
### test Catalog service
http :9001/

http :9001/books

http POST :9001/books author="Lyra Silverstar" title="Northern Lights" isbn="1234567891" price=9.90

http :9001/books/1234567891

http POST :9001/books author="Jon Snow" title="" isbn="123ABC456Z" price=9.90

### test Order service
http POST :9001/books author="Jon Snow" \
 title="All I don't know about the Arctic" isbn="1234567897" \
 price=9.90 publisher="Polarsophia"

http POST :9002/orders isbn=1234567897 quantity=3

http :9002/orders

#### test order Messaging using RabbitMQ
add a new book to the catalog:

http POST :9000/books author="Jon Snow" \
 title="All I don't know about the Arctic" isbn="1234567897" \
 price=9.90 publisher="Polarsophia"

order three copies of that book:

http POST :9000/orders isbn=1234567897 quantity=3

Fetch the order from Order Service:

http :9000/orders

The status should be DISPATCHED:
#### test Kubernetes ConfigMaps 

**test catalog-service**

```cd polar-deployment/kubernetes```

``./create-cluster.sh``

package the application as a container image and load it into the cluster

```./mvnw -pl catalog-service spring-boot:build-image -DskipTests```

```minikube image load catalog-service --profile polar```

apply ConfigMap

```cd catalog-service```

```kubectl apply -f k8s/configmap.yml```

verify that the ConfigMap has been created correctly with this command:

```kubectl get cm -l app=catalog-service```

deploy the application in the local cluster by applying the Deployment and Service manifests:

```kubectl apply -f k8s/deployment.yml -f k8s/service.yml```

verify when Catalog Service is available and ready to accept requests with this command:

```kubectl get deploy -l app=catalog-service```

forward traffic from your local machine to the Kubernetes cluster by running the following command:

```kubectl port-forward service/catalog-service 9001:80```

verify that the polar.greeting value specified in the ConfigMap is used instead of the default one:

```http :9001/```

```http :9001/books```

run the following command, but keep the cluster running, since we’re going to use it again soon:

```kubectl delete -f k8s```
