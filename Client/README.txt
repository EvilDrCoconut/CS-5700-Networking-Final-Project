I.	Run Instructions:
Assuming Java 8 and Unix operating system
To compile, from 1 directory, move to src:
cd src/
then run:
javac TransportProtocol/*.java
The run
For receiver:
java TransportProtocol.Receiver 12000 6 SkyeNygaardTest.txt
Where 12000 is any port, 6 is a window size [1,7], and SkyeNygaardTest.txt is the output file
For sender: 
java TransportProtocol.Sender localhost 12000 5 TransportProtocol/Sender.java
where localhost is the IP and 12000 is any port (but corresponding to the server), 5 is a window size [1,7], and TransportProtocol/Sender.java is an input file
Code is in src/TransportProtocol

In order to simulate the reliablity of the protocol, I introduced 30% random droppage for packets,
in each direction, and created a delay within the network of up to 200 ms. This is displayed out to the sender.

The sample I gave will transmit the Sender Class source code into src/SkyeNygaardTest.txt. Should have the same text after.

I represented the header data in the 4 bytes structure given in the book, 
so there is a solid amount of code for converting to and from binary, including combining type and window size into one byte.