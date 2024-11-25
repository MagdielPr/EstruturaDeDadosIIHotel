package SistemaHotelEstrutura2;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoricoReservas {
    // Classe interna representando um nodo da lista encadeada
    private class Nodo {
        Reserva reserva;
        Nodo proximo;

        Nodo(Reserva reserva) {
            // Cria uma cópia profunda da reserva para o histórico
            this.reserva = new Reserva(
                reserva.getIdReserva(),
                reserva.getUsuario(),
                reserva.getQuarto(),
                reserva.getCheckIn(),
                reserva.getCheckOut()
            );
            // Atualiza o status para cancelado
            this.reserva.setStatus(Reserva.StatusReserva.CANCELADA);
            this.proximo = null;
        }
    }

    // Atributos
    private Nodo primeiro;
    private int tamanho;

    // Construtor
    public HistoricoReservas() {
        this.primeiro = null;
        this.tamanho = 0;
    }

    // Adiciona uma reserva ao histórico (apenas reservas canceladas)
    public void adicionarHistorico(Reserva reserva) {
        adicionarReservaCancelada(reserva);
    }

    // Adiciona uma reserva cancelada ao histórico
    public void adicionarReservaCancelada(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula");
        }
        if (reserva.getStatus() != Reserva.StatusReserva.CANCELADA) {
            throw new IllegalArgumentException("Apenas reservas canceladas podem ser adicionadas ao histórico");
        }
        Nodo novoNodo = new Nodo(reserva);
        novoNodo.proximo = primeiro;
        primeiro = novoNodo;
        tamanho++;
    }

    // Exibe todas as reservas do histórico
    public void exibirTodasReservas() {
        if (primeiro == null) {
            System.out.println("Histórico vazio!");
            return;
        }

        System.out.println("\nHistórico de Reservas Canceladas:");
        Nodo atual = primeiro;
        while (atual != null) {
            exibirReserva(atual.reserva);
            atual = atual.proximo;
        }
    }

    // Busca reservas canceladas por CPF
    public void buscarReservasPorCPF(String cpf) {
        if (primeiro == null) {
            System.out.println("Histórico vazio!");
            return;
        }

        boolean encontrou = false;
        Nodo atual = primeiro;

        System.out.println("\nReservas canceladas do cliente CPF " + cpf + ":");
        while (atual != null) {
            if (atual.reserva.getUsuario().getCPF().equals(cpf)) {
                exibirReserva(atual.reserva);
                encontrou = true;
            }
            atual = atual.proximo;
        }

        if (!encontrou) {
            System.out.println("Nenhuma reserva encontrada para este CPF.");
        }
    }

    // Busca reservas canceladas por período
    public void buscarReservasPorPeriodo(Date inicio, Date fim) {
        if (primeiro == null) {
            System.out.println("Histórico vazio!");
            return;
        }

        boolean encontrou = false;
        Nodo atual = primeiro;

        System.out.println("\nReservas canceladas no período:");
        while (atual != null) {
            Date checkIn = atual.reserva.getCheckIn();
            if (checkIn.compareTo(inicio) >= 0 && checkIn.compareTo(fim) <= 0) {
                exibirReserva(atual.reserva);
                encontrou = true;
            }
            atual = atual.proximo;
        }

        if (!encontrou) {
            System.out.println("Nenhuma reserva encontrada neste período.");
        }
    }

    // Busca uma reserva específica por ID
    public Reserva buscarReservaPorId(String idReserva) {
        Nodo atual = primeiro;
        while (atual != null) {
            if (atual.reserva.getIdReserva().equals(idReserva)) {
                return atual.reserva;
            }
            atual = atual.proximo;
        }
        return null;
    }

    // Retorna o número de reservas no histórico
    public int getTamanho() {
        return tamanho;
    }

    // Verifica se o histórico está vazio
    public boolean estaVazio() {
        return primeiro == null;
    }

    // Limpa todo o histórico
    public void limparHistorico() {
        primeiro = null;
        tamanho = 0;
    }

    // Método auxiliar para exibir uma reserva
    private void exibirReserva(Reserva reserva) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n----------------------------------------\n");
        sb.append("ID Reserva: ").append(reserva.getIdReserva()).append("\n");
        sb.append("Cliente: ").append(reserva.getUsuario().getNome()).append("\n");
        sb.append("CPF: ").append(reserva.getUsuario().getCPF()).append("\n");
        sb.append("Quarto: ").append(reserva.getQuarto().getNumero()).append("\n");
        sb.append("Categoria: ").append(reserva.getQuarto().getCategoria()).append("\n");
        sb.append("Check-in: ").append(new SimpleDateFormat("dd/MM/yyyy").format(reserva.getCheckIn())).append("\n");
        sb.append("Check-out: ").append(new SimpleDateFormat("dd/MM/yyyy").format(reserva.getCheckOut())).append("\n");
        sb.append("Valor Total: R$ ").append(String.format("%.2f", reserva.getValorTotal())).append("\n");
        sb.append("Status: ").append(reserva.getStatus());
        System.out.println(sb.toString());
    }
}
