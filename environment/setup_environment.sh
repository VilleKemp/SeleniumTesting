#This script is supposed to do all the setups required after the docker instances and such have started
for s in setup/*.sh; do
    #echo "$d"
    x-terminal-emulator -e sh $s &
done

#open mutillidae so you can restart the database
firefox http://localhost:81/mutillidae 
