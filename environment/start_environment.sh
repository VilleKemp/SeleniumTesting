##This should ideally set up the whole thing. 
#If nothing else it contains the commands to do so

#Jenkins is at port 8585


for d in start/*.sh; do
    #echo "$d"
    x-terminal-emulator -e sh $d &
done




#open mutillidae so you can restart the database
#firefox http://localhost:81/mutillidae &

 



