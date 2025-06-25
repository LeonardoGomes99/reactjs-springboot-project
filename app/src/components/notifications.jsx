import React from 'react';
import { useNotificacoes } from '../utils/hooks';
import { toast, ToastContainer } from 'react-toastify';

export const Notifications = () => {
  useNotificacoes((notificacao) => {
    toast(`${notificacao.mensagem}`);
    // Se quiser, chame API para marcar como lida:
    fetch(`/api/notificacoes/${notificacao.id}/lida`, {
      method: 'POST',
      credentials: 'include',
    });
  });

  return <ToastContainer />;
}