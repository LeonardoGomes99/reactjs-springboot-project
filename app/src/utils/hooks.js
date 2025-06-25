import { useEffect } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { url } from './request';

export function useNotificacoes(onNotificacao) {
  useEffect(() => {
    const socket = new SockJS(`${url}/sys-notificacoes`, null, {
      withCredentials: true
    });

    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("🟢 Conectado ao WebSocket");
        client.subscribe("/user/queue/notificacoes", (msg) => {
          const data = JSON.parse(msg.body);
          onNotificacao(data);
        });
      },
      onStompError: (frame) => {
        console.error("Erro no STOMP:", frame.headers["message"]);
      },
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [onNotificacao]);
}