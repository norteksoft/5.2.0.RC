#!/bin/sh
cp=h2-1.1.119.jar
if [ -n "$H2DRIVERS" ] ; then
  cp="$cp:$H2DRIVERS"
fi
if [ -n "$CLASSPATH" ] ; then
  cp="$cp:$CLASSPATH"
fi
java -cp "$cp" org.h2.tools.Console %@

