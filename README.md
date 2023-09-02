# Reports microservice
**Reports microservice is a microservice that generate diabete risk report.
The microservice fetchs information from patient & notes microservice in order to generate it.**

## Technical Stack
Microservice is built with the followings technologies :
- Java 11 and Spring Framework 2.7.5
- Maven for the application lifecycle management

Thanks to the Spring Framework, it is possible to use dependencies such as *Feign*, that acts as an intermediary for microservices to communicate

# API Specifications
**Microservice can be accessed at `localhost:9091/report/`**.

## **GET `/report/{patientId}`**
This route will generate for you a report for a patient based on patient informations and his/her notes.

### **Successful response example**
**URL : `localhost:9091/api/report/1`**
```json
{
    "patientId": "1",
    "firstName": "BENALIA",
    "lastName": "Hamza",
    "gender": "Homme",
    "birthdate": "16/05/1995",
    "age": 28,
    "risk": "None",
    "triggers": [
        
    ]
}
```

### **503 Service Unavailable**
```json
{
    "time": "2022-11-16T10:02:40.400+00:00",
    "status": "SERVICE_UNAVAILABLE",
    "message": "Patient microservice is not started. Cannot generate report."
}
```

### **404 Not Found**
```json
{
    "time": "2022-11-16T10:03:41.144+00:00",
    "status": "NOT_FOUND",
    "message": "Patient with given ID was not found"
}
```
