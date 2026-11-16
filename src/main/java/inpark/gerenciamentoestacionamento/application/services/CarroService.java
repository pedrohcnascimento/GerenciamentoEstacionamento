package inpark.gerenciamentoestacionamento.application.services;

import inpark.gerenciamentoestacionamento.application.dto.requests.CarroRequest;
import inpark.gerenciamentoestacionamento.application.dto.response.CarroResponse;
import inpark.gerenciamentoestacionamento.application.mappers.CarroMapper;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.CarroRepository;
import inpark.gerenciamentoestacionamento.infraestructure.repositories.usuario.ClienteRepository;
import inpark.gerenciamentoestacionamento.model.entities.Carro;
import inpark.gerenciamentoestacionamento.model.entities.usuarios.Cliente;
import inpark.gerenciamentoestacionamento.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarroService {
    private final CarroRepository carroRepository;
    private final ClienteRepository clienteRepository;

    public CarroService(CarroRepository carroRepository, ClienteRepository clienteRepository) {
        this.carroRepository = carroRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<CarroResponse> listarCarros() {
        return carroRepository.findByStatusTrue()
                .stream()
                .map(CarroMapper::fromEntity)
                .toList();
    }

    public CarroResponse buscarPorId(Long id) {
        Optional<Carro> optionalCarro = carroRepository.findById(id);

        if (optionalCarro.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return CarroMapper.fromEntity(optionalCarro.get());
    }

    @Transactional
    public CarroResponse cadastrarCarro(CarroRequest dto) {
        if (dto.clienteId() == null) {
            throw new IdNaoCadastrado("Cliente não encontrado com no sistema.");
        }
        Carro carro = CarroMapper.toEntity(dto);
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente não encontrado com no sistema.");
        }

        Cliente cliente = optCliente.get();
        if (cliente.getCarros() == null) {
            cliente.setCarros(new ArrayList<>());
        }
        cliente.getCarros().add(carro);
        carro.setCliente(cliente);

        carro.setStatus(true);
        return CarroMapper.fromEntity(carroRepository.save(carro));
    }

    @Transactional
    public CarroResponse atualizarCarro(CarroRequest dto, Long id) {
        if (dto.clienteId() == null) {
            throw new IdNaoCadastrado("Carro ou Cliente não encontrado no sistema.");
        }
        Optional<Carro> carroOriginal = carroRepository.findById(id);
        Optional<Cliente> novoCliente = clienteRepository.findById(dto.clienteId());

        if (carroOriginal.isEmpty() || novoCliente.isEmpty()) {
            throw new IdNaoCadastrado("Carro ou Cliente não encontrado no sistema.");
        }

        Carro carro = carroOriginal.get();
        Cliente clienteOriginal = carro.getCliente();
        Cliente cliente = novoCliente.get();

        if (carro.getCliente() != cliente) {
            if (clienteOriginal != null && clienteOriginal.getCarros() != null) {
                clienteOriginal.getCarros().remove(carro);
            }
            carro.setCliente(cliente);
            if (cliente.getCarros() == null) {
                cliente.setCarros(new ArrayList<>());
            }
            cliente.getCarros().add(carro);
        }

        carro.setPlaca(dto.placa());
        carro.setModelo(dto.modelo());
        carro.setCor(dto.cor());

        return CarroMapper.fromEntity(carroRepository.save(carro));
    }

    @Transactional
    public void deletarCarro(Long id) {
        Optional<Carro> optCarro = carroRepository.findById(id);

        if (optCarro.isEmpty()) {
            throw new IdNaoCadastrado("Carro não encontrado no sistema.");
        }

        Carro carro = optCarro.get();
        Cliente cliente = carro.getCliente();
        if (cliente != null && cliente.getCarros() != null) {
            cliente.getCarros().remove(carro);
        }

        carro.setStatus(false);
        carroRepository.save(carro);
    }
}
