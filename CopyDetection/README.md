This is a Copy Detection App. Program can send multicast messages or receive them and generate list of other copies.
Module MulticastDiscoveryApp is realization of task. Module MulticastDiscoveryStart starts application.
Program gets 2 arguements: Multicast IP address (IPv4 or IPv6) and mode: "--mode=send" or "--mode=receive"
Receiver has 10 seconds timeout and exited automatically.
