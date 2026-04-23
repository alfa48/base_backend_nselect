/**
 * Helper para gestão de Modais (Fiinika Design System)
 */

function openModal(type, title, message, confirmUrl, confirmText) {
    const wrapper = $('.modals-wrapper');
    const modal = $(`#modal-${type}`);

    if (title) modal.find('.modal-header').text(title);
    if (message) modal.find('.modal-subtext').text(message);
    
    if (type === 'warning' && confirmUrl) {
        $('#modal-confirm-btn').attr('href', confirmUrl);
        if (confirmText) $('#modal-confirm-text').text(confirmText);
    }

    wrapper.show().css('opacity', 1);
    modal.show().css({
        'opacity': 1,
        'transform': 'translate3d(0, 0, 0) scale3d(1, 1, 1)'
    });
}

function closeModal() {
    $('.modals-wrapper').hide().css('opacity', 0);
    $('.modal').hide().css({
        'opacity': 0,
        'transform': 'translate3d(0, -120%, 0) scale3d(1, 1, 1)'
    });
}

// Atalhos para facilitar o uso
const Modal = {
    success: (msg, title) => openModal('success', title || 'Sucesso!', msg),
    error: (msg, title) => openModal('error', title || 'Erro!', msg),
    confirm: (msg, url, title, btnText) => openModal('warning', title || 'Confirmação', msg, url, btnText || 'Sim, continuar')
};

// Auto-trigger baseado em parâmetros de URL ou elementos ocultos
$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    
    // Sucesso via URL
    if (urlParams.has('loginSuccess')) {
        Modal.success('Bem-vindo de volta! Login efetuado com sucesso.', 'Olá!');
        window.history.replaceState({}, document.title, window.location.pathname);
    }

    // Sucesso via Elemento Oculto (Flash Attribute do Thymeleaf)
    const successMsg = $('#server-success-message').val();
    if (successMsg) {
        Modal.success(successMsg, 'Sucesso!');
    }

    // Erro via Elemento Oculto (Thymeleaf)
    const serverError = $('#server-error-message').val();
    if (serverError) {
        Modal.error(serverError, 'Erro de Autenticação');
    }

    // Erro de operação (criar/editar)
    const operationError = $('#operation-error-message').val();
    if (operationError) {
        Modal.error(operationError, 'Erro!');
    }
});
