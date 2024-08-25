package com.youlai.system.service;

public interface WebsocketService {

   void addUser(String username);

   void removeUser(String username) ;

   int getOnlineUserCount() ;
}
