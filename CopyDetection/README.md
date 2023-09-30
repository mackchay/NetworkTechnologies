This is a "CopyDetectionApp". Program can send multicast messages or receive them and generate list of other copies.  

Module MulticastDiscoveryApp is realization of task. Module MulticastDiscoveryStart starts application.  

Program gets 2 arguements: Multicast IP address (IPv4 or IPv6) and mode: "--mode=send" or "--mode=receive"  
Example: "239.0.0.1 --mode=send"

Receiver has 10 seconds timeout and exited automatically.  
