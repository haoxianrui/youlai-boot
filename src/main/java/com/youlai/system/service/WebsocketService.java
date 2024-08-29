package com.youlai.system.service;

import java.util.Set;

public interface WebsocketService {

   void addUser(String username);

   void removeUser(String username) ;

   Set<String> getUsers();
   /**
    * 发送消息到前端
    * @param message
    */
   void sendStringToFrontend(String sender,String message);
}
