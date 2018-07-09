#Due to docker this script will be run using sudo.
#to give Jenkins priviliges to do so execute the following
#visudo
#add jenkins    ALL = NOPASSWD: /bin/sh, /var/lib/jenkins/workspace/demo/environment/get_environment.sh to the file



#Selenium testing
git clone --depth=1 https://github.com/VilleKemp/SeleniumTesting.git
#etracker
git clone -b WIP https://github.com/VilleKemp/ExerciseTracker.git
#browsermob. not the best way. Find how to get is using github api
wget https://github.com/lightbody/browsermob-proxy/releases/download/browsermob-proxy-2.1.4/browsermob-proxy-2.1.4-bin.zip
unzip browsermob-proxy-2.1.4-bin.zip
rm browsermob-proxy-2.1.4-bin.zip
#docker pulls
docker pull swaggerapi/petstore
docker pull bltsec/mutillidae-docker
