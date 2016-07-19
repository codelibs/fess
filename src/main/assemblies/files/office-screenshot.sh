#!/bin/sh

url=$1
outputFile=$2

targetFile=`echo $url | sed -e "s#^file:/*#/#g"`
pdfFile="`echo $outputFile | sed -e "s/\.[^.]*$//g"`.pdf"

env
unoconv -o ${pdfFile} -f pdf ${targetFile} >> /tmp/screenshot_shell.log
convert -thumbnail 200x150! ${pdfFile} ${outputFile} >> /tmp/screenshot_shell.log
