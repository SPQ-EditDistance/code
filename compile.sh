mkdir -p bin
find . -name "*.java" > source.txt;
javac -cp ./FlexSC/lib/*:bin/ -d bin @source.txt;
rm source.txt
