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
 - Client :
 - HashCodes :
 - NioClient :
 - TransmissionStatistics :

server
 - NioServer :
 - Server :

threadpool
 - AcceptBatchTask :
 - BatchJob :
 - BatchJobMgr :
 - BatchTask :
 - ReadBatchTask :
 - ThreadPool :
 - ThreadPoolMgr :
 - Worker :

util
 - ThroughputStatistics :
 - ThroughputStatisticsMgr :
 - Utils :