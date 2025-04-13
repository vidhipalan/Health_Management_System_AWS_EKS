# Health Management System on AWS EKS

This project is a full-stack clinical information system deployed on **AWS Elastic Kubernetes Service (EKS)** using **Docker**, **Kubernetes**, and **Amazon RDS (PostgreSQL)**. It demonstrates deployment and orchestration of microservices in a cloud-native architecture.

## 🧠 Technologies Used

- **AWS EKS** – Managed Kubernetes cluster
- **Amazon RDS (PostgreSQL)** – Relational database backend
- **Amazon ECR** – Private container registry for Docker images
- **Kubernetes** – Workload orchestration and service exposure
- **Docker** – Containerization of microservices
- **Java EE (Jakarta EE)** – Backend microservice and frontend layers
- **JAX-RS / REST** – Patient and provider services via RESTful APIs
- **JSF (Jakarta Faces)** – Web-based frontend interface
- **Payara Micro** – Lightweight application server for deployment
- **IntelliJ IDEA** – IDE for development and PostgreSQL database access

---

## 🧱 Project Structure

| Module           | Description                                |
|------------------|--------------------------------------------|
| `clinic-domain`  | Backend service managing patient/provider data |
| `clinic-rest`    | REST API layer connecting to domain module |
| `clinic-webapp`  | JSF-based frontend for user interaction    |
| `dockerfiles/`   | Dockerfiles for each microservice          |
| `yaml/`          | Kubernetes deployment and service files    |
| `logs/`          | Logs from deployed pods (for grading)      |


---

## 🚀 How It Works

1. **Docker Images Built Locally**  
   - Tagged and pushed to **Amazon ECR**

2. **PostgreSQL Schema**  
   - Created on **Amazon RDS** via pgAdmin or IntelliJ DB Tools

3. **Microservice Deployment**  
   - Applied Kubernetes YAML files to deploy 3 services:
     - `clinic-domain` (NodePort)
     - `clinic-rest` (LoadBalancer)
     - `clinic-webapp` (LoadBalancer)

4. **Client Interaction**
   - Used a JAR-based CLI to POST patients, providers, and treatments to `clinic-rest`
   - Verified data persistence via both:
     - REST responses
     - JSF web UI

5. **Monitoring**
   - Kubernetes health checks and logs captured
   - Verified service discovery and integration across all modules

---

## 🌐 Key Endpoints

| Service        | URL (Sample) |
|----------------|--------------|
| REST WADL      | `http://<clinic-rest-lb>:9090/api/application.wadl` |
| Web Frontend   | `http://<clinic-webapp-lb>:8080/clinic`             |
| Health Checks  | `/health/live`, `/health/ready` via NodePort or LoadBalancer |

---

## 📹 Demo Video

A full walkthrough of:
- IAM setup
- RDS configuration
- ECR image pushes
- Kubernetes deployments
- REST and UI integration

---

## 💡 Skills Demonstrated

- Deploying Java EE microservices on **cloud-native infrastructure**
- Managing **multi-service architecture** with Kubernetes
- Automating builds and deployments using Docker & ECR
- Connecting frontend and backend with REST and JSF
- Using **kubectl** for orchestration and troubleshooting
- Managing cloud database connectivity via **Amazon RDS**

---

## 🙋 Author

**Vidhi Ashok Palan**  
Graduate Student – Stevens Institute of Technology  

---
