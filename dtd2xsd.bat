@echo off
call dtd2dtdx %1 > temp.dtdx
call dtdx2flat temp.dtdx > temp.flat
call flat2xsd temp.flat
rem del temp.dtdx temp.flat