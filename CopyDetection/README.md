# How to use
This is a "CopyDetectionApp". Program can send multicast messages or receive them and generate list of other copies' ip addresses.  
Module MulticastDiscoveryApp is realization of task. Module MulticastDiscoveryStart starts application.  
## Start
Program gets 4 options:
* -k - Security unique key-message. Receiver and sender should have the same key.
* -i - Multicast IP address (IPv4 or IPv6).
* -p - Multicast receiver listen this port. 
* -m - Program mode <recv\send>.
## Example
> -k hello -i 239.0.0.1 -p 1234 -m recv    
> -k message -i FF02::1 1234 -p 1234 -m send

Receiver has 10 seconds timeout and exited automatically. 
## Format
Message format is UTF-8
