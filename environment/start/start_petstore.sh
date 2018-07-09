#start petstore at localhost:8080 because the image is dumb
#Give it a name so it is easier to remove
sudo docker run -d --name petstore -e SWAGGER_BASE_PATH=/v2 -p 8080:8080 swaggerapi/petstore
