package SistemaHotelEstrutura2;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Reserva {
    private String idReserva;
    private Usuario usuario;
    private Quarto quarto;
    private Date checkIn;
    private Date checkOut;
    private StatusReserva status;
    private double valorTotal;

    public enum StatusReserva {
        ATIVA,
        CANCELADA,
        CONCLUIDA
    }

    public Reserva(String idReserva, Usuario usuario, Quarto quarto, Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas");
        }
        if (checkIn.after(checkOut)) {
            throw new IllegalArgumentException("Data de check-in não pode ser posterior ao check-out");
        }

        this.idReserva = idReserva;
        this.usuario = usuario;
        this.quarto = quarto;
        this.checkIn = (Date) checkIn.clone();
        this.checkOut = (Date) checkOut.clone();
        this.status = StatusReserva.ATIVA;
        calcularValorTotal();
    }

    private void calcularValorTotal() {
        long diff = checkOut.getTime() - checkIn.getTime();
        long dias = Math.max(1, diff / (1000 * 60 * 60 * 24));
        this.valorTotal = dias * quarto.getValorDiaria();
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setCliente(Usuario cliente) {
        this.usuario = cliente;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public Date getCheckIn() {
        return (Date) checkIn.clone();
    }

    public void setCheckIn(Date checkIn) {
        if (checkIn == null) {
            throw new IllegalArgumentException("Data de check-in não pode ser nula");
        }
        if (checkOut != null && checkIn.after(checkOut)) {
            throw new IllegalArgumentException("Data de check-in não pode ser posterior ao check-out");
        }
        this.checkIn = (Date) checkIn.clone();
        calcularValorTotal();
    }

    public Date getCheckOut() {
        return (Date) checkOut.clone();
    }

    public void setCheckOut(Date checkOut) {
        if (checkOut == null) {
            throw new IllegalArgumentException("Data de check-out não pode ser nula");
        }
        if (checkIn != null && checkIn.after(checkOut)) {
            throw new IllegalArgumentException("Data de check-out não pode ser anterior ao check-in");
        }
        this.checkOut = (Date) checkOut.clone();
        calcularValorTotal();
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        return String.format(
            "Reserva %s: %s - Quarto %d (%s)\nPeríodo: %s a %s\nValor: R$ %.2f\nStatus: %s",
            idReserva,
            usuario.getNome(),
            quarto.getNumero(),
            quarto.getCategoria(),
            new SimpleDateFormat("dd/MM/yyyy").format(checkIn),
            new SimpleDateFormat("dd/MM/yyyy").format(checkOut),
            valorTotal,
            status
        );
    }
}
