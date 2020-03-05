package com.chenyacheng.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author chenyacheng
 * @date 2019/06/17
 */
public class Client {

	private static String host = "192.168.1.241";

	private static int port = 9000;

	private static void start() {
		// worker负责读写数据
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			// 辅助启动类
			Bootstrap bootstrap = new Bootstrap()
					// 设置线程池
					.group(worker)
					// 设置socket工厂
					.channel(NioSocketChannel.class)
					// 设置管道
					.handler(new ClientInitializer())
					// 如果不设置超时，连接会一直占用本地线程，端口，连接客户端一多，会导致本地端口用尽及CPU压力
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
			Channel channel = bootstrap.connect(host, port).sync().channel();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			// 等待客户端链路关闭
			channel.closeFuture().sync();
			channel.writeAndFlush(in.readLine() + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 优雅的退出，释放NIO线程组
			worker.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		start();
	}
}
