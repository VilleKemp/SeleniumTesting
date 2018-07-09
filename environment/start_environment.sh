##This should ideally set up the whole thing. 
#Remember to change setup_browsermob and setup_etracker so they use proper paths

#Jenkins is at port 8585


for d in start/*.sh; do
    #echo "$d"
    x-terminal-emulator -e sh $d &
done






 



