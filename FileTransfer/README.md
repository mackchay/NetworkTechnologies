# How to use
This is a "FileTransfer". Program can send files or receive them.
Module Server is the part which receiving files. Module Client is the part which sending files.
Open out/artifacts and run jar file of client or server.
## Start
Client gets 4 options:
* -f - Path to required file. 
* -i - Server IP address (IPv4 or IPv6).
* -p - Server listen this port. 
* -s - Sending speed in MB/s.
## Example
Client
> java -jar FileTransfer.Client.main.jar -i 239.0.0.1 -p 1234 -f file.mp3 -s 5

Server
> java -jar FileTransfer.Server.main.jar -p 1234

## Format
Message format is UTF-8
=======