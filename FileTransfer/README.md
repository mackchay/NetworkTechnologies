# How to use
This is a "FileTransfer". Program can send files or receive them. Client sends files and server receives them. 
Module MulticastDiscoveryApp is realization of task. Module MulticastDiscoveryStart starts application.  
Open out/artifacts and run jar file of client or server.
## Start
Client gets 4 options:
* -f - Path to required file. 
* -i - Server IP address (IPv4 or IPv6).
* -p - Server listen this port. 
* -s - Sending speed in MB/s.
## Example
> -java -jar  hello -i 239.0.0.1 -p 1234 -m recv    
> -k message -i FF02::1 1234 -p 1234 -m send

Receiver has 10 seconds timeout and exited automatically. 
## Format
Message format is UTF-8
