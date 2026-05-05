/**
 * Utilitário para exibição de modais modernos usando SweetAlert2
 */
const Modal = {
    // Modal de sucesso
    success: (message, title = 'Sucesso!') => {
        return Swal.fire({
            title: title,
            text: message,
            icon: 'success',
            confirmButtonColor: '#007bff'
        });
    },

    // Modal de erro
    error: (message, title = 'Erro') => {
        return Swal.fire({
            title: title,
            text: message,
            icon: 'error',
            confirmButtonColor: '#ff4d4d'
        });
    },

    // Modal de aviso
    warning: (message, title = 'Aviso') => {
        return Swal.fire({
            title: title,
            text: message,
            icon: 'warning',
            confirmButtonColor: '#ffc107'
        });
    },

    // Modal de confirmação para ações perigosas
    confirm: (title, message, confirmText = 'Sim, eliminar!', cancelText = 'Cancelar') => {
        return Swal.fire({
            title: title || 'Tens a certeza?',
            text: message,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ff4d4d',
            cancelButtonColor: '#6c757d',
            confirmButtonText: confirmText,
            cancelButtonText: cancelText
        });
    },

    // Atalho para eliminar itens via AJAX
    deleteItem: (id, url, itemName = 'este item', successCallback) => {
        Modal.confirm('Eliminar', `Deseja realmente eliminar ${itemName}?`).then((result) => {
            if (result.isConfirmed) {
                Modal.showLoading('A eliminar...');
                fetch(url, { method: 'DELETE' })
                    .then(async res => {
                        const data = await res.json();
                        Modal.hideLoading();
                        
                        if (res.ok) {
                            const successMsg = data.msg || data.message || 'Item eliminado com sucesso.';
                            Modal.toast(successMsg, 'success');
                            if (successCallback) successCallback(data);
                            else setTimeout(() => location.reload(), 1500);
                        } else {
                            const errorMsg = data.msg || data.message || 'Erro ao eliminar item.';
                            Modal.error(errorMsg);
                        }
                    })
                    .catch(err => {
                        Modal.hideLoading();
                        Modal.error('Erro de rede ao tentar eliminar.');
                    });
            }
        });
    },

    // Atalho para submeter formulários via AJAX (Multipart ou JSON)
    submitForm: (formId, url, method = 'POST', successUrlOrCallback) => {
        const form = document.getElementById(formId);
        if (!form) return;

        form.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            Modal.showLoading('A processar...');

            fetch(url, {
                method: method,
                body: formData
            })
            .then(res => res.json())
            .then(data => {
                Modal.hideLoading();
                const feedbackMsg = data.msg || data.message;
                if (feedbackMsg && !data.error) {
                    Modal.toast(feedbackMsg, 'success');
                    if (typeof successUrlOrCallback === 'function') {
                        successUrlOrCallback(data);
                    } else if (successUrlOrCallback) {
                        setTimeout(() => { window.location.href = successUrlOrCallback.replace('{id}', data.publicId || ''); }, 1500);
                    } else {
                        setTimeout(() => location.reload(), 1500);
                    }
                } else {
                    Modal.error(data.msg || data.message || data.error || 'Ocorreu um erro no processamento.');
                }
            })
            .catch(err => {
                Modal.hideLoading();
                Modal.error('Erro de rede ao submeter formulário.');
            });
        });
    },

    // Modal de carregamento (Progress indeterminado)
    showLoading: (message = 'A processar...', title = 'Aguarde um momento') => {
        Swal.fire({
            title: title,
            text: message,
            allowOutsideClick: false,
            allowEscapeKey: false,
            showConfirmButton: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
    },

    // Fechar modal de carregamento
    hideLoading: () => {
        Swal.close();
    },

    // Notificação rápida (Toast)
    toast: (message, icon = 'success') => {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer)
                toast.addEventListener('mouseleave', Swal.resumeTimer)
            }
        });
        Toast.fire({
            icon: icon,
            title: message
        });
    },

    // Atalho para submeter JSON via AJAX
    submitJson: (formId, url, method = 'POST', successUrlOrCallback) => {
        const form = document.getElementById(formId);
        if (!form) return;

        form.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            const jsonData = {};
            formData.forEach((value, key) => jsonData[key] = value);
            
            Modal.showLoading('A processar...');

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(jsonData)
            })
            .then(res => res.json())
            .then(data => {
                Modal.hideLoading();
                if (data.message) {
                    Modal.toast(data.message, 'success');
                    if (typeof successUrlOrCallback === 'function') {
                        successUrlOrCallback(data);
                    } else if (successUrlOrCallback) {
                        setTimeout(() => { window.location.href = successUrlOrCallback.replace('{id}', data.publicId || ''); }, 1500);
                    } else {
                        setTimeout(() => location.reload(), 1500);
                    }
                } else {
                    Modal.error(data.error || 'Ocorreu um erro no processamento.');
                }
            })
            .catch(err => {
                Modal.hideLoading();
                Modal.error('Erro de rede ao submeter.');
            });
        });
    },
    
    // Confirmação de logout
    confirmLogout: (formId) => {
        Modal.confirm('Sair', 'Deseja realmente terminar a sua sessão?', 'Sim, sair', 'Continuar').then((result) => {
            if (result.isConfirmed) {
                document.getElementById(formId).submit();
            }
        });
    },

    // Mostrar modal se houver mensagem no URL ou no model (Thymeleaf)
    checkMessages: () => {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('success')) {
            Modal.toast(urlParams.get('success'), 'success');
        }
        if (urlParams.has('error')) {
            Modal.error(urlParams.get('error'));
        }
    }
};

// Verificar mensagens ao carregar
document.addEventListener('DOMContentLoaded', () => {
    Modal.checkMessages();
});
