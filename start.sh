CLASSES=`pwd`/out/production/classes
#CLASSES=`pwd`/build/classes/java/main
SCRIPT="cd $CLASSES; java -cp . cs455.scaling.client.Client carson-city 50321 100"
for ((j=1;j<=$1;j++));
do
    COMMAND='gnome-terminal'
    for i in `cat machine_list`
    do
        echo 'logging into '$i
        OPTION='--tab -e "ssh -t '$i' '$SCRIPT'"'
        COMMAND+=" $OPTION"
    done
    eval $COMMAND &
done