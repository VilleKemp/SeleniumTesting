# SeleniumTesting
Setup:
:shipit:
Get java and eclipse  
export this project  
Required jars are in the libs folder  

Done with java 10.0.1

Install mutillidae 2 docker image
```
docker pull bltsec/mutillidae-docker 
```
```
docker run -d -p 80:80 -p 443:443 --name owasp17 bltsec/mutillidae-docker
```
```
firefox http://localhost:80/mutillidae
```

* remake the database  
Start testing!  

# Using browsermob
Download and run browsermob-proxy binary  
* https://github.com/lightbody/browsermob-proxy/releases  
* Allow executing bin/browsermob-proxy as executable  
* Execute ./browsermob-proxy  

Create proxy and start har collecting using API with curl  
```
curl -X POST http://localhost:8080/proxy {"port":8081}
```
```
curl -X PUT http://localhost:8080/proxy/8081/har   {"captureContent":"true","captureHeaders":"true","captureCookies":"true"}  
```
Look at HAR file using browser with address http://localhost:8080/proxy/8081/har  

Set value proxyInUse in basictest.java to true  
