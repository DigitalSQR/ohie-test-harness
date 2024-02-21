import { Client } from '@stomp/stompjs';
import { useState, useEffect, useRef } from 'react';

const WebSocketService = () => {
    const [stompClient, setStompClient] = useState(null);
    const stompClientRef = useRef(null);
    const isWebSocketConnected = useRef(false);

    const webSocketConnect = () => {
        if (isWebSocketConnected.current) return;

        const client = new Client({
            brokerURL: process.env.REACT_APP_WEB_SOCKET_URL || 'ws://https://tht.argusoft.com/api/socket',
            reconnectDelay: 5000,
            heartbeatIncoming: 30000,
            heartbeatOutgoing: 30000,
        });

        client.onConnect = (frame) => {
            console.log('WebSocket connection established!', frame);
            setStompClient(client);
            stompClientRef.current = client;
            isWebSocketConnected.current = true;
        }

        client.activate();
    };

    const webSocketDisconnect = () => {
        const client = stompClientRef.current;
        if (!client || !isWebSocketConnected.current) return;
        client.deactivate();
        console.log("Disconnected from WebSocket.");
        setStompClient(null);
        stompClientRef.current = null;
        isWebSocketConnected.current = false;
    };

    return {
        stompClient,
        webSocketConnect,
        webSocketDisconnect
    };
};

export default WebSocketService;
