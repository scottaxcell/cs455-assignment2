Scott Axcell (827239918)
CS455 Homework 2: Programming Component
03-11-2019

Description:
    Scalable Server Design: Using Thread Pools & Micro Batching To Manage And Load Balance Active Network Connections

Build:
  gradle build

Run:
    java cs455.scaling.server.Server portnum thread-pool-size batch-size batch-time
    java cs455.scaling.client.Client server-host server-port message-rate

Classes:
client
 - Client : main method responsible for creating the NioClient
 - HashCodes : BlockingQueue based container used for storing Hashcode strings
 - NioClient : responsible sending random bytes of data to the server
 - TransmissionStatistics : responsible for tracking transmission statistics for a client

server
 - NioServer : responsible for handling accept and read messages from clients via a ThreadPoolMgr
 - Server : main method responsible for creating the NioServer

threadpool
 - AcceptBatchTask : responsible for registering a client connection
 - BatchJob : Runnable that maintains a BlockingQueue of BatchTasks to complete
 - BatchJobMgr : responsible for maintaining the BlockingQueue of BatchJobs as spec'd
 - BatchTask : represents a task that is executed using a thread via the ThreadPool
 - ReadBatchTask : task that handles reading a client message, hashcode creation, and writing to a client
 - ThreadPool : fixed thread pool of Worker threads
 - ThreadPoolMgr : responsible for managing access to Worker resources in the ThreadPool
 - Worker : thread that runs a job (in this particular case a BatchJob)

util
 - ThroughputStatistics : responsible for tracking throughput statistics for a given client connection
 - ThroughputStatisticsMgr : responsible for tracking the total throughput statistics for the NioServer
 - Utils : miscellaneous utility methods (methods of interest include readBytesFromChannel and writeBytesToChannel)

 WC notes:
 server : 50321 10 10 5
 client : carson-city 50321 10

100 clients
10 msg/s
[20:29:06] Server Throughput: 986 messages/s, Active Client Connections: 99, Mean Per-client Throughput: 9.96 messages/s, Std. Dev. Of Per-client Throughput: 0.02 messages/s

100 clients
15 msg/s
[20:31:37] Server Throughput: 1505 messages/s, Active Client Connections: 100, Mean Per-client Throughput: 15.05 messages/s, Std. Dev. Of Per-client Throughput: 0.03 messages/s

100 clients
30 msg/s
[20:34:20] Server Throughput: 2966 messages/s, Active Client Connections: 99, Mean Per-client Throughput: 29.96 messages/s, Std. Dev. Of Per-client Throughput: 0.07 messages/s

200 clients
30 msg/s
[20:36:58] Server Throughput: 5815 messages/s, Active Client Connections: 194, Mean Per-client Throughput: 29.98 messages/s, Std. Dev. Of Per-client Throughput: 0.07 messages/s
[20:39:16] Server Throughput: 5904 messages/s, Active Client Connections: 197, Mean Per-client Throughput: 29.97 messages/s, Std. Dev. Of Per-client Throughput: 0.06 messages/s

300 clients
30 msg/s
[20:41:26] Server Throughput: 7406 messages/s, Active Client Connections: 247, Mean Per-client Throughput: 29.99 messages/s, Std. Dev. Of Per-client Throughput: 0.08 messages/s

100 clients
100 msg/s
[20:52:15] Server Throughput: 9720 messages/s, Active Client Connections: 100, Mean Per-client Throughput: 97.20 messages/s, Std. Dev. Of Per-client Throughput: 0.64 messages/s

200 clients
100 msg/s
[21:11:48] Server Throughput: 14919 messages/s, Active Client Connections: 199, Mean Per-client Throughput: 74.97 messages/s, Std. Dev. Of Per-client Throughput: 3.57 messages/s

300 clients
100 msg/s
22:40:16] Server Throughput: 14229 messages/s, Active Client Connections: 244, Mean Per-client Throughput: 58.32 messages/s, Std. Dev. Of Per-client Throughput: 2.66 messages/s
