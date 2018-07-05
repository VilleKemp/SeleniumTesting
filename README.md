# SeleniumTesting
Setup:  
Get java and eclipse  
export this project  
Required jars are in the libs folder  

Done with java 10.0.1  

Install mutillidae 2 docker image  
```
docker pull bltsec/mutillidae-docker 
```
```
docker run -d -p 81:80 -p 443:443 --name owasp17 bltsec/mutillidae-docke
```
```
firefox http://localhost:80/mutillidae
```
* remake the database  
Start testing!  
  
REST testing  
Note this API is kinda barebones and missing features. It requires python2 and atleas packages flask and flask-restful
```
git clone https://github.com/VilleKemp/ExerciseTracker.git && cd ExerciseTracker && git checkout WIP
python forum.py
```
Follow the README in branch WIP of the ExerciseTracker
# Using browsermob
Download and run browsermob-proxy binary  
* https://github.com/lightbody/browsermob-proxy/releases  
* Allow executing bin/browsermob-proxy as executable  
* Execute 
```./browsermob-proxy -p 8181 ``` 

Create proxy and start har collecting using API with curl  
```
curl -X POST -d "port=8081" http://localhost:8181/proxy
```
```
curl -X PUT -d "captureContent=true" -d "captureHeaders=true" -d "captureCookies=true" -d "captureBinaryContent=true" http://localhost:8181/proxy/8081/har
```
Look at HAR file using browser with address http://localhost:8080/proxy/8081/har  

Set value proxyInUse in basictest.java to true


