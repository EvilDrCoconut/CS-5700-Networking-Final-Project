Run Instructions:
Assuming Java 8 and Unix operating system
To compile, from 1 directory, move to src:
cd src/
then run:
javac */*.java
The run
java webServer.MultithreadedServer -ip 83.43.110.23 -port 2000
Port should match client port, which defaults to 2000 unless provided a different one by DNS
This will allow .com connections, act as a walmart or lowes server
23.233.120.82 would allow .edu connections, northeastern server
43.83.130.43 would allow .org connections, act as wikipedia, geeksforgeeks, pbs server.
Important part for distinguishing is the xxx.xxx.110/120/130.xxx
java Client.Client www.geeksforgeeks.org/geeksforgeeks/GeeksforGeeks_Home.html header=language:e:size:70000  save=C:\Programs\temp
arguments are header= and language or size. Any breaks are : distinguished. IE header=size:8000. header=language:f. Header=size:2888:language:s.
and save=. This is a path to save the web files. Must already exist.
Valid webpages are provided in ReachableURLs with language.
java DNS.DNSReceiver
WIP TODO
