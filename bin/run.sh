#!/bin/sh

WORKDIR=.

BUILDDIR=$WORKDIR/build
LIBDIR=$WORKDIR/lib
SRCDIR=$WORKDIR/src

ALL_LIB_JARS=
for jarfile in $LIBDIR/*.jar
do
	ALL_LIB_JARS=$ALL_LIB_JARS:$jarfile
done




echo ">>--> Begin: $@"

case $1 in

    compile)	
	javac -d  $BUILDDIR -classpath "$BUILDDIR:$ALL_LIB_JARS" \
	      $SRCDIR/*/*/*.java       
       
	;;

    
    class)
	java -Xmx1024m -classpath "$BUILDDIR:$ALL_LIB_JARS" $2 $3 $4 $5 $6 $7 $8 $9
	;;

    
    *)
        echo "!!! Unkown options \"$@\" !!!"
        ;;

esac
echo ">>--> End: $@"
    
