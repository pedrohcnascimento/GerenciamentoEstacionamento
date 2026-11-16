package inpark.gerenciamentoestacionamento.application.mappers;

import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.model.entities.Carro;

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
