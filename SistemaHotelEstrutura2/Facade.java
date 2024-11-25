package SistemaHotelEstrutura2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Facade {
    private final CadastroUsuarios cadastroUsuarios;
    private final GerenciadorReservas gerenciadorReservas;
    private final HistoricoReservas historicoReservas;
    private final Hotel gerenciadorQuartos;
  
    public Facade() {
        this.cadastroUsuarios = new CadastroUsuarios();
        this.gerenciadorReservas = new GerenciadorReservas();
        this.historicoReservas = new HistoricoReservas();
        this.gerenciadorQuartos = new Hotel();
    }

    public void cadastrarUsuario(String cpf, String nome, String telefone, String email) {
        if (!Validador.validarCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        if (!Validador.validarEmail(email)) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (!Validador.validarTelefone(telefone)) {
            throw new IllegalArgumentException("Telefone inválido");
        }

        Usuario usuario = new Usuario(cpf, nome, telefone, email);
        cadastroUsuarios.adicionarUsuario(usuario);
    }

    public void fazerReserva(String cpf, int numeroQuarto, Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas");
        }
        if (!Validador.validarDatas(checkIn, checkOut)) {
            throw new IllegalArgumentException("Datas inválidas");
        }

        Usuario usuario = cadastroUsuarios.consultarUsuario(cpf);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Quarto quarto = gerenciadorQuartos.buscarQuarto(numeroQuarto);
        if (quarto == null) {
            throw new RuntimeException("Quarto não encontrado");
        }

        if (!gerenciadorReservas.verificarDisponibilidade(numeroQuarto, checkIn, checkOut)) {
            throw new RuntimeException("Quarto não disponível para o período");
        }

        String idReserva = gerarIdReserva();
        Reserva novaReserva = new Reserva(idReserva, usuario, quarto, checkIn, checkOut);
        gerenciadorReservas.inserir(novaReserva);
        quarto.setDisponivel(false);
    }

    public void cancelarReserva(String idReserva) {
        if (idReserva == null || idReserva.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da reserva não pode ser vazio");
        }

        List<Reserva> reservasAtivas = gerenciadorReservas.listarReservasPorData();
        Reserva reservaParaCancelar = null;
        
        // Encontrar a reserva com o ID especificado
        for (Reserva reserva : reservasAtivas) {
            if (reserva.getIdReserva().equals(idReserva)) {
                reservaParaCancelar = reserva;
                break;
            }
        }

        if (reservaParaCancelar != null) {
            // Atualizar o status da reserva
            reservaParaCancelar.setStatus(Reserva.StatusReserva.CANCELADA);
            
            // Liberar o quarto
            reservaParaCancelar.getQuarto().setDisponivel(true);
            
            // Adicionar ao histórico
            historicoReservas.adicionarReservaCancelada(reservaParaCancelar);
            
            System.out.println("Reserva " + idReserva + " cancelada com sucesso!");
        } else {
            throw new RuntimeException("Reserva não encontrada para cancelamento");
        }
        gerenciadorReservas.remover(idReserva);
    }

    public List<Quarto> consultarQuartosDisponiveis(Quarto.CategoriaQuarto categoria, Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas");
        }
        return gerenciadorQuartos.getQuartosDisponiveis(categoria, checkIn, checkOut);
    }

    public Reserva consultarReservaPorUsuario(String cpf) {
        if (!Validador.validarCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        return gerenciadorReservas.buscarReservaPorCliente(cpf);
    }

    public void listarReservasPorData(Date data) {
        if (data == null) {
            throw new IllegalArgumentException("Data não pode ser nula");
        }
        List<Reserva> reservas = gerenciadorReservas.listarReservasPorData(data);
        if (reservas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada para a data especificada.");
        } else {
            System.out.println("\nReservas encontradas para a data: " + 
                             new SimpleDateFormat("dd/MM/yyyy").format(data));
            for (Reserva reserva : reservas) {
                System.out.println(reserva);
            }
        }
    }

    private String gerarIdReserva() {
        return GerenciadorReservas.gerarNovoId();
    }

    public void mostrarHistoricoReservas() {
        historicoReservas.exibirTodasReservas();
    }

    public void carregarDadosIniciais() {
        try {
            // Cadastrar usuários de exemplo
            cadastrarUsuario("11111111111", "João Silva", "47999999999", "joao@email.com");
            cadastrarUsuario("22222222222", "Maria Santos", "47988888888", "maria@email.com");
            
            // Criar reservas de exemplo
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fazerReserva("11111111111", 45, sdf.parse("01/12/2024"), sdf.parse("05/12/2024"));
            fazerReserva("22222222222", 46, sdf.parse("10/12/2024"), sdf.parse("15/12/2024"));
            System.out.println("Dados iniciais carregados com sucesso!");
        } catch (ParseException e) {
            System.out.println("Erro ao carregar dados iniciais: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado ao carregar dados: " + e.getMessage());
        }
    }
    
    public List<List<String>> getVisualizacaoReservas() {
        return gerenciadorReservas.getVisualizacaoGrid();
    }

    public void mostrarReservasAtivas() {
        if (gerenciadorReservas.estaVazia()) {
            System.out.println("Não há reservas ativas.");
        } else {
            List<Reserva> reservas = gerenciadorReservas.listarReservasAtivas();
            System.out.println("\n=== Reservas Ativas ===");
            for (Reserva reserva : reservas) {
                String corReserva = gerenciadorReservas.getCorReserva(reserva.getIdReserva());
                System.out.printf("    %s (%s)%n", 
                    reserva.getIdReserva(), 
                    corReserva);
            }
            
            System.out.println("\n=== Lista de Quartos ===");
            List<Quarto> todosQuartos = gerenciadorQuartos.getQuartos();
            for (Quarto quarto : todosQuartos) {
                String status = quarto.isDisponivel() ? "DISPONÍVEL" : "OCUPADO";
                System.out.printf("Quarto %d - Tipo: %s - Status: %s%n",
                    quarto.getNumero(),
                    quarto.getCategoria(),
                    status);
            }
        }
    }
    
    public double verificarTaxaOcupacao() {
        return gerenciadorQuartos.verificarTaxaOcupacao();
    }
}