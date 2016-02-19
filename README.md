# 数据集成指南

@(SyncTemp)[OPC,UTGARD, Markdown]

本文主要讲解接触到一个全新的电厂时，需要对接工控系统的数据时，提供一些方式和方法的指导和借鉴。无论是企业版SNMIS还是政府版GMIS，我们都需要对系统服务方或者被监管方（垃圾电厂）对接相关工控系统的实时数据。常见的系统有**DCS控制系统**，**ECS控制系统**，**垃圾吊控制系统**，**水处理控制系统**，**地磅称重系统**，对于不同的控制系统采取的通信协议会有所区别，具体的对接过程如下。

-----------------

[TOC]

##简介
根据之前的实践，可以总结一些结论：DCS、ECS、水处理这三个控制系统的对接一般采取OPC的通信方式，地磅一般采取JDBC的方式读取源数据库，垃圾吊可以采取JDBC或者modbus的方式。

##通信协议
主要的通信协议是三种，OPC、JDBC和MODBUS。

###OPC (OLE for Process Control)
当确定使用OPC作为数据对接的通讯协议后，对方厂家所能提供的一般就是工程师站的IP地址，登陆用户名和密码。

- **网络连接** ：用一根网线连接工程师站和测试的笔记本，IP地址设置为工程师站IP所在的网段即可，如工程师站的IP为172.16.1.1，可以将测试本的IP设置为192.16.1.2，只需要满足在192.16.1网段不与其他电脑的IP地址冲突即可；
![image](http://7xr1w9.com1.z0.glb.clouddn.com/wangluolianjie.png)

- **寻找OPC服务**：通过uip中的opc辅助工具类**UtgardOpcHelper**来寻找工程师站上面的OPC服务，需要输入三个参数**目的IP**，**目标登录用户名**以及**目标的登录密码**，主要是获取工程师站上面的OPC服务的clsid，**clsid**是windows系统中注册表的唯一标示。


        try {
            ConnectionInformation connectionInformation = new ConnectionInformation();
            connectionInformation.setHost("localhost");
            connectionInformation.setUser("Administrator");
            connectionInformation.setPassword("123456");
            connectionInformation.setDomain("");
            ServerList serverList = new ServerList(connectionInformation.getHost(),
                    connectionInformation.getUser(), connectionInformation.getPassword(),
                    connectionInformation.getDomain());

            /** According the progid get the clsid, then get the classdetail */
            /** Whatever the using DA agreement */
            classDetails = serverList
                    .listServersWithDetails(new Category[] {
                            Categories.OPCDAServer10, Categories.OPCDAServer20,
                            Categories.OPCDAServer30 }, new Category[] {});

            log.error("-----------------------------------------------------------");
            log.error("--------开始获取目标Ip：" + connectionInformation.getHost() + "下所有on service的opc服务.-----");
            for (ClassDetails cds : classDetails) {
                log.error("ClassDetails  Show.   ");
                log.error("    ProgId--->>" + cds.getProgId());
                log.error("    Desp  --->>" + cds.getDescription());
                log.error("    ClsId --->>" + cds.getClsId());
            }
            log.error("-----------------------------------------------------------");
        } catch (JIException e) {
            log.error("获取配置文件中内容时出错",e);
        } catch (UnknownHostException e1) {
            log.error("Host无法识别或者格式错误",e1);
        }

- **OPC通信测试**： 建立OPC测试连接时，需要四个条件，分别是工程师站的IP，工程师站的登录用户名，工程师站的登录密码以及之前找出来的OPC服务的clsid的值。
![image](http://7xr1w9.com1.z0.glb.clouddn.com/opcceshi.png)

- **通信测点数据测试**：既然OPC的通信连接也建立好了，下一步就要获取实时数据了，opc是通过对测点名称进行匹配的，也就是说，要想获取工程师站上目标OPC服务上的测点实时数据，我们需要找到这些通信测点的名称，寻找测点名的方式有多种。
1. 询问厂家工程师获取点表。
2. 使用**OPC Client**工具如，MatrikonOPC Explorer ，opcClient，将此类opc client软件安装在工程师站上，
客户端上Item Id列即是我们寻找的测点的名称，将需要的测点名称整理出来。
3. 如果直接将客户端安装在工程师站上不可行，那么我们可以选择将客户端安装在测试本或者接口机上获取测点名称。此时配置相较方式二需要多做一些配置，包括了-->用户名和密码需要跟工程师站保持一致，接口机需要配置DCOM。（DCOM的配置另有详解）
![image](http://7xr1w9.com1.z0.glb.clouddn.com/opcclient.jpg)

- **项目配置**： 对接系统获取数据，要在接口机上获取工控系统的实时数据，还需要对uip进行相关配置。
在utgard-opc-config.properties配置文件中配置被接方的信息，主要是
    
    ``` java
        host->1=192.168.0.71
        sysid->1=1
        domain->1=
        username->1=Administrator
        password->1=123456
        clsid->1=ba198b62-32e3-11d1-a1b5-00805f35623c
        progid->1=
    ```
其中sysId为系统ID，此为自定义的编号；host为工程师站的IP地址；username为登录用户名；
password为登录密码；clsid为注册表ID；

### JDBC (Java Data Base Connectivity)
**JDBC**主要适用于地磅称重系统的对接中，地磅的称重系统一般使用sql server系列的数据库作为数据存储。对接过程如下：
- **网络接入**：我们需要了解到地磅系统的IP地址，设置接口机中一块新网卡的IP地址，使其可以与地磅系统网络通信。
- **数据库分析**：需要在数据库中找到入厂垃圾的流水明细表。做好字段的对应，根据地磅系统数据库中的表结构，更改TradeDataService.java中insertCurrDayTradeData方法，需要更改地磅数据同步的sql。
**ps**：如果地磅的数据库是sqlserver 2000版本，需要安装sqlserver 2000 sp4 补丁。如果补丁的方式协调不了，可用数据库同步软件DBSync定时同步数据。
![image](http://7xr1w9.com1.z0.glb.clouddn.com/trade.png)

### UDP（User Datagram Protocol）
**UDP**的使用场景是在对接系统中存在网闸时，在有网闸存在的结构图中，需要在接口机上运行silence，获取工程师站上OPC服务的实时数据，再将实时数据通过UDP的方式发送至数据服务器的指定端口上。数据服务器上需要部署uip，获取本机端口上的UDP传输的实时数据。  

![image](http://7xr1w9.com1.z0.glb.clouddn.com/udp.png)