package com.youlai.system.service;

public interface WebsocketService {

   void addUser(String username);

   void removeUser(String username) ;

   /**
    * 发送消息到前端
    * @param message
    */
   void sendStringToFrontend(String message);
}
