# Private-Edit-Distance-Approximation
Link to the paper: http://www.cs.umd.edu/~wangxiao/papers/ped.pdf

## Compilation and Setup:

1.Get the code

`git clone git@github.com:SPQ-EditDistance/code.git`

`cd code`

`git submodule init && git submodule update`

2.Compile and preprocess data

`./compile.sh`

`mkdir processed`

`java -cp bin/ offline.DataPreprocessor listOfGenome processed/`

## Run the protocols
3.Run Protocols

`./run.py gen ptcl1  & ./run.py eva ptcl1`

or

`./run.py gen ptcl2  & ./run.py eva ptcl2`

4.Try to change parameters in Conf.conf and rerun

More data can be obtained from PGP project: http://www.personalgenomes.org/
Certains genome files has slightly different format and may need slightly changes on preprocessing
code.

To run over internet, update the address in Conf.conf to generator's ip address. Make sure each party has their own files.


### Question about the code?

please email me at wangxiao (aT) cs (Dot) umd (dOt) edu
