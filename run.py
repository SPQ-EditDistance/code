#!/usr/bin/python
import sys, subprocess

party = {'gen': "java -cp FlexSC/lib/*:bin/ util.GenRunnable",
         'eva':"java -cp FlexSC/lib/*:bin/ util.EvaRunnable"}
program = {'ptcl1': "Protocol1 ",
           'ptcl2':"Protocol2 "}

cmd = party[sys.argv[1]]+" online."+program[sys.argv[2]]+" ".join(sys.argv[3:])
print "Command to run", cmd

subprocess.call(cmd, shell=True)
