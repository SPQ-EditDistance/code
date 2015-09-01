# Private-Edit-Distance-Approximation


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

In order to run over internet, update the address in Conf.conf to generator's ip address. Make sure each party has their own files.
