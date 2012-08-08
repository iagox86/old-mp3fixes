#!/bin/sh

for i in *; 
do 
  #track=`echo "$i" | cut -d\- -f1`
  #artist=`echo "$i" | cut -d\- -f3`
  #artist=`echo "$artist" | sed "s/_/ /g"`
  #artist=`echo "$artist.mp3"`

  track=`echo "$i" | cut -d\. -f1`
  artist=`echo "$i" | cut -d\  -f8- | sed 's/\[//g' | sed 's/\]//g'`

  echo "$i --> $track-$artist"
  mv "$i" "$track-$artist"

done

