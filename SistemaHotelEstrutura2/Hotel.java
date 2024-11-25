package SistemaHotelEstrutura2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Hotel {
    private String nome;
    private int capacidade;
    private int quartosOcupados;
    private GerenciadorReservas gerenciadorReservas;
    private List<Quarto> quartos;

    public Hotel() {
        this("Hotel Padrão", 100);
    }

    public Hotel(String nome, int capacidade) {
        if (nome == null || nome.isEmpty() || capacidade <= 0) {
            throw new IllegalArgumentException("Nome do hotel e capacidade devem ser válidos.");
        }
        this.nome = nome;
        this.capacidade = capacidade;
        this.quartosOcupados = 0;
        this.gerenciadorReservas = new GerenciadorReservas();
        this.quartos = new ArrayList<>();
        inicializarQuartos();
    }

    private void inicializarQuartos() {
        for (int i = 1; i <= capacidade; i++) {
            Quarto.CategoriaQuarto categoria;
            if (i <= capacidade * 0.2) {
                categoria = Quarto.CategoriaQuarto.LUXO;
            } else if (i <= capacidade * 0.5) {
                categoria = Quarto.CategoriaQuarto.PRESIDENCIAL;
            } else {
                categoria = Quarto.CategoriaQuarto.ECONOMICO;
            }
            quartos.add(new Quarto(i, categoria));
        }
    }

    public Quarto buscarQuarto(int numeroQuarto) {
        return quartos.stream()
                .filter(q -> q.getNumero() == numeroQuarto)
                .findFirst()
                .orElse(null);
    }

    public List<Quarto> getQuartosDisponiveis(Quarto.CategoriaQuarto categoria, Date checkIn, Date checkOut) {
        return quartos.stream()
                .filter(quarto -> quarto.getCategoria() == categoria &&
                        quarto.isDisponivel() &&
                        gerenciadorReservas.verificarDisponibilidade(quarto.getNumero(), checkIn, checkOut))
                .collect(Collectors.toList());
    }

    public double verificarTaxaOcupacao() {
        if (capacidade == 0) {
            return 0.0;
        }
        return (double) quartosOcupados / capacidade * 100;
    }

    public List<Integer> listarQuartosDisponiveis() {
        return quartos.stream()
                .filter(Quarto::isDisponivel)
                .map(Quarto::getNumero)
                .collect(Collectors.toList());
    }

    public boolean reservarQuarto(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva inválida.");
        }
        if (quartosOcupados >= capacidade) {
            System.out.println("O hotel está totalmente ocupado.");
            return false;
        }
        
        if (!gerenciadorReservas.verificarDisponibilidade(
                reserva.getQuarto().getNumero(),
                reserva.getCheckIn(),
                reserva.getCheckOut())) {
            return false;
        }
        
        gerenciadorReservas.inserir(reserva);
        quartosOcupados++;
        return true;
    }

    public boolean cancelarReserva(int numeroQuarto) {
        List<Reserva> reservasAtivas = gerenciadorReservas.listarReservasAtivas();
        for (Reserva reserva : reservasAtivas) {
            if (reserva.getQuarto().getNumero() == numeroQuarto) {
                gerenciadorReservas.remover(reserva.getIdReserva());
                quartosOcupados--;
                return true;
            }
        }
        return false;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getQuartosOcupados() {
        return quartosOcupados;
    }

    public GerenciadorReservas getGerenciadorReservas() {
        return gerenciadorReservas;
    }

    public List<Quarto> getQuartos() {
        return new ArrayList<>(quartos);
    }
}