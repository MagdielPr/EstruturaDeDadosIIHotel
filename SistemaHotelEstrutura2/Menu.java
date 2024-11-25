package SistemaHotelEstrutura2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private static Facade hotel;
    private static Scanner scanner;
    private static SimpleDateFormat dateFormat;

    public static void main(String[] args) {
        inicializarSistema();
        boolean continuar = true;

        while (continuar) {
            try {
                exibirMenu();
                String opcaoStr = scanner.nextLine().trim();

                if (!opcaoStr.matches("\\d+")) {
                    System.out.println("\nOpção inválida! Digite apenas números.");
                    continue;
                }

                int opcao = Integer.parseInt(opcaoStr);

                processarOpcao(opcao);
                if (opcao == 7) {
                    continuar = false;
                    System.out.println("\nSistema encerrado!");
                }
            } catch (Exception e) {
                System.out.println("\nErro inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    private static void inicializarSistema() {
        hotel = new Facade();
        scanner = new Scanner(System.in);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        // Carregar dados iniciais
        hotel.carregarDadosIniciais();
    }

    private static void exibirMenu() {
        System.out.println("\n=== Hotel Sorriso ===");
        System.out.println("1. Cadastrar Usuário");
        System.out.println("2. Buscar Reserva por CPF do Usuário");
        System.out.println("3. Fazer Reserva");
        System.out.println("4. Mostrar Reservas Ativas");
        System.out.println("5. Cancelar Reserva");
        System.out.println("6. Consultar Reservas por Data");
        System.out.println("7. Ver Histórico de Reservas");
        System.out.println("8. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                cadastrarUsuario();
                break;
            case 2:
                buscarUsuario();
                break;
            case 3:
                fazerReserva();
                break;
            case 4:
                mostrarReservasAtivas();
                break;
            case 5:
                cancelarReserva();
                break;
            case 6:
                consultarReservasPorData();
                break;
            case 7:
                verHistoricoReservas();
                break;
            case 8:
                System.out.println("\nSistema encerrado!");
                System.exit(0); 
                break;
            default:
                System.out.println("\nOpção inválida!");
        }
    }
    
    private static void verHistoricoReservas() {
        try {
            System.out.println("\n=== Histórico de Reservas ===");
            hotel.mostrarHistoricoReservas();
        } catch (Exception e) {
            System.out.println("Erro ao exibir histórico: " + e.getMessage());
        }
    }
    private static void cadastrarUsuario() {
        try {
            System.out.println("\n=== Cadastro de Usuário ===");

            System.out.print("CPF (apenas números): ");
            String cpf = scanner.nextLine().trim();
            if (!cpf.matches("\\d{11}")) {
                System.out.println("CPF inválido! Digite exatamente 11 números.");
                return;
            }

            System.out.print("Nome: ");
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) {
                System.out.println("Nome não pode ser vazio!");
                return;
            }

            System.out.print("Telefone: ");
            String telefone = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            hotel.cadastrarUsuario(cpf, nome, telefone, email);
            System.out.println("Usuário cadastrado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    private static void buscarUsuario() {
        try {
            System.out.println("\n=== Buscar Usuário ===");
            System.out.print("Digite o CPF (apenas números): ");
            String cpf = scanner.nextLine().trim();

            if (!cpf.matches("\\d{11}")) {
                System.out.println("CPF inválido! Digite exatamente 11 números.");
                return;
            }

            Reserva reserva = hotel.consultarReservaPorUsuario(cpf);
            if (reserva != null) {
                System.out.println("\nReserva encontrada:");
                System.out.println(reserva.toString());
            } else {
                System.out.println("Nenhuma reserva encontrada para este CPF!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        }
    }

    private static void fazerReserva() {
        try {
            System.out.println("\n=== Nova Reserva ===");

            System.out.print("CPF do usuário (apenas números): ");
            String cpf = scanner.nextLine().trim();
            if (!cpf.matches("\\d{11}")) {
                System.out.println("CPF inválido! Digite exatamente 11 números.");
                return;
            }

            // Adicionado: mostrar categorias disponíveis
            System.out.println("\nCategorias disponíveis:");
            for (Quarto.CategoriaQuarto categoria : Quarto.CategoriaQuarto.values()) {
                System.out.printf("%s (R$ %.2f por dia)%n", 
                    categoria.name(), categoria.getValorBase());
            }
            
            System.out.print("\nEscolha a categoria (ECONOMICO/LUXO/PRESIDENCIAL): ");
            String categoriaStr = scanner.nextLine().toUpperCase();
            Quarto.CategoriaQuarto categoria;
            try {
                categoria = Quarto.CategoriaQuarto.valueOf(categoriaStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Categoria inválida!");
                return;
            }

            Date checkIn = lerData("Data de check-in (dd/MM/yyyy): ");
            if (checkIn == null) return;
            
            // Validação de data passada
            if (checkIn.before(new Date())) {
                System.out.println("Data de check-in não pode ser no passado!");
                return;
            }

            Date checkOut = lerData("Data de check-out (dd/MM/yyyy): ");
            if (checkOut == null || checkOut.before(checkIn)) {
                System.out.println("Data de check-out inválida! Deve ser posterior ao check-in.");
                return;
            }

            // Mostrar quartos disponíveis da categoria selecionada
            List<Quarto> quartosDisponiveis = hotel.consultarQuartosDisponiveis(categoria, checkIn, checkOut);
            if (quartosDisponiveis.isEmpty()) {
                System.out.println("Não há quartos disponíveis nesta categoria para o período selecionado.");
                return;
            }

            System.out.println("\nQuartos disponíveis:");
            for (Quarto quarto : quartosDisponiveis) {
                System.out.printf("Quarto %d - %s%n", 
                    quarto.getNumero(), quarto.getCategoria());
            }

            System.out.print("\nEscolha o número do quarto: ");
            String numeroQuartoStr = scanner.nextLine().trim();
            if (!numeroQuartoStr.matches("\\d+")) {
                System.out.println("Número do quarto inválido! Digite apenas números.");
                return;
            }
            int numeroQuarto = Integer.parseInt(numeroQuartoStr);

            // Validar se o quarto existe e está na lista de disponíveis
            boolean quartoValido = quartosDisponiveis.stream()
                .anyMatch(q -> q.getNumero() == numeroQuarto);
            if (!quartoValido) {
                System.out.println("Quarto inválido ou não disponível!");
                return;
            }

            hotel.fazerReserva(cpf, numeroQuarto, checkIn, checkOut);
            System.out.println("Reserva realizada com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao fazer reserva: " + e.getMessage());
        }
    }

    private static void mostrarReservasAtivas() {
        try {
            hotel.mostrarReservasAtivas();
        } catch (Exception e) {
            System.out.println("Erro ao mostrar reservas: " + e.getMessage());
        }
    }


    private static void cancelarReserva() {
        try {
            System.out.println("\n=== Cancelar Reserva ===");
            System.out.print("Digite o ID da reserva: ");
            String idReserva = scanner.nextLine().trim();

            hotel.cancelarReserva(idReserva);
            System.out.println("Reserva cancelada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cancelar reserva: " + e.getMessage());
        }
    }

    private static void consultarReservasPorData() {
        try {
            System.out.println("\n=== Consultar Reservas por Data ===");
            Date data = lerData("Digite a data (dd/MM/yyyy): ");
            if (data == null) return;

            hotel.listarReservasPorData(data);
        } catch (Exception e) {
            System.out.println("Erro ao consultar reservas: " + e.getMessage());
        }
    }

    private static Date lerData(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String dataStr = scanner.nextLine().trim();
                return dateFormat.parse(dataStr);
            } catch (ParseException e) {
                System.out.println("Formato de data inválido! Use dd/MM/yyyy");
                return null;
            }
        }
    }
}