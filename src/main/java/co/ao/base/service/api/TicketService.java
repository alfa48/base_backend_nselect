package co.ao.base.service.api;

import co.ao.base.model.PageResponse;
import co.ao.base.model.TicketDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class TicketService extends BaseApiService {

    public PageResponse<TicketDTO> listarTickets(int pagina, int tamanho) {
        return get("/tickets?pagina=" + pagina + "&tamanho=" + tamanho, new ParameterizedTypeReference<PageResponse<TicketDTO>>() {});
    }

    public TicketDTO buscarTicket(String publicId) {
        return get("/tickets/" + publicId, TicketDTO.class);
    }

    public String criarTicket(Object dados) {
        return post("/tickets", dados, String.class);
    }

    public void editarTicket(String publicId, Object dados) {
        put("/tickets/" + publicId, dados, Void.class);
    }

    public void eliminarTicket(String publicId) {
        delete("/tickets/" + publicId);
    }
}
