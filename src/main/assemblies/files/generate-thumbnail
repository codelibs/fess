#!/bin/sh

url=$1
outputFile=$2

targetFile=`echo $url | sed -e "s#^file:/*#/#g"`
pdfFile="`echo $outputFile | sed -e "s/\.[^.]*$//g"`.pdf"

unoconv -o ${pdfFile} -f pdf ${targetFile}
convert -thumbnail 200x150! ${pdfFile} ${outputFile}
