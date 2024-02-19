import { Client } from '@stomp/stompjs';
import { useState } from 'react';

const WebSocketService = () => {
    const [stompClient, setStompClient] = useState(null);

    const webSocketConnect = () => {
        const client = new Client({
            brokerURL: process.env.REACT_APP_WEB_SOCKET_URL || 'ws://https://tht.argusoft.com/api/socket',
            reconnectDelay: 5000,
            heartbeatIncoming: 30000,
            heartbeatOutgoing: 30000,
        });

        client.onConnect = (frame) => {
            console.log('WebSocket connection established!', frame);
            setStompClient(client);
        }

        client.activate();
    };

    const webSocketDisconnect = () => {
        if (stompClient && stompClient.connected) {
            stompClient.deactivate();
            console.log("Disconnected from WebSocket.");
            setStompClient(null);
        }
    };

    return {
        stompClient,
        webSocketConnect,
        webSocketDisconnect
    };
};

export default WebSocketService;
