/*
Copyright 2021 kino jin
zhikai.jin@bozhon.com
This file is part of next-sdk.
next-sdk is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
next-sdk is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with next-sdk.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lib.sdk.next.socket;


import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by maqing 2018/11/15 15:37
 * Email：2856992713@qq.com
 */
public interface CustomSocketListener {
    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    public void onOpen(WebSocket webSocket, Response response);


    /**
     * Invoked when a text (type {@code 0x1}) message has been received.
     */
    public void onMessage(WebSocket webSocket, String text) ;



    /**
     * Invoked when a binary (type {@code 0x2}) message has been received.
     */
    public void onMessage(WebSocket webSocket, ByteString bytes) ;


    /**
     * Invoked when the peer has indicated that no more incoming messages will be transmitted.
     */
    public void onClosing(WebSocket webSocket, int code, String reason) ;


    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    public void onClosed(WebSocket webSocket, int code, String reason) ;


    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    public void onFailure(WebSocket webSocket, Throwable t, Response response) ;



}
