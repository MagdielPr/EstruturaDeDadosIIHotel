package SistemaHotelEstrutura2;

import java.util.Date;

public class Validador {
    public static boolean validarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }
        return cpf.matches("\\d{11}");
    }

    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean validarTelefone(String telefone) {
        if (telefone == null || telefone.isEmpty()) {
            return false;
        }
        return telefone.matches("\\d{10,11}");
    }

    public static boolean validarDatas(Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            return false;
        }
        Date hoje = new Date();
        return checkIn.after(hoje) && checkOut.after(checkIn);
    }
}