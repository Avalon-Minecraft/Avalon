package com.github.avalon.server;

import com.github.avalon.chat.ChatModule;
import com.github.avalon.concurrent.ConcurrentModule;
import com.github.avalon.network.SocketServer;
import com.github.avalon.network.PlayerSessionContainer;
import com.github.avalon.network.protocol.ProtocolContainer;
import com.github.avalon.packet.PacketModule;
import com.github.avalon.scheduler.SchedulerModule;
import com.github.avalon.translation.TranslationModule;

import java.net.InetSocketAddress;

/**
 * This class provides all methods related to the server. If we want to start the server we need to
 * run {@code startServer()}
 *
 * @author Horizon
 * @version 1.0
 */
public interface IServer {

  /**
   * This method if is called will start the server.
   *
   * @since 1.0
   */
  void startServer();

  /**
   * This method will stop the server.
   *
   * @since 1.0
   */
  void stopServer();

  /**
   * This method will create all necessary connections.
   *
   * @since 1.0
   */
  void bindServer();

  /**
   * @return Returns the current state of the server {@link ServerState}
   * @since 1.0
   */
  ServerState getServerState();

  /**
   * This method is for setting the server state.
   *
   * @since 1.0
   * @param serverState Stater that server is currently in.
   */
  void setServerState(ServerState serverState);

  /**
   * This should set the current {@link ServerData} repository.
   *
   * @param serverData Repository with server data.
   */
  void setServerData(ServerData serverData);

  /**
   * Get the current server data.
   *
   * @return Should return the current server data.
   */
  ServerData getServerData();

  /**
   * @return Return the container that stores all registered {@link
   *     com.github.avalon.network.protocol.ProtocolRegistry}
   */
  ProtocolContainer getProtocolContainer();

  /** @return Container of the players. */
  PlayerSessionContainer getPlayerSessionRegistry();

  /** @return Concurrent module. */
  ConcurrentModule getConcurrentModule();

  /** @return Module for scheduling tasks. */
  SchedulerModule getSchedulerModule();

  /** @return Returns the parent module of server. */
  Bootstrap getBootstrap();

  /**
   * @return Returns the module that manages and handle the packets and {@link
   *     com.github.avalon.packet.PacketListener}s.
   */
  PacketModule getPacketModule();

  /** @return Returns the module that manages the chat. */
  ChatModule getChatModule();

  /** @return Returns translation module. */
  TranslationModule getTranslationModule();

  /** @return We have 2 classes for server one is for server itself and one is for connection. */
  SocketServer getSocketServer();

  /**
   * Creates socket address that can be used for server.
   *
   * @since 1.0
   * @param address Address of server.s
   * @param port Port of the server.
   */
  default InetSocketAddress createAddress(String address, int port) {
    InetSocketAddress socketAddress;
    if (address != null) {
      socketAddress = new InetSocketAddress(address, port);
    } else {
      socketAddress = new InetSocketAddress(port);
    }
    return socketAddress;
  }
}
