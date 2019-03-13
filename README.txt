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

Notes for TA:
The Server and Client should function as spec'd in the assignment (fingers crossed).
