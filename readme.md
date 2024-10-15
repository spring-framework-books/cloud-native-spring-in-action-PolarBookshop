# Polar Bookshop: A cloud native application
Imagine that you have been hired to build a Polar Bookshop application for Polar-
sophia. This organization manages a specialized bookshop and wants to sell its
books about the North Pole and the Arctic online. A cloud native approach is being
considered.

 As a pilot project, your boss has assigned you to demonstrate to your colleagues
how to go from implementation to production in the cloud. The web application you
are asked to build is Catalog Service, and for now it will only have one responsibility:
welcoming users to the book catalog. This pilot project will be the foundation for
actual products built as cloud native applications, should it be successful and well-
received.

 Considering the task’s goal, you might decide to implement the application as a
RESTful service with a single HTTP endpoint responsible for returning a welcome
message. Surprisingly enough, you choose to adopt Spring as the primary technology
stack for the one service (Catalog Service) composing the application. The architec-
ture of the system is shown in figure 2.2, and you’ll be trying your hand at building
and deploying the application in the upcoming sections.

![Architecture diagram for the Polar Bookshop application following the C4 model](docs/Architecture%20diagram%20for%20the%20Polar%20Bookshop%20application.png)

In above figure, you can see the notation that I’ll use to represent architectural diagrams
throughout the book, following the C4 model created by Simon Brown (https://
c4model.com). To describe the architecture for the Polar Bookshop project, I’m rely-
ing on three abstractions from the model:
* Person—This represents one of the human users of the software system. In our
example, it’s a customer of the bookshop.
* System—This represents the overall application you will build to deliver value to
its users. In our example, it’s the Polar Bookshop system.
* Container—This represents a service, either application or data. It’s not to be
confused with Docker. In our example, it’s the Catalog Service.

For this task we’ll use the Spring Framework and Spring Boot to do the following:
* Declare the dependencies needed to implement the application.
* Bootstrap the application with Spring Boot.
* Implement a controller to expose an HTTP endpoint for returning a welcome
message.
* Run and try the application.

All the examples in this book are based on Java 17, the latest long-term release of Java at the time of writing. 


## Deploy

### Local
mvn spring-boot:run -pl catalog-service

### Docker
mvn spring-boot:build-image -pl catalog-service

docker images catalog-service

docker run --rm --name catalog-service -p 8080:8080 catalog-service:1.0
#### Kubernetes
minikube start

minikube image load catalog-service:1.0

kubectl create deployment catalog-service --image=catalog-service:1.0

kubectl expose deployment catalog-service --name=catalog-service --port=8080 

kubectl port-forward service/catalog-service 8080:8080


**Stop and remove**
kubectl delete service catalog-service

kubectl delete deployment catalog-service

minikube stop

## Test

http://localhost:8080/