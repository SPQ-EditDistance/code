# Private-Edit-Distance-Approximation


## Compilation and Setup:

1. Get the code

`git clone git@github.com:SPQ-EditDistance/code.git`

`cd code`

`git submodule init && git submodule update`

2. Compile and preprocess data

`./compile.sh`

`processed`

`java -cp bin/ offline.DataPreprocessor listOfGenome processed/`

3. Run Protocols

`./run.py gen ptcl1  & ./run.py eva ptcl1`

or 

`./run.py gen ptcl2  & ./run.py eva ptcl2`

4. Try to change parameters in Conf.conf and rerun
