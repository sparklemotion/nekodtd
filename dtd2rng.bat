@echo off
call dtd2dtdx %1 > temp.dtdx
call dtdx2flat temp.dtdx > temp.flat
call flat2rng temp.flat
del temp.dtdx temp.flat