package parkwise.gerenciamentoestacionamento.application.mappers;

import parkwise.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import parkwise.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import parkwise.gerenciamentoestacionamento.model.entities.Carro;

public class CarroMapper {

    public static Carro toEntity(CarroRequest dto) {
        Carro carro = new Carro();

        carro.setCor(dto.cor());
        carro.setModelo(dto.modelo());
        carro.setPlaca(dto.placa());

        return carro;
    }

    public static CarroResponse fromEntity(Carro carro) {
        return new CarroResponse(
                carro.getId(),
                carro.getCliente().getId(),
                carro.getPlaca(),
                carro.getModelo(),
                carro.getCor(),
                carro.getStatus()
        );
    }
}
