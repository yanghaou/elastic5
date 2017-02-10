package com.yhao.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TransportClientBean {
    @Value("${adlog.escluster.host}")
    private String esHost;
    @Value("${adlog.escluster.name}")
    private String clusterName;

    private TransportClient client;

    //设置为5.1.1版本的客户端连接方式
    public void initClient()  {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName) //设置集群名称
                .put("client.transport.sniff", true) //自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
                .build();
        client = new PreBuiltTransportClient(settings);

        try {
            //获取所有节点
            String []nodes = esHost.split(",");
            for (String node:nodes){
                //跳过为空的node（当开头、结尾有逗号或多个连续逗号时会出现空node）
                if (node.length()>0){
                    //获取每个节点的ip和port
                    String []hostPort = node.split(":");
                    //每个节点加入客户端
                    client.addTransportAddress(
                            new InetSocketTransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1]))
                    );
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public TransportClient getClient(){
        return client;
    }
}
