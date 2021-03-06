基于JAVA MINA框架实现的简单IM系统
=================================
    这个系统是个人学习MINA框架编写的一个IM系统，或者说是一个模块。
    基本实现客户端到服务器的简单通讯（文字、图片、语音等）。

#### TCP协议
    IM应用TCP协议，应用层模仿HTTP编写自定义协议。

#### 离线缓存
    IM的离线消息缓存应用的是LRU算法（LeastRecentlyUsed 近期最少使用算法），
    当缓存溢出时，就会自动取出缓存写入数据库。提供了写入数据库的接口，
    后续开发就是一个高可用的海量KV存储系统，结合IM就能实现快速的离线数据查询。
    
#### 心跳机制
    心跳机制一开始是自己设计心跳机制，通过设定超时时间的心跳来实现keepalive。
    最新改用了MINA集成的心跳过滤器来实现。
   
    
## Version 1.0
    发布于2014-08-22，然后开始重构协议。
    
## Verson 2.0
    发布于2014-08-31，系统更加稳定、高效。
    重构了应用层协议，分别以DataPacket和BytePacket左右解码包（数据包）和编码包（字节包）。
    重构了日志系统（采用log4j+slf4j），日志分析更清晰。
    编写了ANT编译所需的build.xml文件。
    修复了1.0的一些bug，修复了端口占用问题，添加了唯一登录用户的判断逻辑。

## wiki：
    https://github.com/scauwjh/IM/wiki/im_wiki
