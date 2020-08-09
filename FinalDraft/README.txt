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

For some good client examples, you could pass (all starting with java Client.Client)
- www.pbs.org/pbs/NHPBS_TV_Schedule.html header=language:e
- www.pbs.org/pbs/PBS_Public_Broadcasting_Service.html header=language:e save=C:\Programs\temp
- www.wikipedia.org/wikipedia/Web_server_Wikipedia.html header=language:s
- www.geeksforgeeks.org/geeksforgeeks/GeeksforGeeks_Home.html header=language:e:size:70000 save=C:\internet (You have to create internet directory on windows machine)
- www.geeksforgeeks.org/geeksforgeeks/GeeksforGeeks_Home.html
- www.wikipedia.org/wikipedia/Wikipedia.html
- www.northeastern.edu/northeastern/Northeastern_University_Home.html
- www.lowes.com/lowes/Lowes_Home_Improvement.html header=language:s
- www.walmart.com/walmart/Walmart.html header=language:f
- www.walmart.com/walmart/Walmart.html header=language:s:size:300000

All ReachableURLs are valid though.

If the DNS was fully operational, you would also start
java DNS.DNSReceiver
and
java DNS.ExampleClient

These servers would go through DNS hierarchy and call appropriate servers, caching, and getting definitive info.
However, currently this is not fully operational. The code reflects how it should operate, but
it is not fully operational due to issues with the recursive hierarchy calls.

